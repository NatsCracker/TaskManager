package main.exception;

public class TaskTimeIntersectionException extends RuntimeException {
    public TaskTimeIntersectionException(String message) {
        super(message);
    }
}