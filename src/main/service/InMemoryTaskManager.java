package main.service;

import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.model.TaskStatus;
import main.util.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int taskIdCounter = 1;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    public HistoryManager history = Managers.getDefaultHistory();

    // Метод для создания обычной задачи
    @Override
    public void createTask(Task task) {
        task.setId(taskIdCounter++);
        tasks.put(task.getId(), task);
    }

    // Метод для создания эпика
    @Override
    public void createEpic(Epic epic) {
        epic.setId(taskIdCounter++);
        epics.put(epic.getId(), epic);
    }

    // Метод для создания подзадачи (добавляем только если есть эпик)
    @Override
    public void createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Ошибка: нельзя создать подзадачу без эпика.");
            return;
        }

        if (subtask.getEpicId() == subtask.getId()) {
            System.out.println("Ошибка: подзадача не может ссылаться на эпик, являющийся самим собой.");
            return;
        }

        subtask.setId(taskIdCounter++);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        updateEpicStatus(epic.getId());
    }

    // Метод для получения задачи по ID
    @Override
    public Task getTaskById(int id) {
        if (tasks.get(id) != null) {
            history.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    // Метод для получения эпика по ID
    @Override
    public Epic getEpicById(int id) {
        if (epics.get(id) != null) {
            history.add(epics.get(id));
        }
        return epics.get(id);
    }

    // Метод для получения подзадачи по ID
    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.get(id) != null) {
            history.add(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    // Обновление статуса задачи
    @Override
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
    @Override
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
    @Override
    public void updateEpic(Epic updatedEpic) {
        Epic existingEpic = epics.get(updatedEpic.getId());
        if (existingEpic != null) {
            existingEpic.setName(updatedEpic.getName());
            existingEpic.setDescription(updatedEpic.getDescription());
            epics.put(updatedEpic.getId(), updatedEpic);
        }
    }

    // Обновление статуса эпика в зависимости от статусов его подзадач
    public void updateEpicStatus(int epicId) {
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
    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        history.remove(id);
    }

    // Удаление эпика и его подзадач по ID
    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        history.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
                history.remove(subtaskId);
            }
        }
    }

    // Удаление подзадачи по ID и обновление статуса эпика
    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        history.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic.getId());
            }
        }
    }

    // Метод для получения списка всех задач
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Метод для получения всех эпиков
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // Метод получения всех подзадач
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Метод для получения списка всех подзадач для эпика
    @Override
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
    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            history.remove(task.getId());
        }
        tasks.clear();
    }

    // Метод для удаления всех эпиков и подзадач
    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        for (Epic epic : epics.values()) {
            history.remove(epic.getId());
        }
        epics.clear();
    }

    // Метод для удаления всех подзадач
    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            history.remove(subtask.getId());
        }
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
        }
        subtasks.clear();
    }

    public List<Task> getHistory() {
        return history.getHistory();
    }

}




