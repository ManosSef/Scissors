package me.manossef.scissors;

import com.google.gson.JsonSyntaxException;
import me.manossef.scissors.config.InvalidConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Resources {
    private static final Logger LOGGER = LoggerFactory.getLogger(Resources.class);
    private static final String FILE_DIRECTORY = "storage";

    public static <T> T getJsonFromFile(String fileName, Class<T> type) {
        String json = "";
        try {
            json = Files.readString(Path.of(FILE_DIRECTORY, fileName));
            return Scissors.GSON.fromJson(json, type);
        } catch(IOException e) {
            LOGGER.error("Failed to read the {} file.", fileName);
            return null;
        } catch(JsonSyntaxException e) {
            LOGGER.error("Invalid JSON in {}:\n{}", fileName, json, e);
            return null;
        } catch(InvalidConfigurationException e) {
            LOGGER.error("Invalid configuration:\n{}", json, e);
            return null;
        }
    }

    public static void saveJsonToFile(String fileName, Object object) {
        try {
            Files.writeString(Path.of(FILE_DIRECTORY, fileName), Scissors.GSON.toJson(object));
        } catch(IOException e) {
            LOGGER.error("Failed to save the {} file.", fileName);
        }
    }

    public static void loadWords(String fileName, List<String> list, String logError) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(
            Objects.requireNonNull(Scissors.class.getResourceAsStream("/" + fileName))))) {
            list.clear();
            list.addAll(reader.lines().toList());
        } catch(IOException e) {
            LOGGER.error("Failed to load the list of words from the {} file.", fileName, e);
            DevGuild.logStatus(logError);
        }
    }
}