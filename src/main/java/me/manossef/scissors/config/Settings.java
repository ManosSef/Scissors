package me.manossef.scissors.config;

import java.util.HashMap;
import java.util.Map;

public class Settings {

    private final Map<Option<?>, OptionValue<?>> values;

    public Settings() {

        this.values = new HashMap<>();

    }

    public boolean isPresent(Option<?> option) {

        return values.containsKey(option);

    }

    public <T> T get(Options option, Class<T> type) {

        if(option == null)
            throw new IllegalArgumentException("Option cannot be null");
        if(!option.option().getType().equals(type))
            throw new IllegalArgumentException("Option is not of the given type");
        if(values.get(option.option()) == null)
            return (T) option.option().getDefault();
        Object value = values.get(option.option()).value();
        if(!(value.getClass().equals(option.option().getType())))
            throw new IllegalStateException("Option of type " + option.option().getType() + " with value of type " + value);
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
