package handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.util.Arrays;

public class PriorityHandler extends BaseHttpHandler {

    private final TaskManager taskManager;

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
        } catch (Exception e) {
            sendText(httpExchange, "Критическая ошибка", 400);
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
