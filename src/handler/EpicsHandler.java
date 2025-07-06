package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.CrossTasksInTimeException;
import manager.TaskManager;
import model.Epic;
import model.TaskStatus;
import model.TaskType;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


public class EpicsHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());

            System.out.println("Началась обработка /epics запроса от клиента.");
            System.out.println(endpoint);
            switch (endpoint) {
                case GET_EPICS: {
                    handleGetEpics(httpExchange);
                    break;
                }
                case GET_EPIC: {
                    handleGetEpicById(httpExchange);
                    break;
                }
                case GET_EPIC_SUBTASKS: {
                    handleGetEpicSubtasks(httpExchange);
                    break;
                }
                case DELETE_EPIC: {
                    handleDeleteEpicById(httpExchange);
                    break;
                }
                case POST_EPIC: {
                    handlePostEpic(httpExchange);
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

    private void handleGetEpicSubtasks(HttpExchange httpExchange) throws IOException {
        Optional<Integer> epicId = getTaskId(httpExchange);
        if (epicId.isEmpty()) {
            sendNotCorrect(httpExchange, "Некорректный id эпика");
            return;
        }
        if (taskManager.getEpic(epicId.get()) == null) {
            sendNotFound(httpExchange, "Эпика с таким id не существует");
            return;
        }
        String response = gson.toJson(taskManager.getEpic(epicId.get()).getSubtasksInEpic());
        sendText(httpExchange, response);
    }

    private void handlePostEpic(HttpExchange httpExchange) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String body = bufferedReader
                    .lines()
                    .collect(Collectors.joining("\n"));
            try {
                Epic epic = gson.fromJson(body, Epic.class);
                epic.setType(TaskType.EPIC);
                if (epic.getStatus() == null) {
                    epic.setStatus(TaskStatus.NEW);
                }
                if (epic.getId() == 0) {
                    taskManager.addEpic(epic);
                    sendText(httpExchange, "Эпик добавлен", 201);
                } else if (taskManager.getEpic(epic.getId()) == null) {
                    sendNotFound(httpExchange, "Эпика с таким id не существует");
                } else {
                    //  taskManager.updateEpic(epic);
                    sendText(httpExchange, "Функция изменения эпика недоступна", 400);
                }
            } catch (CrossTasksInTimeException e) {
                sendHasInteractions(httpExchange, e.getMessage());
            } catch (RuntimeException e) {
                sendNotCorrect(httpExchange, "Эпик не корректен");
            }
        }
    }

    private void handleDeleteEpicById(HttpExchange httpExchange) throws IOException {
        Optional<Integer> epicId = getTaskId(httpExchange);
        if (epicId.isEmpty()) {
            sendNotCorrect(httpExchange, "Некорректный id эпика");
            return;
        }
        if (taskManager.getEpic(epicId.get()) == null) {
            sendNotFound(httpExchange, "Эпика с таким id не существует");
            return;
        }
        taskManager.deleteEpicById(epicId.get());
        sendText(httpExchange, "Эпик успешно удалён");
    }

    private void handleGetEpics(HttpExchange httpExchange) throws IOException {
        if (taskManager.getEpics().isEmpty()) {
            sendNotFound(httpExchange, "Список эпиков пуст");
            return;
        }
        String response = gson.toJson(taskManager.getEpics());
        sendText(httpExchange, response);
    }

    private void handleGetEpicById(HttpExchange httpExchange) throws IOException {
        Optional<Integer> epicId = getTaskId(httpExchange);
        if (epicId.isEmpty()) {
            sendNotCorrect(httpExchange, "Некорректный id эпика");
            return;
        }
        if (taskManager.getEpic(epicId.get()) == null) {
            sendNotFound(httpExchange, "Эпика с таким id не существует");
            return;
        }
        String response = gson.toJson(taskManager.getEpic(epicId.get()));
        sendText(httpExchange, response);
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (requestMethod.equals("GET") && pathParts[1].equals("epics")) {
            if (pathParts.length == 2) {
                return Endpoint.GET_EPICS;
            }
            if (pathParts.length == 3) {
                return Endpoint.GET_EPIC;
            }
            if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                return Endpoint.GET_EPIC_SUBTASKS;
            }
        }
        if (requestMethod.equals("POST") && pathParts.length == 2) {
            return Endpoint.POST_EPIC;
        }
        if (requestMethod.equals("DELETE") && pathParts.length == 3) {
            return Endpoint.DELETE_EPIC;
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {
        GET_EPICS, GET_EPIC, GET_EPIC_SUBTASKS, POST_EPIC, DELETE_EPIC, UNKNOWN
    }

}