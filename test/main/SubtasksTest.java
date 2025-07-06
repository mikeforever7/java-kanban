package main;

import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
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

public class SubtasksTest extends HttpTaskServerTest {

    class SubtaskListTypeToken extends TypeToken<Map<Integer, Subtask>> {
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        // создаём задачу
        taskManager.addEpic(new Epic("Эпик", "Для подзадачи"));
        Subtask subtask = new Subtask("Test subtask", "Testing subtask",
                LocalDateTime.now(), Duration.ofMinutes(5), 1);
        // конвертируем её в JSON
        String subtaskJson = gson.toJson(subtask);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        assertNotNull(taskManager.getSubtasks(), "Подзадачи не возвращаются");
        assertEquals(1, taskManager.getSubtasks().size(), "Некорректное количество подзадач");
        assertEquals("Test subtask", taskManager.getSubtasks().get(2).getName(), "Некорректное имя подзадачи");
        // И отдельно проверяем ответ при неуспешном сценарии
        Subtask subtask2 = new Subtask("Test subtask", "Testing subtask", 2);
        String subtask2Json = gson.toJson(subtask2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtask2Json)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(400, response2.statusCode(), "Нужно - подзадача некорректна, эпика для нее нет");
        // Еще один неуспешный сценарий на пересечение
        Subtask subtask3 = new Subtask("Test subtask", "Testing subtask",
                LocalDateTime.now(), Duration.ofMinutes(5), 1);
        String subtask3Json = gson.toJson(subtask3);
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtask3Json)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response3.statusCode(), "Нужно - Задачи пересекаются по времени");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        // создаём задачи
        taskManager.addEpic(new Epic("Эпик", "Для подзадачи"));
        taskManager.addSubtask(new Subtask("Подзадача", "Для обновления", 1));
        Subtask subtask = new Subtask(2, "Test subtask", "Testing subtask",
                LocalDateTime.now(), Duration.ofMinutes(5), 1);
        // конвертируем её в JSON
        String subtaskJson = gson.toJson(subtask);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        assertNotNull(taskManager.getSubtasks(), "Подзадачи не возвращаются");
        assertEquals(1, taskManager.getSubtasks().size(), "Некорректное количество подзадач");
        assertEquals("Test subtask", taskManager.getSubtasks().get(2).getName(), "Некорректное имя подзадачи");
        // И отдельно проверяем ответ при неуспешном сценарии
        Subtask subtask2 = new Subtask(3, "Test subtask", "Testing subtask",
                LocalDateTime.now(), Duration.ofMinutes(5), 1);
        String subtask2Json = gson.toJson(subtask2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtask2Json)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Нужно - подзадача с таким id не существует");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        // создаём подзадачу
        taskManager.addEpic(new Epic("Эпик", "Для подзадачи"));
        taskManager.addSubtask(new Subtask("Test subtask", "Testing subtask",
                LocalDateTime.now(), Duration.ofMinutes(5), 1));
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // вызываем рест, отвечающий за удаление подзадач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что подзадача удалена, в том числе из эпика
        assertNotNull(taskManager.getSubtasks(), "Подзадачи не возвращаются");
        assertEquals(0, taskManager.getSubtasks().size(), "Некорректное количество подзадач");
        assertEquals(0, taskManager.getEpic(1).getSubtasksInEpic().size(),
                "Некорректное количество подзадач в эпике");
        // И отдельно проверяем ответ при неуспешном сценарии
        URI badUrl = URI.create("http://localhost:8080/subtasks/8");
        HttpRequest request2 = HttpRequest.newBuilder().uri(badUrl).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Должен быть неверный путь");
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        // создаём подзадачу
        taskManager.addEpic(new Epic("Эпик", "Для подзадачи"));
        taskManager.addSubtask(new Subtask("Test subtask", "Testing subtask",
                LocalDateTime.now(), Duration.ofMinutes(5), 1));
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за получение задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);
        // проверяем, что задача получена
        assertNotNull(responseSubtask, "Ответ не содержит подзадачу");
        assertEquals("Test subtask", responseSubtask.getName(), "Некорректное имя подзадачи");
        // И отдельно проверяем ответ при неуспешном сценарии
        taskManager.deleteAllSubtasks();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode(), "Нужно - подзадачи не существует");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        // создаём подзадачу
        taskManager.addEpic(new Epic("Эпик", "Для подзадачи"));
        taskManager.addSubtask(new Subtask("Test subtask", "Testing subtask",
                LocalDateTime.now(), Duration.ofMinutes(5), 1));
        taskManager.addSubtask(new Subtask("Test subtask2", "Testing subtask2", 1));
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        URI badUrl = URI.create("http://localhost:8080/sssssubtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за получение задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        Map<Integer, Subtask> responseSubtasks = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        // проверяем, что задачи получены
        assertNotNull(responseSubtasks, "Ответ не содержит задачи");
        assertEquals("Test subtask", responseSubtasks.get(2).getName(), "Некорректное имя задачи");
        assertEquals("Testing subtask2", responseSubtasks.get(3).getDescription());
        assertEquals(2, responseSubtasks.size(), "Некорректное количество задач");
        // И отдельно проверяем ответ при неуспешном сценарии
        HttpRequest request2 = HttpRequest.newBuilder().uri(badUrl).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Нужно - неверный путь");
    }
}
