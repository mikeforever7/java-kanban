package manager;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> history = new ArrayList<>();



    @Override
    public ArrayList<Task> getHistory() {
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
        if (history.size() == 10) {
            history.remove(0);
        }
        history.add(task);
    }
}
