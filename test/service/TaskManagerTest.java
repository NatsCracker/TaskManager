package service;

import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.service.HistoryManager;
import main.service.TaskManager;
import main.util.Managers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import main.service.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    // Тест 1: Проверяет, что два объекта main.java.model.Task считаются равными, если у них одинаковый ID
    @Test
    void testTaskEqualityById() {
        Task task1 = new Task("Задача 1", "Описание 1");
        Task task2 = new Task("Задача 2", "Описание 2");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2, "Задачи с одинаковыми ID должны быть равны");
    }

    // Тест 2: Проверяет, что наследники класса main.java.model.Task
    // (например, main.java.model.Epic, main.java.model.Subtask) считаются равными, если у них одинаковый ID
    @Test
    void testTaskSubclassesEqualityById() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        epic1.setId(1);
        epic2.setId(1);
        assertEquals(epic1, epic2, "Эпики с одинаковыми ID должны быть равны");

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", 1);
        subtask1.setId(2);
        subtask2.setId(2);
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковыми ID должны быть равны");
    }

    // Тест 3: Проверяет, что объект main.java.model.Epic не может добавить самого себя в качестве подзадачи
    @Test
    void testEpicCannotAddItselfAsSubtask() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Эпик задача", "Описание эпика");
        manager.createEpic(epic);

        Subtask invalidSubtask = new Subtask("Подзадача", "Описание", epic.getId());
        invalidSubtask.setId(epic.getId());

        manager.createSubtask(invalidSubtask);

        List<Integer> subtasks = epic.getSubtasks();
        assertFalse(subtasks.contains(invalidSubtask.getId()),
                "Эпик не должен содержать подзадачу, указывающую на него самого.");

        assertNull(manager.getSubtaskById(invalidSubtask.getId()),
                "Некорректная подзадача не должна быть добавлена в менеджер задач.");
    }

    // Тест 4: Проверяет, что подзадача не может быть своим же эпиком
    @Test
    void testSubtaskCannotBeItsOwnEpic() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Эпик", "Описание эпика");
        epic.setId(1);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", 1);
        manager.createSubtask(subtask);
        assertNotEquals(subtask.getId(), epic.getId(), "Подзадача не может быть своим собственным эпиком");
    }

    // Тест 5: Проверяет, что утилитарный класс возвращает проинициализированный и готовый к работе экземпляр
    // main.java.service.TaskManager
    @Test
    void testUtilityClassReturnsInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "main.java.service.TaskManager должен быть инициализирован");
    }

    // Тест 6: Проверяет, что утилитарный класс возвращает проинициализированный и готовый к работе экземпляр
    // main.java.service.HistoryManager
    @Test
    void testUtilityClassReturnsInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "main.java.service.HistoryManager должен быть инициализирован");
    }

    // Тест 7: Проверяет, что main.java.service.InMemoryTaskManager добавляет задачи разных типов и может найти их по ID
    @Test
    void testInMemoryTaskManagerAddAndFindTasks() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Задача", "Описание");
        Epic epic = new Epic("Эпик", "Описание эпика");
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 2);

        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask);

        assertEquals(task, manager.getTaskById(1), "Задача должна быть найдена по ID после добавления");
        assertEquals(epic, manager.getEpicById(2), "Эпик должен быть найден по ID после добавления");
        assertEquals(subtask, manager.getSubtaskById(3), "Подзадача должна быть найдена по ID после добавления");
    }

    // Тест 8: Проверяет, что задачи с назначенными и сгенерированными ID не конфликтуют в менеджере
    @Test
    void testNoConflictBetweenGeneratedAndAssignedIds() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Описание 1");
        Task task2 = new Task("Задача 2", "Описание 2");
        task1.setId(1);
        manager.createTask(task1);
        manager.createTask(task2);
        assertNotEquals(task1.getId(), task2.getId(), "Назначенные и сгенерированные ID не должны конфликтовать");
    }

    // Тест 9: Проверяет, что задача не изменяется при добавлении в менеджер (по всем полям)
    @Test
    void testTaskImmutabilityOnAddingToManager() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Неизменяемая задача", "Описание");
        task.setId(1);
        manager.createTask(task);
        Task retrievedTask = manager.getTaskById(1);
        assertEquals(task.getName(), retrievedTask.getName(), "Имя задачи должно оставаться неизменным");
        assertEquals(task.getDescription(), retrievedTask.getDescription(), "Описание задачи должно оставаться неизменным");
    }

    // Тест 10: Проверяет, что main.java.service.HistoryManager сохраняет последнюю версию задачи при добавлении в историю
    @Test
    void testHistoryManagerPreservesTaskVersions() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Задача для истории", "Описание");
        task.setId(1);
        historyManager.add(task);
        Task updatedTask = new Task("Обновленная задача", "Обновленное описание");
        updatedTask.setId(1);
        historyManager.add(updatedTask);
        assertEquals(1, historyManager.getHistory().size(), "main.java.service.HistoryManager " +
                "должен сохранять обе версии задачи");
    }

    // Тест 11: Проверяет, что все подзадачи удаляются при удалении всех подзадач
    @Test
    void testDeleteAllSubtasks() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Эпик", "Описание");
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 1);

        manager.createEpic(epic);
        manager.createSubtask(subtask);
        manager.deleteAllSubtasks();

        assertEquals(0, epic.getSubtasks().size(), "Эпик должен содержать пустой список подзадач" +
                " после удаления всех подзадач");
    }

     @Test
    // Тест 12: Проверяет что main.java.service.HistoryManager корректно удаляет из истории просмотра при удалении задачи
    void testHistoryManagerDeletesTaskFromHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Задача для истории", "Описание");
        task.setId(1);
        historyManager.add(task);
        historyManager.remove(1);
        assertEquals(0, historyManager.getHistory().size(), "main.java.service.HistoryManager " +
                "должен удалять задачу из истории просмотра при ее удалении");
    }

    @Test
    // Тест 13: Проверяет, что main.java.service.HistoryManager getHistory возвращает список
    void testGetHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Задача для истории", "Описание");
        task.setId(1);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "main.java.service.HistoryManager " +
                "должен возвращать список задач из истории просмотра");
    }
}

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void shouldSaveAndLoadEmptyTaskManager() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(0, loadedManager.getAllTasks().size());
        assertEquals(0, loadedManager.getAllEpics().size());
        assertEquals(0, loadedManager.getAllSubtasks().size());
    }

    @Test
    void shouldSaveAndLoadTasksWithHistory() {
        Task task = new Task("Task", "Description");
        Epic epic = new Epic("Epic", "Description");
        manager.createTask(task);
        manager.createEpic(epic);
        
        manager.getTaskById(task.getId()); // добавляем в историю
        
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getHistory().size());
    }
}
