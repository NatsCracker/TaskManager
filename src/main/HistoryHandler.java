package main;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import main.model.Task;
import main.service.TaskManager;
import main.util.GsonConfig;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = GsonConfig.getGson();

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            List<Task> history = taskManager.getHistory();
            sendText(exchange, gson.toJson(history));
        } else {
            exchange.sendResponseHeaders(405, 0);
            exchange.getResponseBody().close();
        }
    }
} 