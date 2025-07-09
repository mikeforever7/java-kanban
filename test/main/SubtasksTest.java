package main;

import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
        String url = "http://localhost:8080/subtasks";
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = getResponseForPost(subtaskJson, url);
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        assertNotNull(taskManager.getSubtasks(), "Подзадачи не возвращаются");
        assertEquals(1, taskManager.getSubtasks().size(), "Некорректное количество подзадач");
        assertEquals("Test subtask", taskManager.getSubtasks().get(2).getName(), "Некорректное имя подзадачи");
        // И отдельно проверяем ответ при неуспешном сценарии
        Subtask subtask2 = new Subtask("Test subtask", "Testing subtask", 2);
        String subtask2Json = gson.toJson(subtask2);
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = getResponseForPost(subtask2Json, url);
        // проверяем код ответа
        assertEquals(400, response2.statusCode(), "Нужно - подзадача некорректна, эпика для нее нет");
        // Еще один неуспешный сценарий на пересечение
        Subtask subtask3 = new Subtask("Test subtask", "Testing subtask",
                LocalDateTime.now(), Duration.ofMinutes(5), 1);
        String subtask3Json = gson.toJson(subtask3);
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response3 = getResponseForPost(subtask3Json, url);
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
        String url = "http://localhost:8080/subtasks";
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = getResponseForPost(subtaskJson, url);
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
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = getResponseForPost(subtask2Json, url);
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Нужно - подзадача с таким id не существует");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        // создаём подзадачу
        taskManager.addEpic(new Epic("Эпик", "Для подзадачи"));
        taskManager.addSubtask(new Subtask("Test subtask", "Testing subtask",
                LocalDateTime.now(), Duration.ofMinutes(5), 1));
        String url = "http://localhost:8080/subtasks/2";
        // вызываем рест, отвечающий за удаление подзадач
        HttpResponse<String> response = getNotPostResponse(url, "DELETE");
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что подзадача удалена, в том числе из эпика
        assertNotNull(taskManager.getSubtasks(), "Подзадачи не возвращаются");
        assertEquals(0, taskManager.getSubtasks().size(), "Некорректное количество подзадач");
        assertEquals(0, taskManager.getEpic(1).getSubtasksInEpic().size(),
                "Некорректное количество подзадач в эпике");
        // И отдельно проверяем ответ при неуспешном сценарии
        String badUrl = "http://localhost:8080/subtasks/8";
        HttpResponse<String> response2 = getNotPostResponse(badUrl, "DELETE");
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Должен быть неверный путь");
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        // создаём подзадачу
        taskManager.addEpic(new Epic("Эпик", "Для подзадачи"));
        taskManager.addSubtask(new Subtask("Test subtask", "Testing subtask",
                LocalDateTime.now(), Duration.ofMinutes(5), 1));
        String url = "http://localhost:8080/subtasks/2";
        // вызываем рест, отвечающий за получение задач
        HttpResponse<String> response = getNotPostResponse(url, "GET");
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);
        // проверяем, что задача получена
        assertNotNull(responseSubtask, "Ответ не содержит подзадачу");
        assertEquals("Test subtask", responseSubtask.getName(), "Некорректное имя подзадачи");
        // И отдельно проверяем ответ при неуспешном сценарии
        taskManager.deleteAllSubtasks();
        response = getNotPostResponse(url, "GET");
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
        String url = "http://localhost:8080/subtasks";
        String badUrl = "http://localhost:8080/sssssubtasks";
        // вызываем рест, отвечающий за получение задач
        HttpResponse<String> response = getNotPostResponse(url, "GET");
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        Map<Integer, Subtask> responseSubtasks = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        // проверяем, что задачи получены
        assertNotNull(responseSubtasks, "Ответ не содержит задачи");
        assertEquals("Test subtask", responseSubtasks.get(2).getName(), "Некорректное имя задачи");
        assertEquals("Testing subtask2", responseSubtasks.get(3).getDescription());
        assertEquals(2, responseSubtasks.size(), "Некорректное количество задач");
        // И отдельно проверяем ответ при неуспешном сценарии
        HttpResponse<String> response2 = getNotPostResponse(badUrl, "GET");
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Нужно - неверный путь");
    }
}
