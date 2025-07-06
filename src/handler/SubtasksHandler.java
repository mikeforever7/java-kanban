package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.CrossTasksInTimeException;
import manager.TaskManager;
import model.Subtask;
import model.TaskStatus;
import model.TaskType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());

            System.out.println("Началась обработка /subtasks запроса от клиента.");
            System.out.println(endpoint);
            switch (endpoint) {
                case GET_SUBTASKS: {
                    handleGetSubtasks(httpExchange);
                    break;
                }
                case GET_SUBTASK: {
                    handleGetSubtaskById(httpExchange);
                    break;
                }
                case DELETE_SUBTASK: {
                    handleDeleteSubtaskById(httpExchange);
                    break;
                }
                case POST_SUBTASK: {
                    handlePostSubtask(httpExchange);
                    break;
                }
                case UNKNOWN: {
                    sendNotFound(httpExchange, "Такого эндпоинта не существует");
                }
            }
        } catch (RuntimeException e) {
            sendText(httpExchange, "Критическая ошибка", 400);
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    private void handlePostSubtask(HttpExchange httpExchange) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String body = bufferedReader
                    .lines()
                    .collect(Collectors.joining("\n"));
            System.out.println(taskManager.getEpics());

            try {
                Subtask subtask = gson.fromJson(body, Subtask.class);
                subtask.setType(TaskType.SUBTASK);
                if (subtask.getStatus() == null) {
                    subtask.setStatus(TaskStatus.NEW);
                }
                if (subtask.getId() == 0) {
                    taskManager.addSubtask(subtask);
                    sendText(httpExchange, "Подзадача добавлена", 201);
                } else if (taskManager.getSubtask(subtask.getId()) == null) {
                    sendNotFound(httpExchange, "Подзадачи с таким id не существует");
                } else {
                    taskManager.updateSubtask(subtask);
                    sendText(httpExchange, "Подзадача обновлена", 201);
                }
            } catch (CrossTasksInTimeException e) {
                sendHasInteractions(httpExchange, e.getMessage());
            } catch (RuntimeException e) {
                sendNotCorrect(httpExchange, "Подзадача не корректна");
            }
        }
    }

    private void handleDeleteSubtaskById(HttpExchange httpExchange) throws IOException {
        Optional<Integer> subtaskId = getTaskId(httpExchange);
        if (subtaskId.isEmpty()) {
            sendNotCorrect(httpExchange, "Некорректный id подзадачи");
            return;
        }
        if (taskManager.getSubtask(subtaskId.get()) == null) {
            sendNotFound(httpExchange, "Подзадачи с таким id не существует");
            return;
        }
        taskManager.deleteSubtaskById(subtaskId.get());
        sendText(httpExchange, "Подзадача успешно удалена");
    }

    private void handleGetSubtasks(HttpExchange httpExchange) throws IOException {
        System.out.println(taskManager.getSubtasks());
        if (taskManager.getSubtasks().isEmpty()) {
            sendNotFound(httpExchange, "Список подзадач пуст");
            return;
        }
        String response = gson.toJson(taskManager.getSubtasks());
        sendText(httpExchange, response);
    }

    private void handleGetSubtaskById(HttpExchange httpExchange) throws IOException {
        Optional<Integer> subtaskId = getTaskId(httpExchange);
        if (subtaskId.isEmpty()) {
            sendNotCorrect(httpExchange, "Некорректный id подзадачи");
            return;
        }
        if (taskManager.getSubtask(subtaskId.get()) == null) {
            sendNotFound(httpExchange, "Подзадачи с таким id не существует");
            return;
        }
        String response = gson.toJson(taskManager.getSubtask(subtaskId.get()));
        sendText(httpExchange, response);
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (requestMethod.equals("GET") && pathParts[1].equals("subtasks")) {
            if (pathParts.length == 2) {
                return Endpoint.GET_SUBTASKS;
            }
            if (pathParts.length == 3) {
                return Endpoint.GET_SUBTASK;
            }
        }
        if (requestMethod.equals("POST") && pathParts.length == 2) {
            return Endpoint.POST_SUBTASK;
        }
        if (requestMethod.equals("DELETE") && pathParts.length == 3) {
            return Endpoint.DELETE_SUBTASK;
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {
        GET_SUBTASKS, GET_SUBTASK, POST_SUBTASK, DELETE_SUBTASK, UNKNOWN
    }
}
