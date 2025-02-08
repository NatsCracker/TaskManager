package main.service;

import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.model.TaskStatus;
import main.model.TaskType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Collection;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    // Метод для сохранения данных в файл
    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();
            
            // Объединяем все задачи в один стрим
            Stream.of(getAllTasks(), getAllEpics(), getAllSubtasks())
                .flatMap(Collection::stream)
                .forEach(task -> {
                    try {
                        writer.write(task.toString());
                        writer.newLine();
                    } catch (IOException e) {
                        throw new ManagerSaveException("Ошибка при записи задачи: " + task, e);
                    }
                });
            
            writer.newLine();
            writer.write("History");
            writer.newLine();
            
            // Записываем историю
            String historyIds = history.getHistory().stream()
                .map(task -> String.valueOf(task.getId()))
                .collect(Collectors.joining(","));
            writer.write(historyIds);
            
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл: " + file.getPath(), e);
        }
    }

    // Метод для загрузки данных из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        if (file == null)
            throw new IllegalArgumentException("Файл не может быть null");
            
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        
        if (!file.exists()) {
            return manager;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            Optional.ofNullable(reader.readLine())
                .filter(header -> !header.isEmpty())
                .ifPresent(header -> {
                    List<Task> tasks = manager.readTasks(reader);
                    manager.createTasks(tasks);
                    manager.readHistory(reader);
                });
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки данных из файла: " + file.getPath(), e);
        }
        
        return manager;
    }

    // Метод для чтения задач из файла
    private List<Task> readTasks(BufferedReader reader) {
        try {
            return reader.lines()
                    .takeWhile(line -> !line.isBlank())
                    .map(FileBackedTaskManager::fromString)
                    .collect(Collectors.toList());
        } catch (UncheckedIOException e) {
            throw new ManagerSaveException("Ошибка при чтении задач из файла", e);
        }
    }

    // Метод для создания задач
    private void createTasks(List<Task> tasks) {
        // Сначала создаем эпики и обычные задачи
        tasks.stream()
                .filter(task -> !(task instanceof Subtask))
                .forEach(task -> {
                    if (task instanceof Epic) {
                        createEpic((Epic) task);
                    } else {
                        createTask(task);
                    }
                });

        // Затем создаем подзадачи
        tasks.stream()
                .filter(task -> task instanceof Subtask)
                .map(task -> (Subtask) task)
                .forEach(this::createSubtask);
    }

    // Метод для чтения истории
    private void readHistory(BufferedReader reader) {
        try {
            reader.lines()
                    .dropWhile(line -> !line.equals("History"))
                    .skip(1)
                    .findFirst()
                    .filter(line -> !line.isEmpty())
                    .map(line -> line.split(","))
                    .ifPresent(taskIds -> Arrays.stream(taskIds)
                            .map(Integer::parseInt)
                            .map(this::getTaskById)
                            .filter(Objects::nonNull)
                            .forEach(history::add));
        } catch (UncheckedIOException e) {
            throw new ManagerSaveException("Ошибка при чтении истории из файла", e);
        }
    }

    // Метод для преобразования из строки в задачу
    private static Task fromString(String value) {
<<<<<<< HEAD
<<<<<<< HEAD
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        Duration duration = Duration.ofMinutes(Long.parseLong(parts[5]));
        LocalDateTime startTime = parts[6].isEmpty() ? null : LocalDateTime.parse(parts[6]);

        Task task;
=======
        if (value == null || value.isEmpty()) {
            return null;
        }
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
>>>>>>> 1cac1a9 (V3.7)
=======
        if (value == null || value.isEmpty()) {
            return null;
        }
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
>>>>>>> 84824b1 (V3.7)
        switch (type) {
            case TASK:
                Task task = new Task(parts[2], parts[4]);
                task.setId(id);
                task.setStatus(TaskStatus.valueOf(parts[3]));
                return task;
            case SUBTASK:
<<<<<<< HEAD
<<<<<<< HEAD
                int epicId = Integer.parseInt(parts[7]);
                task = new Subtask(name, description, epicId, duration, startTime);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
=======
                Subtask subtask = new Subtask(parts[2], parts[4], 
                        Integer.parseInt(parts[5]));
                subtask.setId(id);
                subtask.setStatus(TaskStatus.valueOf(parts[3]));
                return subtask;
>>>>>>> 1cac1a9 (V3.7)
        }
        if (file == null) {
            throw new IllegalArgumentException("Файл не может быть null");
        }
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        
        if (!file.exists()) {
            return manager;
        }
        
        return null; // Placeholder return, actual implementation needed
=======
                Subtask subtask = new Subtask(parts[2], parts[4], 
                        Integer.parseInt(parts[5]));
                subtask.setId(id);
                subtask.setStatus(TaskStatus.valueOf(parts[3]));
                return subtask;
            case EPIC:
                Epic epic = new Epic(parts[2], parts[4]);
                epic.setId(id);
                epic.setStatus(TaskStatus.valueOf(parts[3]));
                return epic;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
>>>>>>> 84824b1 (V3.7)
    }

    // Метод для создания задачи
    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    // Метод для создания эпика
    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    // Метод для создания подзадачи
    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    // Метод для удаления задачи
    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    // Метод для удаления эпика
    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    // Метод для удаления подзадачи
    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    // Метод для удаления всех задач
    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    // Метод для удаления всех эпиков
    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    // Метод для удаления всех подзадач
    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    // Метод для обновления задачи
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    // Метод для обновления эпика
    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    // Метод для обновления подзадачи
    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    // Метод для получения истории
    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}