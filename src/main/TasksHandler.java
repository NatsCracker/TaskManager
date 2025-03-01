package main;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import main.exception.TaskValidationException;
import main.model.Task;
import main.service.TaskManager;
import main.util.GsonConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private final TaskManager taskManager;
    private final Gson gson = GsonConfig.getGson();

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set(CONTENT_TYPE, APPLICATION_JSON);

        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            // Проверяем путь на наличие ID
            boolean isPathWithId = path.matches("/tasks/\\d+");
            int id = isPathWithId ? extractIdFromPath(path) : 0;

            switch (method) {
                case "GET":
                    if (isPathWithId) {
                        Task task = taskManager.getTaskById(id);
                        if (task != null) {
                            sendText(exchange, gson.toJson(task));
                        } else {
                            sendNotFound(exchange, "Задача не найдена");
                        }
                    } else {
                        List<Task> tasks = taskManager.getAllTasks();
                        if (tasks.isEmpty()) {
                            sendNotFound(exchange, "Задачи не найдены");
                        } else {
                            sendText(exchange, gson.toJson(tasks));
                        }
                    }
                    break;
                case "POST":
                    if (isPathWithId) {
                        sendBadRequest(exchange, "Некорректный URL для POST запроса");
                        return;
                    }
                    handlePost(exchange);
                    break;
                case "DELETE":
                    if (isPathWithId) {
                        taskManager.deleteTaskById(id);
                        sendText(exchange, "Задача удалена");
                    } else {
                        taskManager.deleteAllTasks();
                        sendText(exchange, "Все задачи удалены");
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, 0);
                    exchange.getResponseBody().close();
            }
        } catch (TaskValidationException e) {
            sendResponse(exchange, 406, e.getMessage());
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        try {
            Task task = gson.fromJson(body, Task.class);
            if (task.getId() == 0) {
                taskManager.createTask(task);
                Task createdTask = taskManager.getTaskById(task.getId());
                sendText(exchange, gson.toJson(createdTask));
            } else {
                taskManager.updateTask(task);
                Task updatedTask = taskManager.getTaskById(task.getId());
                sendText(exchange, gson.toJson(updatedTask));
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Некорректный формат JSON");
        }
    }

    protected void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, 200, text);
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        sendResponse(exchange, 404, message);
    }

    protected void sendServerError(HttpExchange exchange, String message) throws IOException {
        sendResponse(exchange, 500, message);
    }
}

