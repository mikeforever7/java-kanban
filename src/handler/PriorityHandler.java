package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

public class PriorityHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public PriorityHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
            if (httpExchange.getRequestMethod().equals("GET") && pathParts[1].equals("prioritized") && pathParts.length == 2) {
                System.out.println("Началась обработка /prioritized запроса от клиента.");
                String response = gson.toJson(taskManager.getPrioritizedTasks());
                sendText(httpExchange, response);
            } else {
                sendNotFound(httpExchange, "Такого эндпоинта не существует");
            }
        } catch (RuntimeException e) {
            sendText(httpExchange, "Такого вообще нет", 400);
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
