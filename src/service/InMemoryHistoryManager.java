package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node lastNode;

    private static class Node {
        Task task;
        Node next;
        Node prev;

        Node(Node prev, Task task) { //добавляется только в конец
            this.prev = prev;
            this.next = null;
            this.task = task;
        }
    }

    private void removeNode(Node node) {
        final Node next = node.next;
        final Node prev = node.prev;

        node.task = null;
        if (next == null && prev == null) {
            lastNode = null;
        }
        if (next != null && prev != null) {
            prev.next = next;
            next.prev = prev;
        }
        if (next != null && prev == null) {
            next.prev = null;
        }
        if (next == null && prev != null) {
            lastNode = prev;
            prev.next = null;
        }
    }

    private Node linkLast(Task task) {
        Node newNode;
        if (lastNode == null) {
            newNode = new Node(null, task);
        } else {
            newNode = new Node(lastNode, task);
            lastNode.next = newNode;
        }
        lastNode = newNode; // стал последним
        return newNode;
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> list = new ArrayList<>();

        Node currentNode = lastNode;
        if (currentNode == null) return list;
        while (currentNode.prev != null) {
            list.add(currentNode.task);
            currentNode = currentNode.prev;
        }
        list.add(currentNode.task);
        return list;
    }

    @Override
    public void remove(Integer id) {
        if (historyMap.containsKey(id)) {
            Node node = historyMap.remove(id);
            removeNode(node);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getTaskId())) {
            remove(task.getTaskId());
        }
        historyMap.put(task.getTaskId(), linkLast(task));
    }
}
