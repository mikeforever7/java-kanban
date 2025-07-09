package main;

import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HistoryTest extends HttpTaskServerTest {

    class HistoryListTypeToken extends TypeToken<List<Task>> {
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        taskManager.addEpic(new Epic("Test epic", "Testing epic"));
        taskManager.addTask(new Task("Test task2", "Testing task2"));
        taskManager.addTask(new Task("Test task3", "Testing task3"));
        taskManager.getTask(3);
        taskManager.getEpic(1);
        taskManager.getTask(2);
        String url = "http://localhost:8080/history";
        // вызываем рест, отвечающий за получение истории
        HttpResponse<String> response = getNotPostResponse(url, "GET");
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        List<Task> responseTasks = gson.fromJson(response.body(), new HistoryListTypeToken().getType());
        assertNotNull(responseTasks, "Ответ не содержит задачи");
        assertEquals("Test task3", responseTasks.get(0).getName(), "Неверное имя задачи");
        assertEquals("Testing task2", responseTasks.get(2).getDescription(), "Неверное описание задачи");
        assertEquals(3, responseTasks.size(), "Некорректное количество задач");
    }
}
