package me.manossef.scissors;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.manossef.scissors.commands.core.CommandSource;

@FunctionalInterface
public interface LazilyFormattedText {
    String format(CommandSource source);

    record ExceptionType(LazilyFormattedText text) implements CommandExceptionType {
        public CommandSyntaxException create(CommandSource source) {
            return new CommandSyntaxException(this, new LiteralMessage(text.format(source)));
        }
    }
}