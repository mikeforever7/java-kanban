package main;

import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
        Epic epic = new Epic("Test epic", "Testing epic");
        String url = "http://localhost:8080/epics";
        HttpResponse response = getResponseForPost(gson.toJson(epic), url);
        assertEquals(201, response.statusCode());
        assertNotNull(taskManager.getEpics(), "Эпики не возвращаются");
        assertEquals(1, taskManager.getEpics().size(), "Некорректное количество эпиков");
        assertEquals("Test epic", taskManager.getEpics().get(1).getName(), "Некорректное имя эпика");
        Epic epic2 = new Epic(3, "Эпик", "Для плохого сценария", TaskStatus.IN_PROGRESS);
        HttpResponse response2 = getResponseForPost(gson.toJson(epic2), url);
        assertEquals(404, response2.statusCode(), "Нужно - эпик с неккоректным id");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        // создаём задачу
        taskManager.addEpic(new Epic("Test epic", "Testing epic"));
        taskManager.addSubtask(new Subtask("Подзадача", "Для эпика", 1));
        String url = "http://localhost:8080/epics/1";
        HttpResponse<String> response = getNotPostResponse(url, "DELETE");
        assertEquals(200, response.statusCode());
        // проверяем, что эпик и его подзадача удалены
        assertNotNull(taskManager.getEpics(), "Эпики не возвращаются");
        assertEquals(0, taskManager.getEpics().size(), "Некорректное количество эпиков");
        assertEquals(0, taskManager.getSubtasks().size(), "Некорректное количество подзадач");
        // И отдельно проверяем ответ при неуспешном сценарии
        HttpResponse<String> response2 = getNotPostResponse(url, "DELETE");
        // проверяем код ответа
        assertEquals(404, response2.statusCode(), "Нужно - эпика для удаления не существует");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        // создаём задачу
        taskManager.addEpic(new Epic("Test epic", "Testing epic"));
        String url = "http://localhost:8080/epics/1";
        String badUrl = "http://localhost:8080/epics/1dsf";
        HttpResponse<String> response = getNotPostResponse(url, "GET");
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        Epic responseEpic = gson.fromJson(response.body(), Epic.class);
        // проверяем, что задача получена
        assertNotNull(responseEpic, "Ответ не содержит эпика'");
        assertEquals("Test epic", responseEpic.getName(), "Некорректное имя эпика");
        // И отдельно проверяем ответ при неуспешном сценарии
        HttpResponse<String> response2 = getNotPostResponse(badUrl, "GET");
        // проверяем код ответа
        assertEquals(400, response2.statusCode(), "Нужно - некорректный id");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        // создаём задачу
        taskManager.addEpic(new Epic("Test epic", "Testing epic"));
        taskManager.addEpic(new Epic("Test epic2", "Testing epic2"));
        String url = "http://localhost:8080/epics";
        // вызываем рест, отвечающий за получение задач
        HttpResponse<String> response = getNotPostResponse(url, "GET");
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что задачи получены
        Map<Integer, Epic> responseTasks = gson.fromJson(response.body(), new EpicListTypeToken().getType());
        assertEquals("Test epic", responseTasks.get(1).getName());
        assertEquals("Testing epic2", responseTasks.get(2).getDescription());
        assertEquals(2, responseTasks.size(), "Некорректное количество эпиков");
        // И отдельно проверяем ответ при неуспешном сценарии
        taskManager.deleteAllEpics();
        response = getNotPostResponse(url, "GET");
        // проверяем код ответа
        assertEquals(404, response.statusCode(), "Нужно - список эпиков пуст");
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        // создаём задачу
        taskManager.addEpic(new Epic("Test epic", "Testing epic"));
        taskManager.addSubtask(new Subtask("Test subtask", "Testing subtask", 1));
        taskManager.addSubtask(new Subtask("Test subtask2", "Testing subtask2", 1));
        String url = "http://localhost:8080/epics/1/subtasks";
        // вызываем рест, отвечающий за получение задач
        HttpResponse<String> response = getNotPostResponse(url, "GET");
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что задачи получены
        List<Subtask> responseTasks = gson.fromJson(response.body(), new EpicSubtasksTypeToken().getType());
        assertEquals("Test subtask", responseTasks.get(0).getName());
        assertEquals("Testing subtask2", responseTasks.get(1).getDescription());
        assertEquals(2, responseTasks.size(), "Некорректное количество эпиков");
        // И отдельно проверяем ответ при неуспешном сценарии
        taskManager.deleteAllEpics();
        response = getNotPostResponse(url, "GET");
        // проверяем код ответа
        assertEquals(404, response.statusCode(), "Нужно - список эпиков пуст");
    }
}
