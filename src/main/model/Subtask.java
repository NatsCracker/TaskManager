package main.model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, TaskType type, int epicId) {
        super(name, description, TaskType.SUBTASK);
        this.epicId = epicId;
    }

    // Метод для получения id эпика
    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d",
            getId(),
            TaskType.SUBTASK,
            getName(),
            getStatus(),
            getDescription(),
            epicId
        );
    }
}
