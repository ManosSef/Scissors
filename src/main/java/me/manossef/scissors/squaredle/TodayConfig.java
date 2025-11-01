package me.manossef.scissors.squaredle;

import java.util.Map;

public record TodayConfig(Map<String, PuzzleData> puzzles, String currentDate) {
}
