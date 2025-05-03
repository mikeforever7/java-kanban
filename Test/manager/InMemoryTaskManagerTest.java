package manager;

import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static TaskManager taskManager;

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




//        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
//        final int taskId = taskManager.addNewTask(task);
//
//        final Task savedTask = taskManager.getTask(taskId);
//
//        assertNotNull(savedTask, "Задача не найдена.");
//        assertEquals(task, savedTask, "Задачи не совпадают.");
//
//        final List<Task> tasks = taskManager.getTasks();
//
//        assertNotNull(tasks, "Задачи не возвращаются.");
//        assertEquals(1, tasks.size(), "Неверное количество задач.");
//        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
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


}