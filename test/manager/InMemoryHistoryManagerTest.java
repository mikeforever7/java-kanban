package manager;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager taskManager;

    List<Task> history;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    //Проверяем, что задачи добавляются в порядке их вызова
    @Test
    public void variablesMustBeEqual_WhenAddNewTaskToHistoryWithOrder() {
        for (int i = 1; i <= 5; i++) {
            taskManager.addTask(new Task("Задача", "Описание"));
            taskManager.getTask(i);
        }
        history = taskManager.getHistory();
        Task task = history.get(3);
        assertEquals(4, task.getId()); //Четвертая просмотреная задача совпадет
    }

    @Test
    public void shouldDeleteFromHistory_WhenTaskDeleteFirstTask() {
        for (int i = 1; i <= 5; i++) {
            taskManager.addTask(new Task("Задача", "Описание"));
            taskManager.getTask(i);
        }
        taskManager.deleteTaskById(1); //Удаляем из начала списка
        history = taskManager.getHistory();
        Task firstTask = history.get(0);
        assertEquals(2, firstTask.getId());
    }

    @Test
    public void shouldDeleteFromHistory_WhenTaskDeleteLastTask() {
        for (int i = 1; i <= 5; i++) {
            taskManager.addTask(new Task("Задача", "Описание"));
            taskManager.getTask(i);
        }
        taskManager.deleteTaskById(5); //Удаляем из начала списка
        history = taskManager.getHistory();
        Task lastTask = history.get(3);
        assertEquals(4, history.size());
        assertEquals(4, lastTask.getId());
    }

    @Test
    public void shouldDeleteFromHistory_WhenTaskDeleteMiddleTask() {
        for (int i = 1; i <= 5; i++) {
            taskManager.addTask(new Task("Задача", "Описание"));
            taskManager.getTask(i);
        }
        taskManager.deleteTaskById(3); //Удаляем из начала списка
        history = taskManager.getHistory();
        Task beforeTask = history.get(1);
        Task afterTask = history.get(2);
        assertEquals(2, beforeTask.getId());
        assertEquals(4, afterTask.getId());
    }


    @Test
    public void firstViewTaskMustRemoveFromHistory_WhenGetTaskTwice() {
        for (int i = 1; i <= 5; i++) {
            taskManager.addTask(new Task("Задача", "Описание"));
            taskManager.getTask(i);
        }
        taskManager.getTask(2); // Еще раз просматриваем вторую задачу
        taskManager.getTask(1); // Еще раз просматриваем первую задачу
        history = taskManager.getHistory();
        Task task = history.get(4);
        assertEquals(1, task.getId()); //Первая задача отображается последней
        assertNotEquals(1, history.get(0).getId()); // Первая задача уже с другим ID
        assertEquals(5, history.size()); // Размер списка не изменился,
        // так как дважды просмотренная задача удалена
    }

    @Test
    public void ShouldBeTrue_WhenIsEmpty() {
        history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }
}