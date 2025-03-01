package main.exception;

@SuppressWarnings("checkstyle:Regexp")
public class TaskValidationException extends RuntimeException {
    public TaskValidationException(String message) {
        super(message);
    }
} 