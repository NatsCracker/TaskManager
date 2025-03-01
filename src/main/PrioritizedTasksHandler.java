package main;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import main.model.Task;
import main.service.TaskManager;
import main.util.GsonConfig;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("checkstyle:Regexp")
public class PrioritizedTasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = GsonConfig.getGson();

    public PrioritizedTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            sendText(exchange, gson.toJson(prioritizedTasks));
        } else {
            exchange.sendResponseHeaders(405, 0);
            exchange.getResponseBody().close();
        }
    }
} 