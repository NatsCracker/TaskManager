package main.service;

import main.model.Epic;
import main.model.Subtask;
import main.model.Task;

import java.util.List;

public interface TaskManager {
    // Метод для создания обычной задачи
    void createTask(Task task);

    // Метод для создания эпика
    void createEpic(Epic epic);

    // Метод для создания подзадачи (добавляем только если есть эпик)
    void createSubtask(Subtask subtask);

    // Метод для получения задачи по ID
    Task getTaskById(int id);

    // Метод для получения эпика по ID
    Epic getEpicById(int id);

    // Метод для получения подзадачи по ID
    Subtask getSubtaskById(int id);

    // Обновление статуса задачи
    void updateTask(Task updatedTask);

    // Обновление статуса подзадачи
    void updateSubtask(Subtask updatedSubtask);

    // Метод обнавления статуса эпика
    void updateEpic(Epic updatedEpic);

    // Удаление задачи по ID
    void deleteTaskById(int id);

    // Удаление эпика и его подзадач по ID
    void deleteEpicById(int id);

    // Удаление подзадачи по ID и обновление статуса эпика
    void deleteSubtaskById(int id);

    // Метод для получения списка всех задач
    List<Task> getAllTasks();

    // Метод для получения всех эпиков
    List<Epic> getAllEpics();

    // Метод получения всех подзадач
    List<Subtask> getAllSubtasks();

    // Метод для получения списка всех подзадач для эпика
    List<Subtask> getSubtasksForEpic(int epicId);

    // Метод для удаления всех задач
    void deleteAllTasks();

    // Метод для удаления всех эпиков и подзадач
    void deleteAllEpics();

    // Метод для удаления всех подзадач
    void deleteAllSubtasks();

    // Метод для получения истории просмотров задач
    List<Task> getHistory();
}
