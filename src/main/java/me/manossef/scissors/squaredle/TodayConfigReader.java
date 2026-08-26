package me.manossef.scissors.squaredle;

import me.manossef.scissors.Scissors;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TodayConfigReader {
    public static TodayConfig readTodayPuzzleConfig() {
        String config = getConfig();
        if(config == null) return null;
        Pattern pattern = Pattern.compile("const\\s+gTodayDateStr\\s*=\\s*'(.*?)';", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(config);
        String currentDate;
        if(matcher.find())
            currentDate = matcher.group(1);
        else return null;
        int jsonConstStart = config.indexOf("const gPuzzleConfig");
        int jsonConstEnd = config.indexOf("};", jsonConstStart);
        if(jsonConstStart == -1 || jsonConstEnd == -1) return null;
        String jsonConst = config.substring(jsonConstStart, jsonConstEnd + 1);
        String json = jsonConst.substring(jsonConst.indexOf("=") + 1).trim();
        PuzzleConfig puzzles = Scissors.GSON.fromJson(json, PuzzleConfig.class);
        return new TodayConfig(puzzles.puzzles(), currentDate);
    }

    private static String getConfig() {
        Request request = new Request.Builder().url("https://squaredle.app/api/today-puzzle-config.js").build();
        try(Response response = Scissors.HTTP_CLIENT.newCall(request).execute()) {
            if(!response.isSuccessful()) return null;
            return response.body().string();
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private record PuzzleConfig(Map<String, PuzzleData> puzzles) {
    }
}