package main.service;

import main.model.Task;

import java.util.List;

public interface HistoryManager {
    // Метод для добавления в историю
    void add(Task task);
    // Метод для получения истории
    List<Task> getHistory();
}
