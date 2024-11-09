import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int taskIdCounter = 1;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private ArrayList<Epic> allEpics = new ArrayList<>();
    private ArrayList<Task> allTasks = new ArrayList<>();
    private ArrayList<Subtask> allSubtasks = new ArrayList<>();

    public void createTask(Task task) {
        task.setId(taskIdCounter++);
        tasks.put(task.getId(), task);
        allTasks.add(task);
    }

    public void createEpic(Epic epic) {
        epic.setId(taskIdCounter++);
        epics.put(epic.getId(), epic);
        allEpics.add(epic);
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(taskIdCounter++);
        subtasks.put(subtask.getId(), subtask);
        allSubtasks.add(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask);
            epic.updateEpicStatus();
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void updateTaskStatus(int id, TaskStatus status) {
        Task task = tasks.get(id);
        if (task != null) {
            task.setStatus(status);
            if (task instanceof Epic) {
                ((Epic) task).updateEpicStatus();
            }
        }

        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            subtask.setStatus(status);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.updateEpicStatus();
            }
        }
    }

    public ArrayList<Task> getAllTasks() {
        return allTasks;
    }

    public ArrayList<Epic> getAllEpics() {
        return allEpics;
    }

    public ArrayList<Subtask> getSubtasksForEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic != null ? epic.getSubtasks() : new ArrayList<>();
    }

    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
            allSubtasks.removeAll(epic.getSubtasks());
        }
        allTasks.remove(task);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
                epic.updateEpicStatus();
            }
        }
        allSubtasks.remove(subtask);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
            allSubtasks.removeAll(epic.getSubtasks());
        }
        allEpics.remove(epic);
    }
}





