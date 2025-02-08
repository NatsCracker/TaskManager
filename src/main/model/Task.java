package main.model;

import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    protected int id;
    private String name;
    private String description;
    private TaskStatus status;
    protected TaskType type;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.type = TaskType.TASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    // Геттер id задачи
    public int getId() {
        return id;
    }

    // Сеттер id задачи
    public void setId(int id) {
        this.id = id;
    }

    // Геттер имени задачи
    public String getName() {
        return name;
    }

    // Сеттер имени задачи
    public void setName(String name) {
        this.name = name;
    }

    // Геттер описания задачи
    public String getDescription() {
        return description;
    }

    // Сеттер описания задачи
    public void setDescription(String description) {
        this.description = description;
    }

    // Геттер статуса задачи
    public TaskStatus getStatus() {
        return status;
    }

    // Сеттер статуса задачи
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    // Геттер типа задачи
    public TaskType getType() {
        return type;
    }

    // Геттер времени даты и времени завершения задачи
    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    // Геттер времени начала задачи
    public LocalDateTime getStartTime() {
        return startTime;
    }

    // Геттер продолжительности задачи
    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public boolean hasTimeIntersection(Task other) {
        if (this.startTime == null || other.startTime == null) {
            return false;
        }
        
        LocalDateTime thisEnd = this.getEndTime();
        LocalDateTime otherEnd = other.getEndTime();
        
        return !this.startTime.isAfter(otherEnd) && !thisEnd.isBefore(other.startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d,%s",
            id,
            type,
            name,
            status,
            description,
            duration != null ? duration.toMinutes() : 0,
            startTime != null ? startTime : "");
    }
}
