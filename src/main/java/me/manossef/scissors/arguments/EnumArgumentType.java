package me.manossef.scissors.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.commoncode.TypeChecks;

public class EnumArgumentType<E extends Enum<E>> implements ArgumentType<E> {
    private static final SimpleCommandExceptionType INVALID_CONSTANT = new SimpleCommandExceptionType(new LiteralMessage("Invalid value"));

    private final Class<E> type;

    public EnumArgumentType(Class<E> type) {
        this.type = type;
    }

    public static <T extends Enum<T>> EnumArgumentType<T> enumArg(Class<T> type) {
        return new EnumArgumentType<>(type);
    }

    @Override
    public E parse(StringReader reader) throws CommandSyntaxException {
        String remaining = reader.getRemaining().split(" ")[0];
        if(TypeChecks.isEnum(remaining, this.type)) {
            reader.setCursor(reader.getCursor() + remaining.length());
            return Enum.valueOf(this.type, remaining);
        }
        throw INVALID_CONSTANT.createWithContext(reader);
    }
}