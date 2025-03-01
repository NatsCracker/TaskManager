package main.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.Duration;
import java.time.LocalDateTime;

public class GsonConfig {
    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter()) // Адаптер для Duration
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()) // Адаптер для LocalDateTime
                .create();
    }
}
