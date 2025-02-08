package service;

import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.model.TaskStatus;
import main.service.HistoryManager;
import main.service.TaskManager;
import main.util.Managers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import main.service.FileBackedTaskManager;
import main.exception.TaskTimeIntersectionException;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    // Тест 1: Проверяет, что два объекта main.java.model.Task считаются равными, если у них одинаковый ID
    @Test
    void testTaskEqualityById() {
        Task task1 = new Task("Задача 1", "Описание 1", Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Задача 2", "Описание 2", Duration.ofMinutes(30), LocalDateTime.now());
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

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", 1, Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", 1, Duration.ofMinutes(30), LocalDateTime.now());
        subtask1.setId(2);
        subtask2.setId(2);
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковыми ID должны быть равны");
    }

    // Тест 3: Проверяет, что объект main.java.model.Epic не может добавить самого себя в качестве подзадачи
    @Test
    void testEpicCannotAddItselfAsSubtask() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);

        Subtask invalidSubtask = new Subtask("Подзадача", "Описание", epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
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
        Epic epic = new Epic("Эпик", "Описание");
        epic.setId(1);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", 1, Duration.ofMinutes(30), LocalDateTime.now());
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
        LocalDateTime now = LocalDateTime.now();
        
        Task task = new Task("Задача", "Описание", 
            Duration.ofMinutes(30), now);                    // начинается сейчас
            
        Epic epic = new Epic("Эпик", "Описание эпика");     // у эпика нет собственного времени
        
        manager.createTask(task);
        manager.createEpic(epic);
        
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 
            epic.getId(), Duration.ofMinutes(30), 
            now.plusHours(1));                              // начинается через час после task
        
        manager.createSubtask(subtask);

        assertEquals(task, manager.getTaskById(task.getId()), 
            "Задача должна быть найдена по ID после добавления");
        assertEquals(epic, manager.getEpicById(epic.getId()), 
            "Эпик должен быть найден по ID после добавления");
        assertEquals(subtask, manager.getSubtaskById(subtask.getId()), 
            "Подзадача должна быть найдена по ID после добавления");
    }

    // Тест 8: Проверяет, что задачи с назначенными и сгенерированными ID не конфликтуют в менеджере
    @Test
    void testNoConflictBetweenGeneratedAndAssignedIds() {
        TaskManager manager = Managers.getDefault();
        LocalDateTime now = LocalDateTime.now();
        
        Task task1 = new Task("Задача 1", "Описание 1", 
            Duration.ofMinutes(30), now);                    // начинается сейчас
            
        Task task2 = new Task("Задача 2", "Описание 2", 
            Duration.ofMinutes(30), now.plusHours(1));      // начинается через час
            
        task1.setId(1);
        manager.createTask(task1);
        manager.createTask(task2);
        
        assertNotEquals(task1.getId(), task2.getId(), 
            "Назначенные и сгенерированные ID не должны конфликтовать");
    }

    // Тест 9: Проверяет, что задача не изменяется при добавлении в менеджер (по всем полям)
    @Test
    void testTaskImmutabilityOnAddingToManager() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Неизменяемая задача", "Описание", Duration.ofMinutes(30), LocalDateTime.now());
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
        Task task = new Task("Задача для истории", "Описание", Duration.ofMinutes(30), LocalDateTime.now());
        task.setId(1);
        historyManager.add(task);
        Task updatedTask = new Task("Обновленная задача", "Обновленное описание", Duration.ofMinutes(30), LocalDateTime.now());
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
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 1, Duration.ofMinutes(30), LocalDateTime.now());

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
        Task task = new Task("Задача для истории", "Описание", Duration.ofMinutes(30), LocalDateTime.now());
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
        Task task = new Task("Задача для истории", "Описание", Duration.ofMinutes(30), LocalDateTime.now());
        task.setId(1);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "main.java.service.HistoryManager " +
                "должен возвращать список задач из истории просмотра");
    }

    @Test
    void shouldCalculateEpicStatusWhenAllSubtasksNew() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Epic", "Description");
        manager.createEpic(epic);
        
        Subtask subtask1 = new Subtask("Sub1", "Desc1", epic.getId(),
            Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Sub2", "Desc2", epic.getId(),
            Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
            
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void shouldCalculateEpicStatusWhenAllSubtasksDone() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Epic", "Description");
        manager.createEpic(epic);
        
        Subtask subtask1 = new Subtask("Sub1", "Desc1", epic.getId(),
            Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Sub2", "Desc2", epic.getId(),
            Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
            
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void shouldCalculateEpicStatusWhenMixedSubtasks() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Epic", "Description");
        manager.createEpic(epic);
        
        Subtask subtask1 = new Subtask("Sub1", "Desc1", epic.getId(),
            Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Sub2", "Desc2", epic.getId(),
            Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
            
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.NEW);
        
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldCalculateEpicStatusWhenSubtasksInProgress() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Epic", "Description");
        manager.createEpic(epic);
        
        Subtask subtask = new Subtask("Sub", "Desc", epic.getId(),
            Duration.ofMinutes(30), LocalDateTime.now());
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        
        manager.createSubtask(subtask);
        
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldReturnPrioritizedTasks() {
        TaskManager manager = Managers.getDefault();
        LocalDateTime now = LocalDateTime.now();
        
        Task task1 = new Task("Task1", "Desc1", 
            Duration.ofHours(1), now.plusHours(4)); // начинается через 4 часа
            
        Task task2 = new Task("Task2", "Desc2", 
            Duration.ofHours(1), now);              // начинается сейчас
            
        Task task3 = new Task("Task3", "Desc3", 
            Duration.ofHours(1), now.plusHours(2)); // начинается через 2 часа
        
        manager.createTask(task2); // сначала создаем задачу с самым ранним временем
        manager.createTask(task3); // потом со средним
        manager.createTask(task1); // потом с самым поздним
        
        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(3, prioritized.size());
        assertEquals(task2, prioritized.get(0)); // самая ранняя
        assertEquals(task3, prioritized.get(1)); // средняя
        assertEquals(task1, prioritized.get(2)); // самая поздняя
    }

    @Test
    void shouldNotAllowTasksTimeIntersection() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Task1", "Desc1", Duration.ofHours(2), LocalDateTime.now());
        Task task2 = new Task("Task2", "Desc2", Duration.ofHours(2), LocalDateTime.now().plusHours(1));
        
        manager.createTask(task1);
        
        assertThrows(TaskTimeIntersectionException.class, () -> 
            manager.createTask(task2),
            "Задачи не должны пересекаться по времени"
        );
    }

    @Test
    void shouldValidateSubtaskEpicRelation() {
        TaskManager manager = Managers.getDefault();
        Subtask subtask = new Subtask("Sub", "Desc", 999,
            Duration.ofMinutes(30), LocalDateTime.now());
            
        assertThrows(IllegalArgumentException.class, () ->
            manager.createSubtask(subtask),
            "Нельзя создать подзадачу без существующего эпика"
        );
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
        Task task = new Task("Task", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        Epic epic = new Epic("Epic", "Description");
        manager.createTask(task);
        manager.createEpic(epic);
        
        manager.getTaskById(task.getId()); // добавляем в историю
        
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
    }
}