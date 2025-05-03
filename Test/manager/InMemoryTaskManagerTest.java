package manager;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void variablesMustBeEqual_WhenAddNewTask() {
        taskManager.addTask("Первая задача", "Описание");
        Task savedTask = taskManager.getTask(1);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(1, savedTask.getId(), "Задачи не совпадают");
        assertEquals("Первая задача", savedTask.getName(), "Задачи не совпадают");
        assertEquals("Описание", savedTask.getDescription(), "Задачи не совпадают");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статусы не совпадают");
    }

    @Test
    public void variablesMustBeEqual_WhenAddNewEpic() {
        taskManager.addEpic("Первый эпик", "Описание");
        Epic savedEpic = taskManager.getEpic(1);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(1, savedEpic.getId(), "Эпики не совпадают");
        assertEquals("Первый эпик", savedEpic.getName(), "Эпики не совпадают");
        assertEquals("Описание", savedEpic.getDescription(), "Эпики не совпадают");
        assertEquals(TaskStatus.NEW, savedEpic.getStatus(), "Статусы не совпадают");
    }

    @Test
    public void variablesMustBeEqual_WhenAddNewSubtask() {
        taskManager.addEpic("Эпик", "Для подзадачи");   // id эпика = 1
        taskManager.addSubtask("Первая подзадача", "Описание", 1); // id подзадачи = 2
        Subtask savedSubtask = taskManager.getSubtask(2);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(2, savedSubtask.getId(), "Подзадачи не совпадают");
        assertEquals("Первая подзадача", savedSubtask.getName(), "Подзадачи не совпадают");
        assertEquals("Описание", savedSubtask.getDescription(), "Подзадачи не совпадают");
        assertEquals(TaskStatus.NEW, savedSubtask.getStatus(), "Статусы не совпадают");
    }

    @Test
    public void shouldReturnCorrectSizeWhenElementsAreAddedToHashMap() {
        taskManager.addTask("Задача первая", "Описание");
        taskManager.addTask("Задача вторая", "Описание");
        Task savedTask = taskManager.getTask(2);
        HashMap<Integer, Task> tasks = taskManager.getTasks();
        assertNotNull(tasks.get(1), "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач");
        assertEquals(savedTask, tasks.get(2), "Задачи не равны");
    }

    @Test
    public void null_WhenTaskDeleted() {
        taskManager.addTask("Задача первая", "Описание");
        Task task = taskManager.getTask(1);
        assertNotNull(task);  //Проверка, что задача существует
        taskManager.deleteTaskById(1);
        assertNull(taskManager.getTask(1));  //Проверка, что задача удалена
    }

    @Test
    public void null_WhenEpicDeleted() {
        taskManager.addEpic("Эпик", "Описание");
        Epic epic = taskManager.getEpic(1);
        assertNotNull(epic);  //Проверка, что задача существует
        taskManager.deleteEpicById(1);
        assertNull(taskManager.getEpic(1));  //Проверка, что задача удалена
    }

    @Test
    public void null_WhenSubtaskDeleted() {
        taskManager.addEpic("Эпик", "для подзадачи");
        taskManager.addSubtask("Задача первая", "Описание", 1);
        Subtask subtask = taskManager.getSubtask(2);
        assertNotNull(subtask);  //Проверка, что задача существует
        taskManager.deleteSubtaskById(2);
        assertNull(taskManager.getSubtask(2));  //Проверка, что задача удалена
    }

    @Test
    public void null_WhenAllTasksDeleted() {
        taskManager.addTask("Задача первая", "Описание");
        Task task = taskManager.getTask(1);
        assertNotNull(task);  //Проверка, что задача существует
        taskManager.deleteAllTasks();
        assertNull(taskManager.getTask(1));  //Проверка, что задача удалена
    }

    @Test
    public void null_WhenAllEpicsDeleted() {
        taskManager.addEpic("Эпик", "Описание");
        Epic epic = taskManager.getEpic(1);
        assertNotNull(epic);  //Проверка, что задача существует
        taskManager.deleteAllEpics();
        assertNull(taskManager.getEpic(1));  //Проверка, что задача удалена
    }

    @Test
    public void null_WhenAllSubtasksDeleted() {
        taskManager.addEpic("Эпик", "для подзадачи");
        taskManager.addSubtask("Задача первая", "Описание", 1);
        Subtask subtask = taskManager.getSubtask(2);
        assertNotNull(subtask);  //Проверка, что задача существует
        taskManager.deleteAllSubtasks();
        assertNull(taskManager.getSubtask(2));  //Проверка, что задача удалена
    }

    @Test
    public void shouldEqual_WhenTaskUpdated() {
        taskManager.addTask("Задача первая", "Описание");
        Task task = taskManager.getTask(1);
        taskManager.updateTask(1, "Задача изменена", "Описание", TaskStatus.IN_PROGRESS);
        assertEquals("Задача изменена", task.getName());
        assertEquals("Описание", task.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    public void shouldEqual_WhenSubTaskUpdated() {
        taskManager.addEpic("Эпик", "Для подзадачи");
        taskManager.addSubtask("Задача первая", "Описание", 1);
        Subtask subtask = taskManager.getSubtask(2);
        taskManager.updateSubtask(2, "Задача изменена", "Описание", TaskStatus.DONE);
        assertEquals("Задача изменена", subtask.getName());
        assertEquals("Описание", subtask.getDescription());
        assertEquals(TaskStatus.DONE, subtask.getStatus());
        assertEquals(TaskStatus.DONE, taskManager.getEpic(1).getStatus()); //Заодно проверим, что статус эпика тоже
        // изменился
        taskManager.addSubtask("Для проверки", "статуса эпика",1);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(1).getStatus()); //Проверяем статус эпика IN_PROGRESS
    }

    @Test
    public void shouldEqual_WhenEpicUpdated() {
        taskManager.addEpic("Эпик", "Описание");
        Epic epic = taskManager.getEpic(1);
        taskManager.updateEpic(1, "Задача изменена", "Описание");
        assertEquals("Задача изменена", epic.getName());
        assertEquals("Описание", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }
}