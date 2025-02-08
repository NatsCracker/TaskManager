package main.service;

import main.model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
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
            writer.write("id,type,name,status,description,epic,startTime,duration");
            writer.newLine();
            List<Task> allTasks = new ArrayList<>();
            allTasks.addAll(getAllTasks());
            allTasks.addAll(getAllEpics());
            allTasks.addAll(getAllSubtasks());
            for (Task task : allTasks) {
                writer.write(task.toString());
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
            String header = reader.readLine();
            if (header == null || header.isEmpty()) {
                return manager;
            }
            List<Task> tasks = manager.readTasks(reader);
            manager.createTasks(tasks);
            manager.readHistory(reader);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки данных из файла: " + file.getPath(), e);
        }
        return manager;
    }

    // Метод для чтения задач из файла
    private List<Task> readTasks(BufferedReader reader) throws IOException {
        List<Task> tasks = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            tasks.add(fromString(line));
        }
        return tasks;
    }

    // Метод для создания задач
    private void createTasks(List<Task> tasks) {
        for (Task task : tasks) {
            if (task instanceof Epic) {
                createEpic((Epic) task);
            } else if (!(task instanceof Subtask)) {
                createTask(task);
            }
        }
        for (Task task : tasks) {
            if (task instanceof Subtask) {
                createSubtask((Subtask) task);
            }
        }
    }

    // Метод для чтения истории
    private void readHistory(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals("History")) {
                String historyLine = reader.readLine();
                if (historyLine != null && !historyLine.isEmpty()) {
                    String[] taskIds = historyLine.split(",");
                    for (String taskId : taskIds) {
                        int id = Integer.parseInt(taskId);
                        Task task = getTaskById(id);
                        if (task == null) {
                            task = getEpicById(id);
                            if (task == null) {
                                task = getSubtaskById(id);
                            }
                        }
                        if (task != null) {
                            history.add(task);
                        }
                    }
                }
                break;
            }
        }
    }

    // Метод для преобразования из строки в задачу
    private static Task fromString(String value) {
        // Используем split с лимитом -1, чтобы сохранить пустые поля
        String[] fields = value.split(",", -1);
        if (fields.length == 7) {
            // Если строка содержит 7 полей, добавляем недостающее поле (duration) со значением "null"
            String[] temp = new String[8];
            System.arraycopy(fields, 0, temp, 0, 7);
            temp[7] = "null";
            fields = temp;
        } else if (fields.length != 8) {
            throw new IllegalArgumentException("Некорректная строка CSV: " + value);
        }

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        String epicId = fields[5];
        // Если поле пустое или равно "null", присваиваем null
        LocalDateTime startTime = ("null".equals(fields[6]) || fields[6].isEmpty())
                ? null
                : LocalDateTime.parse(fields[6]);
        Duration duration = ("null".equals(fields[7]) || fields[7].isEmpty())
                ? null
                : Duration.parse(fields[7]);

        Task task;
        switch (type) {
            case TASK:
                task = new Task(name, description, duration, startTime);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            case SUBTASK:
                task = new Subtask(name, description, Integer.parseInt(epicId), duration, startTime);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }

        task.setId(id);
        task.setStatus(status);
        return task;
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