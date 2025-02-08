package main.service;

import main.exception.TaskTimeIntersectionException;
import main.model.*;
import main.util.Managers;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int taskIdCounter = 1;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    public HistoryManager history = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.prioritizedTasks = new TreeSet<>(new TaskStartTimeComparator());
    }

    // Метод для создания обычной задачи
    @Override
    public void createTask(Task task) {
        validateTaskTime(task);
        task.setId(taskIdCounter++);
        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);
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
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Нельзя создать подзадачу без существующего эпика");
        }
        if (subtask.getEpicId() == subtask.getId()) {
            return;
        }
        validateTaskTime(subtask);
        subtask.setId(taskIdCounter++);
        subtasks.put(subtask.getId(), subtask);
        Optional.ofNullable(epics.get(subtask.getEpicId()))
                .ifPresent(epic -> {
                    epic.addSubtask(subtask.getId());
                    updateEpicStatus(epic);
                });

        addToPrioritizedTasks(subtask);
    }

    // Метод для получения задачи по ID
    @Override
    public Task getTaskById(int id) {
        return Optional.ofNullable(tasks.get(id))
                .map(task -> {
                    history.add(task);
                    return task;
                })
                .orElse(null);
    }

    // Метод для получения эпика по ID
    @Override
    public Epic getEpicById(int id) {
        return Optional.ofNullable(epics.get(id))
                .map(epic -> {
                    history.add(epic);
                    return epic;
                })
                .orElse(null);
    }

    // Метод для получения подзадачи по ID
    @Override
    public Subtask getSubtaskById(int id) {
        return Optional.ofNullable(subtasks.get(id))
                .map(subtask -> {
                    history.add(subtask);
                    return subtask;
                })
                .orElse(null);
    }

    // Обновление статуса задачи
    @Override
    public void updateTask(Task task) {
        validateTaskTime(task);
        Optional.ofNullable(tasks.get(task.getId()))
                .ifPresent(this::removeFromPrioritizedTasks);

        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);
    }

    // Обновление статуса подзадачи
    @Override
    public void updateSubtask(Subtask subtask) {
        validateTaskTime(subtask);
        Optional.ofNullable(subtasks.get(subtask.getId()))
                .ifPresent(this::removeFromPrioritizedTasks);

        subtasks.put(subtask.getId(), subtask);

        Optional.ofNullable(epics.get(subtask.getEpicId()))
                .ifPresent(this::updateEpicStatus);

        addToPrioritizedTasks(subtask);

    }

    // Метод обнавления статуса эпика
    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    // Обновление статуса эпика в зависимости от статусов его подзадач
    @Override
    public void updateEpicStatus(Epic epic) {
        List<TaskStatus> subtaskStatuses = getSubtasksForEpic(epic.getId()).stream()
                .map(Task::getStatus)
                .collect(Collectors.toList());

        if (subtaskStatuses.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (subtaskStatuses.stream().allMatch(status -> status == TaskStatus.NEW)) {
            epic.setStatus(TaskStatus.NEW);
        } else if (subtaskStatuses.stream().allMatch(status -> status == TaskStatus.DONE)) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    // Удаление задачи по ID
    @Override
    public void deleteTaskById(int id) {
        Optional.ofNullable(tasks.get(id))
                .ifPresent(this::removeFromPrioritizedTasks);

        tasks.remove(id);
        history.remove(id);
    }

    // Удаление эпика и его подзадач по ID
    @Override
    public void deleteEpicById(int id) {
        Optional.ofNullable(epics.remove(id))
                .ifPresent(epic -> epic.getSubtasks().forEach(subtaskId -> {
                    subtasks.remove(subtaskId);
                    history.remove(subtaskId);
                }));

        history.remove(id);
    }

    // Удаление подзадачи по ID и обновление статуса эпика
    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            removeFromPrioritizedTasks(subtask);
        }
        history.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic);
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
        return Optional.ofNullable(epics.get(epicId))
                .map(epic -> epic.getSubtasks().stream()
                        .map(subtasks::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
    }

    private void validateTaskTime(Task task) {
        if (task.getStartTime() == null) return;

        boolean hasIntersection = getPrioritizedTasks().stream()
                .filter(existingTask -> !existingTask.equals(task))
                .anyMatch(existingTask -> {
                    if (existingTask.getStartTime() == null) return false;

                    LocalDateTime newTaskStart = task.getStartTime();
                    LocalDateTime newTaskEnd = task.getEndTime();
                    LocalDateTime existingTaskStart = existingTask.getStartTime();
                    LocalDateTime existingTaskEnd = existingTask.getEndTime();

                    return !(newTaskEnd.isBefore(existingTaskStart) ||
                            newTaskStart.isAfter(existingTaskEnd));
                });

        if (hasIntersection) {
            throw new TaskTimeIntersectionException(
                    "Задача пересекается по времени с уже существующей задачей: " + task);
        }

    }

}





