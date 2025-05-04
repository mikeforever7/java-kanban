package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> history = new ArrayList<>();
    private static final int HISTORY_LIST_SIZE = 10;

    @Override
    public List<Task> getHistory() {
        System.out.println("Последние 10 просмотров:");
        int i =1;
        for (Task task : history) {
            System.out.println(i + ". " + task);
            i++;
        }
        return history;
    }

    @Override
    public void add(Task task) {
        if (history.size() == HISTORY_LIST_SIZE) {
            history.remove(0);
        }
        history.add(task);
    }
}
