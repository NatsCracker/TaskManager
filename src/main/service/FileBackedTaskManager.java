package main.service;

import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.model.TaskStatus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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
            
            List<Task> allTasks = new ArrayList<>();
            allTasks.addAll(getAllTasks());
            allTasks.addAll(getAllEpics());
            allTasks.addAll(getAllSubtasks());
            
            for (Task task : allTasks) {
                writer.write(toString(task));
                writer.newLine();
            }
            
            writer.newLine();
            writer.write("History");
            writer.newLine();
            for (Task task : history.getHistory()) {
                writer.write(String.valueOf(task.getId()));
                writer.write(",");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл: " + file.getPath(), e);
        }
    }

    // Метод для загрузки данных из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException("Файл не может быть null");
        }
        
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        if (!file.exists()) {
            return manager;
        }

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            // Читаем задачи
            String header = reader.readLine();
            if (header == null || header.isEmpty()) {
                return manager;
            }

            List<Task> tasks = readTasks(reader);
            createTasks(manager, tasks);
            readHistory(reader, manager);
            
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки данных из файла: " + file.getPath(), e);
        }
        return manager;
    }

    // Метод для чтения задач из файла
    private static List<Task> readTasks(BufferedReader reader) throws IOException {
        List<Task> tasks = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            tasks.add(fromString(line));
        }
        return tasks;
    }

    // Метод для создания задач
    private static void createTasks(FileBackedTaskManager manager, List<Task> tasks) {
        // Сначала создаём обычные задачи и эпики
        for (Task task : tasks) {
            if (task instanceof Epic) {
                manager.createEpic((Epic) task);
            } else if (!(task instanceof Subtask)) {
                manager.createTask(task);
            }
        }
        
        // Затем создаём подзадачи
        for (Task task : tasks) {
            if (task instanceof Subtask) {
                manager.createSubtask((Subtask) task);
            }
        }
    }

    // Метод для чтения истории
    private static void readHistory(BufferedReader reader, FileBackedTaskManager manager) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals("History")) {
                String historyLine = reader.readLine();
                if (historyLine != null && !historyLine.isEmpty()) {
                    String[] taskIds = historyLine.split(",");
                    for (String taskId : taskIds) {
                        int id = Integer.parseInt(taskId);
                        Task task = manager.getTaskById(id);
                        if (task == null) {
                            task = manager.getEpicById(id);
                            if (task == null) {
                                task = manager.getSubtaskById(id);
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    private static String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                getTypeTask(task),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                (task instanceof Subtask subtask) ? subtask.getEpicId() : ""
        );
    }

    // Метод для преобразования из строки в задачу
    private static Task fromString(String value) {
        String[] fields = value.split(",");
        if (fields.length < 5) {
            throw new IllegalArgumentException("Некорректная строка CSV: " + value);
        }

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        Task task;
        if (type == TaskType.TASK) {
            task = new Task(name, description);
        } else if (type == TaskType.EPIC) {
            task = new Epic(name, description);
        } else if (type == TaskType.SUBTASK) {
            if (fields.length < 6) {
                throw new IllegalArgumentException("Для Subtask отсутствует epicId: " + value);
            }
            int epicId = Integer.parseInt(fields[5]);
            task = new Subtask(name, description, epicId);
        } else {
            throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }

        task.setId(id);
        task.setStatus(status);
        return task;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    // Метод для получения типа задачи
    private static TaskType getTypeTask(Task task) {
        if (task instanceof Subtask) {
            return TaskType.SUBTASK;
        } else if (task instanceof Epic) {
            return TaskType.EPIC;
        }
        return TaskType.TASK;
    }
}
