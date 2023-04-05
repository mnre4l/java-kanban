import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();

    public static class Node {
        Task task;
        static Node lastNode;
        Node next;
        Node prev;

        Node(Node prev, Task task) { //добавляется только в конец
            this.prev = prev;
            this.next = null;
            this.task = task;
        }

        public static void removeNode(Node node) {
            final Node next = node.next;
            final Node prev = node.prev;

            if (next == null && prev != null) {
                lastNode = prev;
                prev.next = null;
            } else if (prev == null && next != null) {
                next.prev = null;
            } else if (prev == null && next == null) {
                lastNode = null;
            } else {
                next.prev = prev;
                prev.next = next;
            }

            node.task = null;
        }

        public static Node linkLast(Task task) {
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

        public static ArrayList<Task> getTasks() {
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
    }

    @Override
    public void remove(Integer id) {
        Node node = historyMap.remove(id);
        Node.removeNode(node);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return Node.getTasks();
    }

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getTaskId())) {
            remove(task.getTaskId());
        }
        historyMap.put(task.getTaskId(), Node.linkLast(task));
    }
}
