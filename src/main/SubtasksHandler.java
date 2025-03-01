package main;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import main.exception.TaskValidationException;
import main.model.Subtask;
import main.service.TaskManager;
import main.util.GsonConfig;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("checkstyle:Regexp")
public class SubtasksHandler extends BaseHttpHandler {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private final TaskManager taskManager;
    private final Gson gson = GsonConfig.getGson();

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set(CONTENT_TYPE, APPLICATION_JSON);

        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            boolean isPathWithId = path.matches("/subtasks/\\d+");
            int id = isPathWithId ? extractIdFromPath(path) : 0;

            switch (method) {
                case "GET":
                    if (isPathWithId) {
                        Subtask subtask = taskManager.getSubtaskById(id);
                        if (subtask != null) {
                            sendText(exchange, gson.toJson(subtask));
                        } else {
                            sendNotFound(exchange, "Подзадача не найдена");
                        }
                    } else {
                        List<Subtask> subtasks = taskManager.getAllSubtasks();
                        if (subtasks.isEmpty()) {
                            sendNotFound(exchange, "Подзадачи не найдены");
                        } else {
                            sendText(exchange, gson.toJson(subtasks));
                        }
                    }
                    break;
                case "POST":
                    if (isPathWithId) {
                        sendBadRequest(exchange, "Некорректный URL для POST запроса");
                        return;
                    }
                    String body = readBody(exchange);
                    try {
                        Subtask subtask = gson.fromJson(body, Subtask.class);
                        if (subtask.getId() == 0) {
                            taskManager.createSubtask(subtask);
                        } else {
                            taskManager.updateSubtask(subtask);
                        }
                        sendText(exchange, gson.toJson(subtask));
                    } catch (JsonSyntaxException e) {
                        sendBadRequest(exchange, "Некорректный формат JSON");
                    }
                    break;
                case "DELETE":
                    if (isPathWithId) {
                        taskManager.deleteSubtaskById(id);
                        sendText(exchange, "Подзадача удалена");
                    } else {
                        taskManager.deleteAllSubtasks();
                        sendText(exchange, "Все подзадачи удалены");
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
} 