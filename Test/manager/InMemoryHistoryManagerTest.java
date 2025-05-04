package manager;

import model.Task;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();
    /*В уроках особо не было про Collection и List. Если я правильно понял, здесь Collection я сделать
    * не могу. Так как дальше мне требуется метод get(index). Поэтому сделал List везде*/
    List<Task> history;

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