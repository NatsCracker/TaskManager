package main.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.type = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    // Метод для получения id эпика
    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d,%s,%d",
            getId(),
            getType(),
            getName(),
            getStatus(),
            getDescription(),
            getDuration() != null ? getDuration().toMinutes() : 0,
            getStartTime() != null ? getStartTime() : "",
            epicId);
    }
}
