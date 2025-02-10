package main;

import main.model.Epic;
import main.model.Subtask;
import main.service.TaskManager;
import main.util.Managers;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Epic epic = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");

        manager.createEpic(epic);
        manager.createEpic(epic2);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId());
        manager.createSubtask(subtask);
        manager.createSubtask(subtask2);

        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());
        manager.getEpicById(epic2.getId());
        manager.getSubtaskById(subtask2.getId());

        System.out.println(manager.getHistory());

        manager.deleteSubtaskById(subtask.getId());

        System.out.println(manager.getHistory());

        manager.deleteEpicById(epic.getId());

        System.out.println(manager.getHistory());

    }
}
