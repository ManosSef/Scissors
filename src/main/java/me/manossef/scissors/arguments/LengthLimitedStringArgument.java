package me.manossef.scissors.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class LengthLimitedStringArgument implements ArgumentType<String> {
    private static final SimpleCommandExceptionType TOO_LONG = new SimpleCommandExceptionType(new LiteralMessage("The given string is too long"));

    private final int maxLength;

    public LengthLimitedStringArgument(int maxLength) {
        this.maxLength = maxLength;
    }

    public static LengthLimitedStringArgument of(int maxLength) {
        return new LengthLimitedStringArgument(maxLength);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        int position = reader.getCursor();
        String argument = reader.readString();
        if(argument.length() > maxLength) {
            reader.setCursor(position);
            throw TOO_LONG.createWithContext(reader);
        }
        return argument;
    }
}