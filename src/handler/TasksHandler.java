package handler;

import com.sun.net.httpserver.HttpExchange;
import manager.CrossTasksInTimeException;
import manager.TaskManager;
import model.Task;
import model.TaskStatus;
import model.TaskType;

import java.io.*;
import java.util.Arrays;
import java.util.Optional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class TasksHandler extends BaseHttpHandler {

    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());

            System.out.println("Началась обработка /tasks запроса от клиента.");
            System.out.println(endpoint);
            switch (endpoint) {
                case GET_TASKS: {
                    handleGetTasks(httpExchange);
                    break;
                }
                case GET_TASK: {
                    handleGetTaskById(httpExchange);
                    break;
                }
                case DELETE_TASK: {
                    handleDeleteTaskById(httpExchange);
                    break;
                }
                case POST_TASK: {
                    handlePostTask(httpExchange);
                    break;
                }
                case UNKNOWN: {
                    sendNotFound(httpExchange, "Такого эндпоинта не существует");
                }
            }
        } catch (Exception e) {
            sendText(httpExchange, "Критическая ошибка", 400);
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    private void handlePostTask(HttpExchange httpExchange) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String body = bufferedReader
                    .lines()
                    .collect(Collectors.joining("\n"));
            try {
                Task task = gson.fromJson(body, Task.class);
                task.setType(TaskType.TASK);
                if (task.getStatus() == null) {
                    task.setStatus(TaskStatus.NEW);
                }
                if (task.getId() == 0) {
                    taskManager.addTask(task);
                    sendText(httpExchange, "Задача добавлена", 201);
                } else if (taskManager.getTask(task.getId()) == null) {
                    System.out.println("НЕТ id");
                    sendNotFound(httpExchange, "Задачи с таким id не существует");
                } else {
                    taskManager.updateTask(task);
                    sendText(httpExchange, "Задача обновлена", 201);
                }
            } catch (CrossTasksInTimeException e) {
                sendHasInteractions(httpExchange, e.getMessage());
            } catch (Exception e) {
                sendNotCorrect(httpExchange, "Задача не корректна");
            }
        }
    }

    private void handleDeleteTaskById(HttpExchange httpExchange) throws IOException {
        Optional<Integer> taskId = getTaskId(httpExchange);
        if (taskId.isEmpty()) {
            sendNotCorrect(httpExchange, "Некорректный id задачи");
            return;
        }
        if (taskManager.getTask(taskId.get()) == null) {
            sendNotFound(httpExchange, "Задачи с таким id не существует");
            return;
        }
        taskManager.deleteTaskById(taskId.get());
        sendText(httpExchange, "Задача успешно удалена");
    }

    private void handleGetTasks(HttpExchange httpExchange) throws IOException {
        if (taskManager.getTasks().isEmpty()) {
            sendNotFound(httpExchange, "Список задач пуст");
            return;
        }
        String response = gson.toJson(taskManager.getTasks());
        sendText(httpExchange, response);
    }

    private void handleGetTaskById(HttpExchange httpExchange) throws IOException {
        Optional<Integer> taskId = getTaskId(httpExchange);
        if (taskId.isEmpty()) {
            sendNotCorrect(httpExchange, "Некорректный id задачи");
            return;
        }
        if (taskManager.getTask(taskId.get()) == null) {
            sendNotFound(httpExchange, "Задачи с таким id не существует");
            return;
        }
        String response = gson.toJson(taskManager.getTask(taskId.get()));
        sendText(httpExchange, response);
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (requestMethod.equals("GET") && pathParts[1].equals("tasks")) {
            if (pathParts.length == 2) {
                return Endpoint.GET_TASKS;
            }
            if (pathParts.length == 3) {
                return Endpoint.GET_TASK;
            }
        }
        if (requestMethod.equals("POST") && pathParts.length == 2) {
            return Endpoint.POST_TASK;
        }
        if (requestMethod.equals("DELETE") && pathParts.length == 3) {
            return Endpoint.DELETE_TASK;
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {
        GET_TASKS, GET_TASK, POST_TASK, DELETE_TASK, UNKNOWN
    }

}
