package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager; // Объект менеджера задач

    protected abstract T createTaskManager();

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager(); // Создаем конкретный менеджер
    }

    @Test
    public void variablesMustBeEqual_WhenAddNewTask() {
        taskManager.addTask(new Task("Первая задача", "Описание"));
        Task savedTask = taskManager.getTask(1);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(1, savedTask.getId(), "Задачи не совпадают");
        assertEquals("Первая задача", savedTask.getName(), "Задачи не совпадают");
        assertEquals("Описание", savedTask.getDescription(), "Задачи не совпадают");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статусы не совпадают");
    }

    @Test
    public void variablesMustBeEqual_WhenAddNewEpic() {
        taskManager.addEpic(new Epic("Первый эпик", "Описание"));
        Epic savedEpic = taskManager.getEpic(1);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(1, savedEpic.getId(), "Эпики не совпадают");
        assertEquals("Первый эпик", savedEpic.getName(), "Эпики не совпадают");
        assertEquals("Описание", savedEpic.getDescription(), "Эпики не совпадают");
        assertEquals(TaskStatus.NEW, savedEpic.getStatus(), "Статусы не совпадают");
    }

    @Test
    public void variablesMustBeEqual_WhenAddNewSubtask() {
        taskManager.addEpic(new Epic("Эпик", "Для подзадачи"));   // id эпика = 1
        taskManager.addSubtask(new Subtask("Первая подзадача", "Описание", 1)); // id подзадачи = 2
        Subtask savedSubtask = taskManager.getSubtask(2);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(2, savedSubtask.getId(), "Подзадачи не совпадают");
        assertEquals("Первая подзадача", savedSubtask.getName(), "Подзадачи не совпадают");
        assertEquals("Описание", savedSubtask.getDescription(), "Подзадачи не совпадают");
        assertEquals(TaskStatus.NEW, savedSubtask.getStatus(), "Статусы не совпадают");
    }

    @Test
    public void shouldReturnCorrectSizeWhenElementsAreAddedToHashMap() {
        taskManager.addTask(new Task("Задача первая", "Описание"));
        taskManager.addTask(new Task("Задача вторая", "Описание"));
        Task savedTask = taskManager.getTask(2);
        Map<Integer, Task> tasks = taskManager.getTasks();
        assertNotNull(tasks.get(1), "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач");
        assertEquals(savedTask, tasks.get(2), "Задачи не равны");
    }

    @Test
    public void null_WhenTaskDeleted() {
        taskManager.addTask(new Task("Задача первая", "Описание"));
        Task task = taskManager.getTask(1);
        assertNotNull(task);  //Проверка, что задача существует
        taskManager.deleteTaskById(1);
        assertNull(taskManager.getTask(1));  //Проверка, что задача удалена
    }

    @Test
    public void null_WhenEpicDeleted() {
        taskManager.addEpic(new Epic("Эпик", "Описание"));
        Epic epic = taskManager.getEpic(1);
        assertNotNull(epic);  //Проверка, что задача существует
        taskManager.deleteEpicById(1);
        assertNull(taskManager.getEpic(1));  //Проверка, что задача удалена
    }

    @Test
    public void null_WhenSubtaskDeleted() {
        taskManager.addEpic(new Epic("Эпик", "для подзадачи"));
        taskManager.addSubtask(new Subtask("Задача первая", "Описание", 1));
        Subtask subtask = taskManager.getSubtask(2);
        assertNotNull(subtask);  //Проверка, что задача существует
        taskManager.deleteSubtaskById(2);
        assertNull(taskManager.getSubtask(2));  //Проверка, что задача удалена
    }

    @Test
    public void null_WhenAllTasksDeleted() {
        taskManager.addTask(new Task("Задача первая", "Описание"));
        Task task = taskManager.getTask(1);
        assertNotNull(task);  //Проверка, что задача существует
        taskManager.deleteAllTasks();
        assertNull(taskManager.getTask(1));  //Проверка, что задача удалена
    }

    @Test
    public void null_WhenAllEpicsDeleted() {
        taskManager.addEpic(new Epic("Эпик", "Описание"));
        Epic epic = taskManager.getEpic(1);
        assertNotNull(epic);  //Проверка, что задача существует
        taskManager.deleteAllEpics();
        assertNull(taskManager.getEpic(1));  //Проверка, что задача удалена
    }

    @Test
    public void null_WhenAllSubtasksDeleted() {
        taskManager.addEpic(new Epic("Эпик", "для подзадачи"));
        taskManager.addSubtask(new Subtask("Задача первая", "Описание", 1));
        Subtask subtask = taskManager.getSubtask(2);
        assertNotNull(subtask);  //Проверка, что задача существует
        taskManager.deleteAllSubtasks();
        assertNull(taskManager.getSubtask(2));  //Проверка, что задача удалена
    }

    @Test
    public void shouldEqual_WhenTaskUpdated() {
        taskManager.addTask(new Task("Задача первая", "Описание"));
        taskManager.updateTask(new Task(1, "Задача изменена", "Описание", TaskStatus.IN_PROGRESS));
        Task task = taskManager.getTask(1);
        assertEquals("Задача изменена", task.getName());
        assertEquals("Описание", task.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    public void shouldEqual_WhenSubTaskUpdatedAndEpicsStatusIsCorrect() {
        taskManager.addEpic(new Epic("Эпик", "Для подзадачи"));
        taskManager.addSubtask(new Subtask("Задача первая", "Описание", 1));
        assertEquals(TaskStatus.NEW, taskManager.getEpic(1).getStatus()); // Статус эпика NEW, если и подзадачи NEW
        taskManager.updateSubtask(new Subtask(2, "Задача изменена", "Описание", TaskStatus.IN_PROGRESS, 1));
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(1).getStatus());
        // Статус эпика IN_PROGRESS, если и подзадачи IN_PROGRESS
        Subtask subtask = taskManager.getSubtask(2);
        assertEquals("Задача изменена", subtask.getName());
        assertEquals("Описание", subtask.getDescription());
        taskManager.updateSubtask(new Subtask(2, "Задача изменена", "Описание", TaskStatus.DONE, 1));
        assertEquals(TaskStatus.DONE, taskManager.getEpic(1).getStatus()); //Проверяем, что статус эпика
        // изменился на DONE если подзадачи DONE
        Subtask newSubtask = taskManager.getSubtask(2);
        assertEquals(TaskStatus.DONE, newSubtask.getStatus());
        taskManager.addSubtask(new Subtask("Для проверки", "статуса эпика", 1));
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(1).getStatus());
        //Проверяем статус эпика IN_PROGRESS если задачи DONE и NEW
    }

    @Test
    public void shouldEqual_WhenEpicUpdated() {
        taskManager.addEpic(new Epic("Эпик", "Описание"));
        taskManager.updateEpic(new Epic(1, "Задача изменена", "Описание", TaskStatus.NEW));
        Epic epic = taskManager.getEpic(1);
        assertEquals("Задача изменена", epic.getName());
        assertEquals("Описание", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void deleteSubtask_WhenDeleteEpic() {
        taskManager.addEpic(new Epic("Эпик", "Описание")); //Создан эпик с ID1
        taskManager.addSubtask(new Subtask("Подзадача", "Для эпика", 1));
        taskManager.deleteEpicById(1);
        assertNull(taskManager.getEpic(1)); // Проверяем, что эпика нет
        assertNull(taskManager.getSubtask(2)); //Проверяем, что подзадачи нет
    }

    @Test
    public void epicDoNotHaveSubtask_WhenSubtaskDelete() {
        taskManager.addEpic(new Epic("Эпик", "Создан"));
        taskManager.addSubtask(new Subtask("Подзадача", "C ID2", 1));
        taskManager.addSubtask(new Subtask("Подзадача", " С ID3", 1));
        Epic epic = taskManager.getEpic(1);
        List<Subtask> subtasksInEpic = epic.getSubtasksInEpic();
        assertEquals(2, subtasksInEpic.size()); //Список внутри эпика хранит две подзадачи
        taskManager.deleteSubtaskById(2); // Удаляем подзадачу с ID2
        assertFalse(subtasksInEpic.contains(taskManager.getSubtask(2))); //Проверка на наличие удаленной
        // подзадачи по ID
    }

    @Test
    public void shouldSubtasksSizeBeEmpty_WhenAddedSubtaskWithoutEpic() {
        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addSubtask(new Subtask("Подзадача", "К несуществующему эпику", 1));
        }, "При попытке сохранения в недоступный файл должно выбрасываться ManagerSaveException");
    }

    @Test
    public void shouldCorrectEpicsTime_WhenSubtasksAddedAndDeleted() {
        taskManager.addEpic(new Epic("Эпик", "c id 1"));
        taskManager.addSubtask(new Subtask("Подзадача", "с длительностью 30мин",
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(30), 1));
        taskManager.addSubtask(new Subtask("Подзадача", "с длительностью 40мин",
                LocalDateTime.of(2025, 2, 1, 10, 0), Duration.ofMinutes(40), 1));
        assertEquals(4200, taskManager.getEpic(1).getDuration().getSeconds());
        taskManager.addSubtask(new Subtask("Подзадача", "c пересечением по вермени",
                LocalDateTime.of(2025, 2, 1, 9, 0), Duration.ofMinutes(100), 1));
        //Задача с пересечением во времени не добавлена, поэтому не должна влиять на длительность эпика
        assertEquals(4200, taskManager.getEpic(1).getDuration().getSeconds());
        //Проверка startTime и endTime у эпика в зависимости от подзадач
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0),
                taskManager.getEpic(1).getStartTime());
        assertEquals(LocalDateTime.of(2025, 2, 1, 10, 40),
                taskManager.getEpic(1).getEndTime());
        //При удалении подзадачи, также у эпика пересчитываются временные параметры
        taskManager.deleteSubtaskById(3);
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 30),
                taskManager.getEpic(1).getEndTime());
        assertEquals(1800, taskManager.getEpic(1).getDuration().getSeconds());
    }

    @Test
    public void shouldNotAddTask_WhenTimeCrossed() {
        taskManager.addTask(new Task("Подзадача", "для теста",
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60)));
        taskManager.addTask(new Task("Подзадача", "с пересечением сначла",
                LocalDateTime.of(2025, 1, 1, 9, 50), Duration.ofMinutes(30)));
        taskManager.addTask(new Task("Подзадача", "с пересечением в конце",
                LocalDateTime.of(2025, 1, 1, 10, 50), Duration.ofMinutes(30)));
        taskManager.addTask(new Task("Подзадача", "с полным охватом по времени",
                LocalDateTime.of(2025, 1, 1, 9, 0), Duration.ofMinutes(200)));
        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    public void shouldAddTask_WhenTimeNotCrossed() {
        taskManager.addTask(new Task("Задача", "с id 1",
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60)));
        taskManager.addTask(new Task("Задача", "с id 2",
                LocalDateTime.of(2023, 1, 1, 9, 50), Duration.ofMinutes(30)));
        taskManager.addEpic(new Epic("Эпик", "с id 3"));
        taskManager.addSubtask(new Subtask("Подзадача", "без пересечения с id 4",
                LocalDateTime.of(2024, 1, 1, 9, 0), Duration.ofMinutes(200), 3));
        assertEquals(3, taskManager.getPrioritizedTasks().size());
        //Проверяем сортировку
        ArrayList<Task> sortedList = new ArrayList<>(taskManager.getPrioritizedTasks());
        assertEquals(2, sortedList.get(0).getId());
        assertEquals(4, sortedList.get(1).getId());
        assertEquals(1, sortedList.get(2).getId());
    }

    @Test
    public void shouldNotAddSubtask_WhenTimeCrossedWithTask() {
        taskManager.addEpic(new Epic("Эпик", "c id 1"));
        taskManager.addTask(new Task("Подзадача", "с пересечением до Task",
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(30)));
        taskManager.addSubtask(new Subtask("Подзадача", "с пересечением до Task",
                LocalDateTime.of(2025, 1, 1, 9, 40), Duration.ofMinutes(30), 1));
        assertEquals(1, taskManager.getTasks().size());
    }
}
