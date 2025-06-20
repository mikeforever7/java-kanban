package manager;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;
    private File tempFile;

    @BeforeEach
    public void beforeEach() throws IOException {
        tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();
        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
    }

    @Test
    public void mustBeTrue_WhenNewTaskManagerIsEmpty() {
        taskManager.save();
        FileBackedTaskManager newTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(newTaskManager.tasks.isEmpty() && newTaskManager.epics.isEmpty() &&
                newTaskManager.subtasks.isEmpty());
    }

    @Test
    public void tasksMustBeEquals_WhenLoadFromFileAfterSaveFile() {
        taskManager.addTask("Задача", "для теста");
        taskManager.addEpic("Эпик", "для теста");
        taskManager.addSubtask("Подзадача", "для теста", 2);
        FileBackedTaskManager newTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals("Задача", newTaskManager.tasks.get(1).getName(), "Задачи не совпадают");
        assertEquals(taskManager.tasks.get(1), newTaskManager.tasks.get(1));
        assertEquals(taskManager.epics.get(2), newTaskManager.epics.get(2));
        assertEquals(taskManager.subtasks.get(3), newTaskManager.subtasks.get(3));
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
        Map<Integer, Task> tasks = taskManager.getTasks();
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
        taskManager.addSubtask("Для проверки", "статуса эпика", 1);
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

    @Test
    public void deleteSubtask_WhenDeleteEpic() {
        taskManager.addEpic("Эпик", "Описание"); //Создан эпик с ID1
        taskManager.addSubtask("Подзадача", "Для эпика", 1);
        taskManager.deleteEpicById(1);
        assertNull(taskManager.getEpic(1)); // Проверяем, что эпика нет
        assertNull(taskManager.getSubtask(2)); //Проверяем, что подзадачи нет
    }

    @Test
    public void epicDoNotHaveSubtask_WhenSubtaskDelete() {
        taskManager.addEpic("Эпик", "Создан");
        taskManager.addSubtask("Подзадача", "C ID2", 1);
        taskManager.addSubtask("Подзадача", " С ID3", 1);
        Epic epic = taskManager.getEpic(1);
        List<Subtask> subtasksInEpic = epic.getSubtasksInEpic();
        assertEquals(2, subtasksInEpic.size()); //Список внутри эпика хранит две подзадачи
        taskManager.deleteSubtaskById(2); // Удаляем подзадачу с ID2
        assertFalse(subtasksInEpic.contains(taskManager.getSubtask(2))); //Проверка на наличие удаленной
        // подзадачи по ID
    }
}