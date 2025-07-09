package main;

import com.google.gson.reflect.TypeToken;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TasksTest extends HttpTaskServerTest {

    class TaskListTypeToken extends TypeToken<Map<Integer, Task>> {
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test task", "Testing task",
                LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        String url = "http://localhost:8080/tasks";
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = getResponseForPost(taskJson, url);
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        assertNotNull(taskManager.getTasks(), "Задачи не возвращаются");
        assertEquals(1, taskManager.getTasks().size(), "Некорректное количество задач");
        assertEquals("Test task", taskManager.getTasks().get(1).getName(), "Некорректное имя задачи");
        // И отдельно проверяем ответ при неуспешном сценарии
        Task task2 = new Task(6, "Test task", "Testing task",
                LocalDateTime.now(), Duration.ofMinutes(5));
        String task2Json = gson.toJson(task2);
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = getResponseForPost(task2Json, url);
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Нужно - добавлена задача с несуществущим id");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        // создаём задачи
        taskManager.addTask(new Task("Задача", "Для обновления"));
        Task task = new Task(1, "Test task", "Testing task",
                LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        String url = "http://localhost:8080/tasks";
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = getResponseForPost(taskJson, url);
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        assertNotNull(taskManager.getTasks(), "Задачи не возвращаются");
        assertEquals(1, taskManager.getTasks().size(), "Некорректное количество задач");
        assertEquals("Test task", taskManager.getTasks().get(1).getName(), "Некорректное имя задачи");
        // И отдельно проверяем ответ при неуспешном сценарии
        Task task2 = new Task(6, "Test task", "Testing task",
                LocalDateTime.now(), Duration.ofMinutes(5));
        String task2Json = gson.toJson(task2);
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = getResponseForPost(task2Json, url);
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Нужно - добавлена задача с несуществущим id");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        // создаём задачу
        taskManager.addTask(new Task("Test task", "Testing task",
                LocalDateTime.now(), Duration.ofMinutes(5)));
        String url = "http://localhost:8080/tasks/1";
        // вызываем рест, отвечающий за удаление задач
        HttpResponse<String> response = getNotPostResponse(url, "DELETE");
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что задача удалена
        assertNotNull(taskManager.getTasks(), "Задачи не возвращаются");
        assertEquals(0, taskManager.getTasks().size(), "Некорректное количество задач");
        // И отдельно проверяем ответ при неуспешном сценарии
        HttpResponse<String> response2 = getNotPostResponse(url, "DELETE");
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Нужно - задача для удаления не существует");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        // создаём задачу
        taskManager.addTask(new Task("Test task", "Testing task",
                LocalDateTime.now(), Duration.ofMinutes(5)));
        String url = "http://localhost:8080/tasks/1";
        String badUrl = "http://localhost:8080/tasks/45";
        // вызываем рест, отвечающий за получение задач
        HttpResponse<String> response = getNotPostResponse(url, "GET");
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        Task responseTask = gson.fromJson(response.body(), Task.class);
        // проверяем, что задача получена
        assertNotNull(responseTask, "Ответ не содержит задачу");
        assertEquals("Test task", responseTask.getName(), "Некорректное имя задачи");
        // И отдельно проверяем ответ при неуспешном сценарии
        HttpResponse<String> response2 = getNotPostResponse(badUrl, "GET");
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Нужно - задачи не существует");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        // создаём задачу
        taskManager.addTask(new Task("Test task", "Testing task"));
        taskManager.addTask(new Task("Test task2", "Testing task2"));
        // создаём HTTP-клиент и запрос
        String url = "http://localhost:8080/tasks";
        // вызываем рест, отвечающий за получение задач
        HttpResponse<String> response = getNotPostResponse(url, "GET");
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        Map<Integer, Task> responseTasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        // проверяем, что задачи получены
        assertNotNull(responseTasks, "Ответ не содержит задачи");
        assertEquals("Test task", responseTasks.get(1).getName(), "Некорректное имя задачи");
        assertEquals("Testing task2", responseTasks.get(2).getDescription());
        assertEquals(2, responseTasks.size(), "Некорректное количество задач");
        // И отдельно проверяем ответ при неуспешном сценарии
        taskManager.deleteAllTasks();
        response = getNotPostResponse(url, "GET");
        // проверяем код ответа
        assertEquals(404, response.statusCode(), "Нужно - список задач пуст");
    }
}
