package me.manossef.scissors.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

public class LengthLimitedStringArgumentType implements ArgumentType<String> {
    private static final DynamicCommandExceptionType TOO_LONG = new DynamicCommandExceptionType(expected -> new LiteralMessage("Expected a string not longer than " + expected + " characters"));

    private final int maxLength;

    public LengthLimitedStringArgumentType(int maxLength) {
        this.maxLength = maxLength;
    }

    public static LengthLimitedStringArgumentType string(int maxLength) {
        return new LengthLimitedStringArgumentType(maxLength);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        int position = reader.getCursor();
        String argument = reader.readString();
        if(argument.length() > this.maxLength) {
            reader.setCursor(position);
            throw TOO_LONG.createWithContext(reader, this.maxLength);
        }
        return argument;
    }
}