package me.manossef.scissors.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

public class LengthLimitedStringArgumentType implements ArgumentType<String> {
    private static final DynamicCommandExceptionType TOO_LONG = new DynamicCommandExceptionType(expected -> new LiteralMessage("Expected a string not longer than " + expected + " characters"));

    private final int maxLength;
    private final StringArgumentType.StringType stringType;

    private LengthLimitedStringArgumentType(int maxLength, StringArgumentType.StringType stringType) {
        this.maxLength = maxLength;
        this.stringType = stringType;
    }

    public static LengthLimitedStringArgumentType word(int maxLength) {
        return new LengthLimitedStringArgumentType(maxLength, StringArgumentType.StringType.SINGLE_WORD);
    }

    public static LengthLimitedStringArgumentType string(int maxLength) {
        return new LengthLimitedStringArgumentType(maxLength, StringArgumentType.StringType.QUOTABLE_PHRASE);
    }

    public static LengthLimitedStringArgumentType greedyString(int maxLength) {
        return new LengthLimitedStringArgumentType(maxLength, StringArgumentType.StringType.GREEDY_PHRASE);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        int position = reader.getCursor();
        String argument;
        if(this.stringType == StringArgumentType.StringType.GREEDY_PHRASE) {
            String text = reader.getRemaining();
            reader.setCursor(reader.getTotalLength());
            argument = text;
        } else if(this.stringType == StringArgumentType.StringType.SINGLE_WORD) argument = reader.readUnquotedString();
        else argument = reader.readString();
        if(argument.length() > this.maxLength) {
            reader.setCursor(position);
            throw TOO_LONG.createWithContext(reader, this.maxLength);
        }
        return argument;
    }
}