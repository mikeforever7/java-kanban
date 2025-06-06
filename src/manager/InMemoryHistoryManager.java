package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    public void linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        historyMap.put(task.getId(), newNode);
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node element = head;
        while (element != null) {
            tasks.add(element.task);
            element = element.next;
        }
        return tasks;
    }

    public void remove(int id) {
        removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    public void removeNode(Node node) {
        if (!(node == null)) {
            final Node next = node.next;
            final Node prev = node.prev;
            node.task = null;
            if (head == node && tail == node) {
                tail = null;
                head = null;
            } else if (head == node && !(tail == node)) {
                head = next;
                head.prev = null;
            } else if (!(head == node) && tail == node) {
                tail = prev;
                tail.next = null;
            } else {
                next.prev = prev;
                prev.next = next;
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (!(task == null)) {
            remove(task.getId());
            linkLast(task);
        }
    }

    public class Node {
        public Task task;
        public Node next;
        public Node prev;

        public Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }
}
