package main.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, description);
        this.type = TaskType.EPIC;
        this.subtasksId = new ArrayList<>();
    }

    // Метод для добавления подзадачи
    public void addSubtask(int subtaskId) {
        if (subtaskId != this.getId()) {
            subtasksId.add(subtaskId);
        }
    }

    // Метод для получения списка id подзад
    public List<Integer> getSubtasks() {
        return new ArrayList<>(subtasksId);
    }

    // Метод для удаления подзадачи
    public void removeSubtask(int subtaskId) {
        subtasksId.remove(Integer.valueOf(subtaskId));
    }

    // Метод для удаления всех подзадач
    public void removeAllSubtasks() {
        subtasksId.clear();
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s",
            getId(),
            getType(),
            getName(),
            getStatus(),
            getDescription());
    }
}
