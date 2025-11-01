package me.manossef.scissors.squaredle;

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
                builder.append(getEmojiForLetter(letter)).append(" ");
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
            builder.append("Difficulty: ").append(puzzle.difficulty()).append(" ⭐\n");
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

            case 'a', 'A' -> Emoji.fromCustom("squaredleA", 1434215203662463187L, false);
            case 'b', 'B' -> Emoji.fromCustom("squaredleB", 1434215202118697071L, false);
            case 'c', 'C' -> Emoji.fromCustom("squaredleC", 1434215165297164348L, false);
            case 'd', 'D' -> Emoji.fromCustom("squaredleD", 1434215200591970305L, false);
            case 'e', 'E' -> Emoji.fromCustom("squaredleE", 1434215199480746174L, false);
            case 'f', 'F' -> Emoji.fromCustom("squaredleF", 1434215198025056266L, false);
            case 'g', 'G' -> Emoji.fromCustom("squaredleG", 1434215196439871588L, false);
            case 'h', 'H' -> Emoji.fromCustom("squaredleH", 1434215194740920524L, false);
            case 'i', 'I' -> Emoji.fromCustom("squaredleI", 1434215193335824465L, false);
            case 'j', 'J' -> Emoji.fromCustom("squaredleJ", 1434215191809228800L, false);
            case 'k', 'K' -> Emoji.fromCustom("squaredleK", 1434215190383165542L, false);
            case 'l', 'L' -> Emoji.fromCustom("squaredleL", 1434215188734803989L, false);
            case 'm', 'M' -> Emoji.fromCustom("squaredleM", 1434215187459739658L, false);
            case 'n', 'N' -> Emoji.fromCustom("squaredleN", 1434215186063032590L, false);
            case 'o', 'O' -> Emoji.fromCustom("squaredleO", 1434215184565670119L, false);
            case 'p', 'P' -> Emoji.fromCustom("squaredleP", 1434215183210774650L, false);
            case 'q', 'Q' -> Emoji.fromCustom("squaredleQ", 1434215181533315273L, false);
            case 'r', 'R' -> Emoji.fromCustom("squaredleR", 1434215180086153448L, false);
            case 's', 'S' -> Emoji.fromCustom("squaredleS", 1434215178588782623L, false);
            case 't', 'T' -> Emoji.fromCustom("squaredleT", 1434215176948809898L, false);
            case 'u', 'U' -> Emoji.fromCustom("squaredleU", 1434215175078285363L, false);
            case 'v', 'V' -> Emoji.fromCustom("squaredleV", 1434215173937172510L, false);
            case 'w', 'W' -> Emoji.fromCustom("squaredleW", 1434215172586868897L, false);
            case 'x', 'X' -> Emoji.fromCustom("squaredleX", 1434215170938372196L, false);
            case 'y', 'Y' -> Emoji.fromCustom("squaredleY", 1434215169696727083L, false);
            case 'z', 'Z' -> Emoji.fromCustom("squaredleZ", 1434215168547487824L, false);
            case '!' -> Emoji.fromCustom("squaredleBang", 1434215167117361234L, false);
            case '.' -> Emoji.fromCustom("squaredlePeriod", 1434216521626026166L, false);
            case '↑' -> Emoji.fromCustom("squaredleUp", 1434228465527296123L, false);
            default -> Emoji.fromCustom("squaredleBlank", 1434228466999492770L, false);

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
        String data = join(decrypted);
        byte[] decoded = Base64.getDecoder().decode(data);
        return new String(decoded);

    }

    private static String decipherChar(String data) {

        String[] key = "5pyf0gcrl1a9oe3ui8d2htn67sqjkxbmw4vzPYFGCRLAOEUIDHTNSQJKXBMWVZ".split("");
        int index = indexOf(key, data);
        return -1 == index ? data : key[(index - 12 + key.length) % key.length];

    }

    private static <T> int indexOf(T[] array, T toFind) {

        for(int i = 0; i < array.length; i++)
            if(toFind.equals(array[i]))
                return i;
        return -1;

    }

    private static <T> String join(T[] array) {

        StringBuilder builder = new StringBuilder();
        for(T t : array)
            builder.append(t.toString());
        return builder.toString();

    }

}
