package main.service;

import main.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> history = new ArrayList<>();

    // Метод для добавления в историю
    @Override
    public void add(Task task) {
        history.add(task);
        if(history.size() > 10){
            history.removeFirst();
        }
    }

    // Метод для получения истории
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

}
