import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, description);
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

    @Override
    public String toString() {
        return "Задача: "+ getName() +
                ", описание: " + getDescription() +
                ", статус: " + getStatus() +
                " количество подзадач: " + subtasksId.size();
    }
}
