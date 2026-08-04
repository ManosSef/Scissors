package me.manossef.scissors.config;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import java.util.Objects;
import java.util.function.Predicate;

public sealed class Option<T> {
    private final String name;
    private final Class<T> type;
    private final Predicate<T> validator;
    private final ArgumentType<T> argumentType;
    private final T defaultValue;

    private Option(String name, Class<T> type, Predicate<T> validator, ArgumentType<T> argumentType, T defaultValue) {
        this.name = name;
        this.type = type;
        this.validator = validator;
        this.argumentType = argumentType;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getType() {
        return this.type;
    }

    public boolean isValid(T value) {
        return this.validator.test(value);
    }

    public ArgumentType<T> getArgumentType() {
        return this.argumentType;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public OptionValue<T> castValue(Object value) {
        return new OptionValue<>(this, type.cast(value));
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
        return new Option<>(name, Boolean.class, b -> true, BoolArgumentType.bool(), defaultValue);
    }

    @SuppressWarnings("SameParameterValue")
    static Option<Integer> integer(String name, int defaultValue, int min, int max) {
        if(defaultValue < min || defaultValue > max)
            throw new IllegalArgumentException("The default value must be between min and max inclusive");
        return integer(name, i -> i >= min && i <= max, IntegerArgumentType.integer(min, max), defaultValue);
    }

    private static Option<Integer> integer(String name, Predicate<Integer> validator, ArgumentType<Integer> argumentType, int defaultValue) {
        return new IntOption(name, validator, argumentType, defaultValue);
    }

    private static final class IntOption extends Option<Integer> {
        private IntOption(String name, Predicate<Integer> validator, ArgumentType<Integer> argumentType, Integer defaultValue) {
            super(name, Integer.class, validator, argumentType, defaultValue);
        }

        public OptionValue<Integer> castValue(Object value) {
            if(value instanceof Number number) {
                int i = number.intValue();
                return new OptionValue<>(this, i);
            }
            throw new ClassCastException("Cannot cast " + value.getClass() + " to java.lang.Integer");
        }
    }
}