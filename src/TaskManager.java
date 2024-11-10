import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int taskIdCounter = 1;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    // Метод для создания обычной задачи
    public void createTask(Task task) {
        task.setId(taskIdCounter++);
        tasks.put(task.getId(), task);
    }

    // Метод для создания эпика
    public void createEpic(Epic epic) {
        epic.setId(taskIdCounter++);
        tasks.put(epic.getId(), epic);
    }

    // Метод для создания подзадачи
    public void createSubtask(Subtask subtask) {
        subtask.setId(taskIdCounter++);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = (Epic) tasks.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic.getId());
        }
    }

    // Метод для получения задачи по ID
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    // Метод для получения эпика по ID
    public Epic getEpicById(int id) {
        return (Epic) tasks.get(id);
    }

    // Метод для получения подзадачи по ID
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    // Обновление статуса задачи
    public void updateTaskStatus(int id, TaskStatus status) {
        Task task = tasks.get(id);
        if (task != null) {
            task.setStatus(status);
        }
    }

    // Обновление статуса подзадачи и пересчет статуса эпика
    public void updateSubtaskStatus(int id, TaskStatus status) {
        Subtask subtask = subtasks.get(id);

        if (subtask != null) {
            subtask.setStatus(status);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    // Обновление статуса эпика в зависимости от статусов его подзадач
    private void updateEpicStatus(int epicId) {
        Epic epic = (Epic) tasks.get(epicId);

        if (epic == null) return;

        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        ArrayList<Integer> subtaskIds = epic.getSubtasks();
        boolean allNew = true;
        boolean allDone = true;

        for (int subtaskId : subtaskIds) {
            TaskStatus status = subtasks.get(subtaskId).getStatus();
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

    // Метод для получения списка всех задач
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Метод для получения списка всех подзадач эпика
    public ArrayList<Subtask> getSubtasksForEpic(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = (Epic) tasks.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                epicSubtasks.add(subtasks.get(subtaskId));
            }
        }
        return epicSubtasks;
    }

    // Удаление задачи по ID
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    // Удаление эпика и его подзадач по ID
    public void deleteEpicById(int id) {
        Epic epic = (Epic) tasks.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    // Удаление подзадачи по ID и обновление статуса эпика
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = (Epic) tasks.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic.getId());
            }
        }
    }

    // Удаление всех задач, эпиков и подзадач
    public void deleteAllTasksAndSubtasks() {
        tasks.clear();
        subtasks.clear();
    }
}




