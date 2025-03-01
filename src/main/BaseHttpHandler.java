package main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected static final String CONTENT_TYPE = "Content-Type";
    protected static final String APPLICATION_JSON = "application/json";

    // Абстрактный метод handle, реализация которого предоставят подклассы
    @Override
    public abstract void handle(HttpExchange exchange) throws IOException;

    protected String readBody(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    protected int extractIdFromPath(String path) {
        return Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        sendResponse(exchange, 400, message);
    }

    protected int extractId(String query) {
        String[] parts = query.split("=");
        return Integer.parseInt(parts[1]);
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, 200, text);
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        sendResponse(exchange, 404, message);
    }

    protected void sendHasInteractions(HttpExchange exchange, String message) throws IOException {
        sendResponse(exchange, 406, message);
    }

    protected void sendServerError(HttpExchange exchange, String message) throws IOException {
        sendResponse(exchange, 500, message);
    }

    protected void sendResponse(HttpExchange exchange, int code, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add(CONTENT_TYPE, APPLICATION_JSON);
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}

