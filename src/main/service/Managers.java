package main.service;

import java.io.File;

@SuppressWarnings("checkstyle:Regexp")
public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("tasks.csv"));
    }
} 