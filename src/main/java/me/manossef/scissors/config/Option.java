package me.manossef.scissors.config;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public final class Option<T> {
    private final String name;
    private final Class<T> type;
    private final ArgumentType<T> argumentType;
    private final T defaultValue;

    private Option(String name, Class<T> type, ArgumentType<T> argumentType, T defaultValue) {
        this.name = name;
        this.type = type;
        this.argumentType = argumentType;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getType() {
        return this.type;
    }

    public ArgumentType<T> getArgumentType() {
        return this.argumentType;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    static Option<Boolean> bool(String name, boolean defaultValue) {
        return new Option<>(name, Boolean.class, BoolArgumentType.bool(), defaultValue);
    }

    static Option<Integer> integer(String name, int defaultValue) {
        return integer(name, IntegerArgumentType.integer(), defaultValue);
    }

    static Option<Integer> integer(String name, int defaultValue, int min) {
        return integer(name, IntegerArgumentType.integer(min), defaultValue);
    }

    static Option<Integer> integer(String name, int defaultValue, int min, int max) {
        return integer(name, IntegerArgumentType.integer(min, max), defaultValue);
    }

    private static Option<Integer> integer(String name, ArgumentType<Integer> argumentType, int defaultValue) {
        return new Option<>(name, Integer.class, argumentType, defaultValue);
    }
}
