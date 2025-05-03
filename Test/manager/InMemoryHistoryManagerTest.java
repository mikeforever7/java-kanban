package manager;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();
    InMemoryHistoryManager historyManager;
    ArrayList<Task> history;


    @Test
    public void deleteOne_When11TaskAdded() {
        for (int i = 1; i <= 11; i++) {
            taskManager.addTask("Задача", "Описание");
            taskManager.getTask(i);
        }
        history = taskManager.getHistory();
        Task task = history.get(0);
        System.out.println(task);
        assertEquals(2, task.getId()); //Первая задача удалилась, теперь первая с id2
    }
}