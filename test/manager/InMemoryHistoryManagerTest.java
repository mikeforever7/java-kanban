package manager;

import model.Task;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();

    List<Task> history;

    @Test
    public void variablesMustBeEqual_WhenAddNewTaskToHistoryWithOrder() {
        for (int i = 1; i <= 5; i++) {
            taskManager.addTask("Задача", "Описание");
            taskManager.getTask(i);
        }
        history = taskManager.getHistory();
        Task task = history.get(3);
        System.out.println(task);
        assertEquals(4, task.getId()); //Четвертая просмотреная задача совпадет
    }

    @Test
    public void firstViewTaskMustRemoveFromHistory_WhenGetTaskTwice() {
        for (int i = 1; i <= 5; i++) {
            taskManager.addTask("Задача", "Описание");
            taskManager.getTask(i);
        }
        taskManager.getTask(2); // Еще раз просматриваем вторую задачу
        taskManager.getTask(1); // Еще раз просматриваем первую задачу
        history = taskManager.getHistory();
        Task task = history.get(4);
        System.out.println(task);
        assertEquals(1, task.getId()); //Первая задача отображается последней
        assertNotEquals(1, history.get(0).getId()); // Первая задача уже с другим ID
        assertEquals(5, history.size()); // Размер списка не изменился,
        // так как дважды просмотренная задача удалена

    }

}