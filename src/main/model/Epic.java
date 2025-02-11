package main.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Duration.ZERO, null);
        this.type = TaskType.EPIC;
        this.subtasksId = new ArrayList<>();
    }

    //  Геттер id
    public int getEpicId() {
        return id;
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

    // Метод для изменения времени завершения
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    // Геттер времени завершения эпика
    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d,%s", getId(), getType(), getName(), getStatus(), getDescription(), getDuration() != null ? getDuration().toMinutes() : 0, getStartTime() != null ? getStartTime() : "");
    }
}
