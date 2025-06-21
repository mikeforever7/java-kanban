package manager;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = File.createTempFile("test", ".csv");
            tempFile.deleteOnExit();
            return FileBackedTaskManager.loadFromFile(tempFile);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл", e);
        }
    }

    @Test
    public void shouldThrowManagerLoadExceptionWhenFileHasInvalidData() throws Exception {
        // Создаем временный файл с некорректными данными
        File tempFile = new File("c:\\Users\\user\\IdeaProjects\\java-kanban\\taskManager.csv");
        tempFile.deleteOnExit();
        Files.writeString(tempFile.toPath(), "id,type,name,status,description,startTime,duration,epic\n" +
                "invalid,data,here\n");
        // Проверяем, что метод loadFromFile выбрасывает ManagerLoadException
        assertThrows(ManagerLoadException.class, () -> {
            FileBackedTaskManager.loadFromFile(tempFile);
        }, "При загрузке из файла с некорректными данными должно выбрасываться ManagerLoadException");
    }

    @Test
    public void shouldThrowManagerSaveExceptionWhenFileIsNotWritable() {
        // Создаем временный файл и делаем его недоступным для записи
        tempFile.setWritable(false); // Делаем файл неизменяемым
        assertThrows(ManagerSaveException.class, () -> {
            taskManager.save();
        }, "При попытке сохранения в недоступный файл должно выбрасываться ManagerSaveException");
    }

    @Test
    public void shouldThrowManagerSaveExceptionWhenFileCannotBeCreated() {
        // Создаем файл в недоступной директории (например, /root/test.csv)
        tempFile = new File("/root/test.csv");
        // Проверяем, что метод loadFromFile выбрасывает ManagerSaveException
        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(tempFile);
        }, "При ошибке создания файла должно выбрасываться ManagerSaveException");
    }

    @Test
    public void mustBeTrue_WhenNewTaskManagerIsEmpty() {
        tempFile.delete(); //Проверка что при отсутствии файла, метод save() создаст новый.
        taskManager.save();
        FileBackedTaskManager newTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(newTaskManager.tasks.isEmpty() && newTaskManager.epics.isEmpty() &&
                newTaskManager.subtasks.isEmpty());
    }

    @Test
    public void tasksMustBeEquals_WhenLoadFromFileAfterSaveFile() {
        taskManager.addTask(new Task("Задача", "для теста"));
        taskManager.addEpic(new Epic("Эпик", "для теста"));
        taskManager.addSubtask(new Subtask("Подзадача", "для теста", 2));
        FileBackedTaskManager newTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals("Задача", newTaskManager.tasks.get(1).getName(), "Задачи не совпадают");
        assertEquals(taskManager.tasks.get(1), newTaskManager.tasks.get(1));
        assertEquals(taskManager.epics.get(2), newTaskManager.epics.get(2));
        assertEquals(taskManager.subtasks.get(3), newTaskManager.subtasks.get(3));
    }
}