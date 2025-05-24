package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;

    public void linkLast(Task task) {
        Node<Task> oldTail = tail;
        Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        historyMap.put(task.getId(), newNode);
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        size++;
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> element = head;
        while (element != null) {
            tasks.add(element.task);
            element = element.next;
        }
        return tasks;
    }

    public void remove(int id) {
        removeNode(historyMap.get(id));
    }

    public void removeNode(Node<Task> node) {
        if (!(node == null)) {
            final Node<Task> next = node.next;
            final Node<Task> prev = node.prev;
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
            size--;
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    @Override
    public void printHistory() {
        List<Task> history = getHistory();
        if (history.isEmpty()) {
            System.out.println("История просмотров пуста.");
            return;
        }
        System.out.println("История просмотров:");
        int index = 1;
        for (Task task : history) {
            System.out.println(index + ". " + task);
            index++;
        }
    }

    public class Node<Task> {
        public Task task;
        public Node<Task> next;
        public Node<Task> prev;

        public Node(Node<Task> prev, Task task, Node<Task> next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }
}
