package me.manossef.scissors;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

@FunctionalInterface
public interface LazilyFormattedText {
    String format(ChatCommandSource source);

    record ExceptionType(LazilyFormattedText text) implements CommandExceptionType {
        public CommandSyntaxException create(ChatCommandSource source) {
            return new CommandSyntaxException(this, new LiteralMessage(text.format(source)));
        }
    }
}