package main.model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.type = TaskType.SUBTASK;
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
            getType(),
            getName(),
            getStatus(),
            getDescription(),
            epicId
        );
    }
}
