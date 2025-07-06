package main;

import com.google.gson.reflect.TypeToken;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
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
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
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
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
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
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
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
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Нужно - добавлена задача с несуществущим id");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        // создаём задачу
        taskManager.addTask(new Task("Test task", "Testing task",
                LocalDateTime.now(), Duration.ofMinutes(5)));
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // вызываем рест, отвечающий за удаление задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что задача удалена
        assertNotNull(taskManager.getTasks(), "Задачи не возвращаются");
        assertEquals(0, taskManager.getTasks().size(), "Некорректное количество задач");
        // И отдельно проверяем ответ при неуспешном сценарии
        HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Нужно - задача для удаления не существует");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        // создаём задачу
        taskManager.addTask(new Task("Test task", "Testing task",
                LocalDateTime.now(), Duration.ofMinutes(5)));
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        URI badUrl = URI.create("http://localhost:8080/tasks/45");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за получение задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        Task responseTask = gson.fromJson(response.body(), Task.class);
        // проверяем, что задача получена
        assertNotNull(responseTask, "Ответ не содержит задачу");
        assertEquals("Test task", responseTask.getName(), "Некорректное имя задачи");
        // И отдельно проверяем ответ при неуспешном сценарии
        HttpRequest request2 = HttpRequest.newBuilder().uri(badUrl).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Нужно - задачи не существует");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        // создаём задачу
        taskManager.addTask(new Task("Test task", "Testing task"));
        taskManager.addTask(new Task("Test task2", "Testing task2"));
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json").GET().build();
        // вызываем рест, отвечающий за получение задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
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
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode(), "Нужно - список задач пуст");
    }
}
