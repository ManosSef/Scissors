package me.manossef.scissors.config;

import com.mojang.brigadier.arguments.ArgumentType;

public class Option<T> {
    private final String name;
    private final Class<T> type;
    private final ArgumentType<T> argumentType;

    Option(String name, Class<T> type, ArgumentType<T> argumentType) {
        this.name = name;
        this.type = type;
        this.argumentType = argumentType;
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    public ArgumentType<T> getArgumentType() {
        return argumentType;
    }
}
