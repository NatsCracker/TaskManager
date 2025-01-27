package main.model;

import java.util.Objects;

public class Task {
    private final int id;
    private String name;
    private String description;
    private TaskStatus status;
    private final TaskType type;

    public Task(String name, String description, TaskType type) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.type = TaskType.TASK;
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
        return String.format("%d,%s,%s,%s,%s", 
            id,
            TaskType.TASK,
            name,
            status,
            description
        );
    }
}
