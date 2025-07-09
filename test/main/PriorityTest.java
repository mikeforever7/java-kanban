package main;

import com.google.gson.reflect.TypeToken;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PriorityTest extends HttpTaskServerTest {

    class PriorityListTypeToken extends TypeToken<List<Task>> {
    }

    @Test
    public void testGetPriority() throws IOException, InterruptedException {
        taskManager.addTask(new Task("Test task", "Testing task",
                LocalDateTime.of(2023, 5, 12, 10, 0), Duration.ofMinutes(5)));
        taskManager.addTask(new Task("Test task2", "Testing task2",
                LocalDateTime.of(2025, 5, 12, 10, 0), Duration.ofMinutes(60)));
        taskManager.addTask(new Task("Test task3", "Testing task3",
                LocalDateTime.of(2024, 5, 12, 10, 0), Duration.ofMinutes(30)));
        String url = "http://localhost:8080/prioritized";
        // вызываем рест, отвечающий за получение истории
        HttpResponse<String> response = getNotPostResponse(url, "GET");
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        List<Task> responseTasks = gson.fromJson(response.body(), new PriorityListTypeToken().getType());
        assertNotNull(responseTasks, "Ответ не содержит задачи");
        assertEquals("Test task2", responseTasks.get(2).getName(), "Неверное имя задачи");
        assertEquals("Testing task3", responseTasks.get(1).getDescription(), "Неверное описание задачи");
        assertEquals(3, responseTasks.size(), "Некорректное количество задач");
    }
}
