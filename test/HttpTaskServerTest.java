import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.HttpTaskServer;
import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.service.InMemoryTaskManager;
import main.service.TaskManager;
import main.util.GsonConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static final String BASE_URL = "http://localhost:8080";
    private HttpTaskServer server;
    private final Gson gson = GsonConfig.getGson();
    private final HttpClient client = HttpClient.newHttpClient();
    private TaskManager taskManager;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        System.out.println("Запуск сервера...");
        taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        server.start();
        System.out.println("Сервер запущен.");
        Thread.sleep(1000);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void shouldCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Description",
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task createdTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(createdTask.getId());
        assertEquals(task.getName(), createdTask.getName());

        // Проверка, что задача добавлена в менеджер
        Task fetchedTask = taskManager.getTaskById(createdTask.getId());
        assertNotNull(fetchedTask);
        assertEquals(task.getName(), fetchedTask.getName());
    }

    @Test
    void shouldReturnNotFoundForNonExistentTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Description",
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/" + task.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void shouldCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Description");
        String json = gson.toJson(epic);

        HttpResponse<String> response = post("/epics", json);

        assertEquals(200, response.statusCode());
        Epic createdEpic = gson.fromJson(response.body(), Epic.class);
        assertNotNull(createdEpic.getId());
        assertEquals(epic.getName(), createdEpic.getName());
    }

    @Test
    void shouldCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Test Discription", epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createSubtask(subtask);
        String json = gson.toJson(taskManager.getSubtaskById(subtask.getId()));

        HttpResponse<String> response = post("/subtasks", json);

        assertEquals(200, response.statusCode());
        Subtask createdSubtask = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(createdSubtask.getId());
        assertEquals(subtask.getName(), createdSubtask.getName());
    }

    @Test
    void shouldReturnTasksInPriorityOrder() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description",
                Duration.ofHours(1), LocalDateTime.now().plusDays(2));
        Task task2 = new Task("Task 2", "Description",
                Duration.ofHours(1), LocalDateTime.now().plusDays(1));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpResponse<String> response = get("/prioritized");

        assertEquals(200, response.statusCode());
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(2, tasks.size());
        assertTrue(tasks.get(0).getStartTime().isBefore(tasks.get(1).getStartTime()));
    }

    @Test
    void shouldReturnEmptyHistory() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/history");

        assertEquals(200, response.statusCode());
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertTrue(history.isEmpty());
    }

    private HttpResponse<String> post(String path, String json) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
} 