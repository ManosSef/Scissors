package me.manossef.scissors.config;

import java.util.Objects;

public record OptionValue<T>(Option<T> option, T value) {
    public OptionValue {
        Objects.requireNonNull(option, "Option cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");
        if(!option.isValid(value))
            throw new IllegalArgumentException(value + " is not valid as a value for the option " + option.getName());
    }
}
