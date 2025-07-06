package main;

import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EpicsTest extends HttpTaskServerTest {

    class EpicListTypeToken extends TypeToken<Map<Integer, Epic>> {
    }

    class EpicSubtasksTypeToken extends TypeToken<List<Subtask>> {
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test epic", "Testing epic");
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        assertNotNull(taskManager.getEpics(), "Эпики не возвращаются");
        assertEquals(1, taskManager.getEpics().size(), "Некорректное количество эпиков");
        assertEquals("Test epic", taskManager.getEpics().get(1).getName(), "Некорректное имя эпика");
        // И отдельно проверяем ответ при неуспешном сценарии
        Epic epic2 = new Epic(3, "Эпик", "Для плохого сценария", TaskStatus.IN_PROGRESS);
        String epic2Json = gson.toJson(epic2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epic2Json)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Нужно - эпик с неккоректным id");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        // создаём задачу
        taskManager.addEpic(new Epic("Test epic", "Testing epic"));
        taskManager.addSubtask(new Subtask("Подзадача", "Для эпика", 1));
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // вызываем рест, отвечающий за удаление эпиков
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что эпик и его подзадача удалены
        assertNotNull(taskManager.getEpics(), "Эпики не возвращаются");
        assertEquals(0, taskManager.getEpics().size(), "Некорректное количество эпиков");
        assertEquals(0, taskManager.getSubtasks().size(), "Некорректное количество подзадач");
        // И отдельно проверяем ответ при неуспешном сценарии
        HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Нужно - эпика для удаления не существует");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        // создаём задачу
        taskManager.addEpic(new Epic("Test epic", "Testing epic"));
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        URI badUrl = URI.create("http://localhost:8080/epics/1dsf");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за получение задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        Epic responseEpic = gson.fromJson(response.body(), Epic.class);
        // проверяем, что задача получена
        assertNotNull(responseEpic, "Ответ не содержит эпика'");
        assertEquals("Test epic", responseEpic.getName(), "Некорректное имя эпика");
        // И отдельно проверяем ответ при неуспешном сценарии
        HttpRequest request2 = HttpRequest.newBuilder().uri(badUrl).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(400, response2.statusCode(), "Нужно - некорректный id");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        // создаём задачу
        taskManager.addEpic(new Epic("Test epic", "Testing epic"));
        taskManager.addEpic(new Epic("Test epic2", "Testing epic2"));
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за получение задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что задача получены
        Map<Integer, Epic> responseTasks = gson.fromJson(response.body(), new EpicListTypeToken().getType());
        assertEquals("Test epic", responseTasks.get(1).getName());
        assertEquals("Testing epic2", responseTasks.get(2).getDescription());
        assertEquals(2, responseTasks.size(), "Некорректное количество эпиков");
        // И отдельно проверяем ответ при неуспешном сценарии
        taskManager.deleteAllEpics();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode(), "Нужно - список эпиков пуст");
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        // создаём задачу
        taskManager.addEpic(new Epic("Test epic", "Testing epic"));
        taskManager.addSubtask(new Subtask("Test subtask", "Testing subtask", 1));
        taskManager.addSubtask(new Subtask("Test subtask2", "Testing subtask2", 1));
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за получение задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что задачи получены
        List<Subtask> responseTasks = gson.fromJson(response.body(), new EpicSubtasksTypeToken().getType());
        assertEquals("Test subtask", responseTasks.get(0).getName());
        assertEquals("Testing subtask2", responseTasks.get(1).getDescription());
        assertEquals(2, responseTasks.size(), "Некорректное количество эпиков");
        // И отдельно проверяем ответ при неуспешном сценарии
        taskManager.deleteAllEpics();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode(), "Нужно - список эпиков пуст");
    }
}
