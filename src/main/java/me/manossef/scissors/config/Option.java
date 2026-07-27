package me.manossef.scissors.config;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class Option<T> {
    private final String name;
    private final Class<T> type;
    private final ArgumentType<T> argumentType;

    private Option(String name, Class<T> type, ArgumentType<T> argumentType) {
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

    static Option<Boolean> bool(String name) {
        return new Option<>(name, Boolean.class, BoolArgumentType.bool());
    }

    static Option<Integer> integer(String name) {
        return integer(name, IntegerArgumentType.integer());
    }

    static Option<Integer> integer(String name, int min) {
        return integer(name, IntegerArgumentType.integer(min));
    }

    static Option<Integer> integer(String name, int min, int max) {
        return integer(name, IntegerArgumentType.integer(min, max));
    }

    private static Option<Integer> integer(String name, ArgumentType<Integer> argumentType) {
        return new Option<>(name, Integer.class, argumentType);
    }
}
