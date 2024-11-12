import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private int taskIdCounter = 1;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    // Метод для создания обычной задачи
    public void createTask(Task task) {
        task.setId(taskIdCounter++);
        tasks.put(task.getId(), task);
    }

    // Метод для создания эпика
    public void createEpic(Epic epic) {
        epic.setId(taskIdCounter++);
        epics.put(epic.getId(), epic);
    }

    // Метод для создания подзадачи (добавляем только если есть эпик)
    public void createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Ошибка: нельзя создать подзадачу без эпика.");
            return;
        }

        subtask.setId(taskIdCounter++);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        updateEpicStatus(epic.getId());
    }

    // Метод для получения задачи по ID
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    // Метод для получения эпика по ID
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    // Метод для получения подзадачи по ID
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    // Обновление статуса задачи
    public void updateTask(Task updatedTask) {
        Task existingTask = tasks.get(updatedTask.getId());
        if (existingTask != null) {
            existingTask.setName(updatedTask.getName());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setStatus(updatedTask.getStatus());
            tasks.put(updatedTask.getId(), updatedTask);
        }
    }

    // Обновление статуса подзадачи
    public void updateSubtask(Subtask updatedSubtask) {
        Subtask existingSubtask = subtasks.get(updatedSubtask.getId());
        if (existingSubtask != null) {
            existingSubtask.setName(updatedSubtask.getName());
            existingSubtask.setDescription(updatedSubtask.getDescription());
            existingSubtask.setStatus(updatedSubtask.getStatus());
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
        }
        Epic epic = epics.get(updatedSubtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic.getId());
        }
    }

    // Метод обнавления статуса эпика
    public void updateEpic(Epic updatedEpic) {
        Epic existingEpic = epics.get(updatedEpic.getId());
        if (existingEpic != null) {
            existingEpic.setName(updatedEpic.getName());
            existingEpic.setDescription(updatedEpic.getDescription());
            epics.put(updatedEpic.getId(), updatedEpic);
        }
    }

    // Обновление статуса эпика в зависимости от статусов его подзадач
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (int subtaskId : epic.getSubtasks()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) {
                continue;
            }
            TaskStatus status = subtask.getStatus();
            if (status != TaskStatus.NEW) {
                allNew = false;
            }
            if (status != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    // Удаление задачи по ID
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    // Удаление эпика и его подзадач по ID
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            tasks.remove(id);
            for (int subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    // Удаление подзадачи по ID и обновление статуса эпика
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic.getId());
            }
        }
    }

    // Метод для получения списка всех задач
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Метод для получения всех эпиков
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Метод для получения списка всех подзадач для эпика
    public List<Subtask> getSubtasksForEpic(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                epicSubtasks.add(subtasks.get(subtaskId));
            }
        }
        return epicSubtasks;
    }

    // Метод для получения статуса задачи по ID, больше для удобства вывода
    public String getTaskStatusById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id).getStatus().toString();
        } else if (subtasks.containsKey(id)) {
            return subtasks.get(id).getStatus().toString();
        } else if (epics.containsKey(id)) {
            return epics.get(id).getStatus().toString();
        } else {
            return "Удалена";
        }
    }

    // Метод для удаления всех задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    // Метод для удаления всех эпиков и подзадач
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            for (int subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
            }
        }
        epics.clear();
    }

    // Метод для удаления всех подзадач
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
        }
        subtasks.clear();
    }

    // Метод для удаления всех задач, эпиков и подзадач
    public void deleteAllTasksAndSubtasks() {
        deleteAllTasks();
        deleteAllEpics();
        deleteAllSubtasks();
    }
}




