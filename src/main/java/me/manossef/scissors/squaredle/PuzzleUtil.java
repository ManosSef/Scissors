package me.manossef.scissors.squaredle;

import me.manossef.commoncode.MyArrays;
import me.manossef.scissors.Emojis;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class PuzzleUtil {
    public static String getMessageText(PuzzleData puzzle) {
        StringBuilder builder = new StringBuilder();
        for(String row : puzzle.board()) {
            for(char letter : row.toCharArray())
                builder.append(getEmojiForLetter(letter).getFormatted()).append(" ");
            builder.append("\n");
        }
        builder.append("Word count: ").append(puzzle.words().length).append(" (");
        Map<Integer, Integer> lengthToCount = getLengthToCount(puzzle.words());
        StringBuilder wordCount = new StringBuilder();
        for(int i = 4; i < 33; i++) {
            Integer count = lengthToCount.get(i);
            if(count == null) continue;
            wordCount.append(count).append("x").append(i).append(", ");
        }
        if(wordCount.length() > 2) wordCount.delete(wordCount.length() - 2, wordCount.length());
        builder.append(wordCount)
            .append(")\nBonus word count: ")
            .append(puzzle.optionalWords().length)
            .append("\n");
        if(puzzle.wordOfTheDay() != null)
            builder.append("Bonus Word of the Day: ")
                .append(puzzle.censoredWOTD().replace("*", "\\*"))
                .append(" (")
                .append(puzzle.wordOfTheDay().term().length())
                .append(" letters)\n");
        if(puzzle.difficulty() != null)
            builder.append("Difficulty: ").append(puzzle.difficulty()).append(" ").append(Emojis.WHITE_MEDIUM_STAR.getFormatted()).append("\n");
        if(puzzle.credits() != null) {
            if(puzzle.credits().author() != null)
                builder.append("Puzzle created by ").append(puzzle.credits().author());
            else if(puzzle.credits().sponsor() != null)
                builder.append("Puzzle sponsored by ").append(puzzle.credits().sponsor());
            else
                builder.delete(wordCount.length() - 2, wordCount.length());
        } else
            builder.delete(wordCount.length() - 2, wordCount.length());
        return builder.toString();
    }

    private static Emoji getEmojiForLetter(char letter) {
        return switch(letter) {
            case 'a', 'A' -> Emojis.SQUAREDLE_A;
            case 'b', 'B' -> Emojis.SQUAREDLE_B;
            case 'c', 'C' -> Emojis.SQUAREDLE_C;
            case 'd', 'D' -> Emojis.SQUAREDLE_D;
            case 'e', 'E' -> Emojis.SQUAREDLE_E;
            case 'f', 'F' -> Emojis.SQUAREDLE_F;
            case 'g', 'G' -> Emojis.SQUAREDLE_G;
            case 'h', 'H' -> Emojis.SQUAREDLE_H;
            case 'i', 'I' -> Emojis.SQUAREDLE_I;
            case 'j', 'J' -> Emojis.SQUAREDLE_J;
            case 'k', 'K' -> Emojis.SQUAREDLE_K;
            case 'l', 'L' -> Emojis.SQUAREDLE_L;
            case 'm', 'M' -> Emojis.SQUAREDLE_M;
            case 'n', 'N' -> Emojis.SQUAREDLE_N;
            case 'o', 'O' -> Emojis.SQUAREDLE_O;
            case 'p', 'P' -> Emojis.SQUAREDLE_P;
            case 'q', 'Q' -> Emojis.SQUAREDLE_Q;
            case 'r', 'R' -> Emojis.SQUAREDLE_R;
            case 's', 'S' -> Emojis.SQUAREDLE_S;
            case 't', 'T' -> Emojis.SQUAREDLE_T;
            case 'u', 'U' -> Emojis.SQUAREDLE_U;
            case 'v', 'V' -> Emojis.SQUAREDLE_V;
            case 'w', 'W' -> Emojis.SQUAREDLE_W;
            case 'x', 'X' -> Emojis.SQUAREDLE_X;
            case 'y', 'Y' -> Emojis.SQUAREDLE_Y;
            case 'z', 'Z' -> Emojis.SQUAREDLE_Z;
            case '!' -> Emojis.SQUAREDLE_BANG;
            case '.' -> Emojis.SQUAREDLE_PERIOD;
            case '↑' -> Emojis.SQUAREDLE_UP;
            default -> Emojis.SQUAREDLE_BLANK;
        };
    }

    private static Map<Integer, Integer> getLengthToCount(String[] words) {
        Map<Integer, Integer> lengthToCount = new HashMap<>();
        for(String word : words) {
            int length = word.length();
            if(!lengthToCount.containsKey(length)) lengthToCount.put(length, 0);
            lengthToCount.put(length, lengthToCount.get(length) + 1);
        }
        return lengthToCount;
    }

    public static String decryptString(String encrypted) {
        String[] split = encrypted.split("");
        Object[] decrypted = Arrays.stream(split).map(PuzzleUtil::decipherChar).toArray();
        String data = MyArrays.join(decrypted, "");
        byte[] decoded = Base64.getDecoder().decode(data);
        return new String(decoded);
    }

    private static String decipherChar(String data) {
        String[] key = "5pyf0gcrl1a9oe3ui8d2htn67sqjkxbmw4vzPYFGCRLAOEUIDHTNSQJKXBMWVZ".split("");
        int index = MyArrays.indexOf(key, data);
        return -1 == index ? data : key[(index - 12 + key.length) % key.length];
    }
}