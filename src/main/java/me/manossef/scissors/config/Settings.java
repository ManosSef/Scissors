package me.manossef.scissors.config;

import java.util.HashMap;
import java.util.Map;

public class Settings {

    private final Map<Option<?>, OptionValue<?>> values;

    public Settings() {

        this.values = new HashMap<>();

    }

    public Settings(Map<Option<?>, OptionValue<?>> values) {

        this.values = values;

    }

    public boolean isPresent(Option<?> option) {

        return values.containsKey(option);

    }

    public <T> T get(Option<T> option) {

        if(option == null)
            throw new IllegalArgumentException("Option cannot be null");
        if(values.get(option) == null)
            return option.getDefault();
        Object value = values.get(option).value();
        if(!(value.getClass().equals(option.getType())))
            throw new IllegalStateException("Option of type " + option.getType() + " with value of type " + value);
        return (T) value;

    }

    public <T> void set(Option<T> option, T value) {

        if(option == null)
            throw new IllegalArgumentException("Option cannot be null");
        if(value == null)
            throw new IllegalArgumentException("Option value cannot be null");
        values.put(option, new OptionValue<>(option, value));

    }

    public void resetToDefault() {

        values.clear();

    }

}
