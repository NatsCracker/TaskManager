package main;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import main.exception.TaskValidationException;
import main.model.Epic;
import main.service.TaskManager;
import main.util.GsonConfig;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private final TaskManager taskManager;
    private final Gson gson = GsonConfig.getGson();

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set(CONTENT_TYPE, APPLICATION_JSON);

        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            boolean isPathWithId = path.matches("/epics/\\d+");
            int id = isPathWithId ? extractIdFromPath(path) : 0;

            switch (method) {
                case "GET":
                    if (isPathWithId) {
                        Epic epic = taskManager.getEpicById(id);
                        if (epic != null) {
                            sendText(exchange, gson.toJson(epic));
                        } else {
                            sendNotFound(exchange, "Эпик не найден");
                        }
                    } else {
                        List<Epic> epics = taskManager.getAllEpics();
                        if (epics.isEmpty()) {
                            sendNotFound(exchange, "Эпики не найдены");
                        } else {
                            sendText(exchange, gson.toJson(epics));
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
                        Epic epic = gson.fromJson(body, Epic.class);
                        if (epic.getId() == 0) {
                            taskManager.createEpic(epic);
                        } else {
                            taskManager.updateEpic(epic);
                        }
                        sendText(exchange, gson.toJson(epic));
                    } catch (JsonSyntaxException e) {
                        sendBadRequest(exchange, "Некорректный формат JSON");
                    }
                    break;
                case "DELETE":
                    if (isPathWithId) {
                        taskManager.deleteEpicById(id);
                        sendText(exchange, "Эпик удален");
                    } else {
                        taskManager.deleteAllEpics();
                        sendText(exchange, "Все эпики удалены");
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