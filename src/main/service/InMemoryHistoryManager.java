package main.service;

import main.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;
    private int id = 0;

    // Метод для добавления в историю
    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }

        Node node = new Node(task);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        historyMap.put(task.getId(), node);
    }

    // Метод для получения истории в ArrayList
    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    // Удаление задачи из истории
    @Override
    public void remove(int id) {
        Node node = historyMap.get(id);
        if (node != null){
            removeNode(node);
            historyMap.remove(id);
        }
    }

    // Метод удаления ноды
    private void removeNode(Node node){
        if (node.prev != null){
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null){
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    // Метод для получения ноды по id
    private Node getNode(int id){
        return historyMap.get(id);
    }

     private class Node {
        private Task task;
        private Node next;
        private Node prev;

        Node(Task task){
            this.task = task;
        }
     }

}
