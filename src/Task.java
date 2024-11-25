import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
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
        return "Задача: " + name + ", описание: " + description + ", статус: " + status;
    }
}
