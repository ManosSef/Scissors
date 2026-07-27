package me.manossef.scissors.config;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import java.util.Objects;

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

    public boolean equals(Object other) {
        if(other == null) return false;
        if(other == this) return true;
        if(!(other instanceof Option<?> otherOption)) return false;
        return Objects.equals(this.name, otherOption.getName()) && Objects.equals(this.type, otherOption.getType());
    }

    public int hashCode() {
        return Objects.hash(this.name, this.type);
    }

    public String toString() {
        return "Option[name=" + this.name + ", type=" + this.type + ", defaultValue=" + this.defaultValue + "]";
    }

    static Option<Boolean> bool(String name, boolean defaultValue) {
        return new Option<>(name, Boolean.class, BoolArgumentType.bool(), defaultValue);
    }

    static Option<Integer> integer(String name, int defaultValue) {
        return integer(name, IntegerArgumentType.integer(), defaultValue);
    }

    static Option<Integer> integer(String name, int defaultValue, int min) {
        if(defaultValue < min)
            throw new IllegalArgumentException("The default value must be greater than or equal to min");
        return integer(name, IntegerArgumentType.integer(min), defaultValue);
    }

    static Option<Integer> integer(String name, int defaultValue, int min, int max) {
        if(defaultValue < min || defaultValue > max)
            throw new IllegalArgumentException("The default value must be between min and max inclusive");
        return integer(name, IntegerArgumentType.integer(min, max), defaultValue);
    }

    private static Option<Integer> integer(String name, ArgumentType<Integer> argumentType, int defaultValue) {
        return new Option<>(name, Integer.class, argumentType, defaultValue);
    }
}