import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksId = new ArrayList<>();
    }

    public void addSubtask(int subtaskId) {
        subtasksId.add(subtaskId);
    }

    public ArrayList<Integer> getSubtasks() {
        return new ArrayList<>(subtasksId);
    }

    public void removeSubtask(int subtaskId) {
        subtasksId.remove(Integer.valueOf(subtaskId));
    }

    // Метод обновления статуса эпика на основе статусов его подзадач
    public void updateEpicStatus(HashMap<Integer, Subtask> subtasks) {
        if (subtasksId.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean anyInProgress = false;

        for (Integer subtaskId : subtasksId) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                    anyInProgress = true;
                }
                if (subtask.getStatus() != TaskStatus.DONE) {
                    allDone = false;
                }
            }
        }

        if (allDone) {
            setStatus(TaskStatus.DONE);
        } else if (anyInProgress) {
            setStatus(TaskStatus.IN_PROGRESS);
        } else {
            setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public String toString() {
        return "Задача: "+ getName() +
                ", описание: " + getDescription() +
                ", статус: " + getStatus() +
                " количество подзадач:" + subtasksId.size();
    }
}
