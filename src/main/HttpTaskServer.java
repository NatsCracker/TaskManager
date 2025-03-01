package main;

import com.sun.net.httpserver.HttpServer;
import main.service.Managers;
import main.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.taskManager = manager;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Регистрация обработчиков для базовых путей API:
        server.createContext("/tasks", new TasksHandler(taskManager));
        server.createContext("/subtasks", new SubtasksHandler(taskManager));
        server.createContext("/epics", new EpicsHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedTasksHandler(taskManager));
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }


    public void stop() {
        server.stop(1);
        System.out.println("HTTP-сервер остановлен");
    }

    public static void main(String[] args) {
        try {
            TaskManager manager = Managers.getDefault();
            HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
            httpTaskServer.start();
        } catch (IOException e) {
            System.out.println("Ошибка при запуске HTTP-сервера: " + e.getMessage());
        }
    }
}

