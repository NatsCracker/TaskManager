package main.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Duration.ZERO, null);
        this.type = TaskType.EPIC;
        this.subtasks = new ArrayList<>();
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

    public void updateTimeAndDuration(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            setStartTime(null);
            setDuration(Duration.ZERO);
            this.endTime = null;
            return;
        }

        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;
        Duration totalDuration = Duration.ZERO;

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime() != null) {
                if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                    earliestStart = subtask.getStartTime();
                }
                LocalDateTime subtaskEnd = subtask.getEndTime();
                if (latestEnd == null || subtaskEnd.isAfter(latestEnd)) {
                    latestEnd = subtaskEnd;
                }
            }
            if (subtask.getDuration() != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }

        setStartTime(earliestStart);
        setDuration(totalDuration);
        this.endTime = latestEnd;
    }

    // Геттер времени завершения эпика
    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d,%s",
            getId(),
            getType(),
            getName(),
            getStatus(),
            getDescription(),
            getDuration() != null ? getDuration().toMinutes() : 0,
            getStartTime() != null ? getStartTime() : "");
    }
}
