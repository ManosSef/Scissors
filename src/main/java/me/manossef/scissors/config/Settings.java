package me.manossef.scissors.config;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Settings {
    private final Set<OptionValue<?>> values;

    public Settings() {
        this.values = new TreeSet<>((o1, o2) -> Comparator.<String>naturalOrder().compare(o1.option().properties().getName(), o2.option().properties().getName()));
    }

    public boolean isPresent(Option option) {
        for(OptionValue<?> optionValue : values) {
            if(optionValue.option().equals(option))
                return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Option option, Class<T> type) {
        if(option == null)
            throw new IllegalArgumentException("Option cannot be null");
        if(!option.properties().getType().equals(type))
            throw new IllegalArgumentException("Option is not of the given type");
        Object value = null;
        for(OptionValue<?> optionValue : values) {
            if(optionValue.option().equals(option))
                value = optionValue.value();
        }
        if(value == null)
            return (T) option.properties().getDefault();
        if(!(value.getClass().equals(option.properties().getType())))
            throw new IllegalStateException("Option of type " + option.properties().getType() + " with value of type " + value);
        return (T) value;
    }

    public <T> void set(Option option, T value) {
        if(option == null)
            throw new IllegalArgumentException("Option cannot be null");
        if(value == null)
            throw new IllegalArgumentException("Option value cannot be null");
        values.removeIf(optionValue -> optionValue.option().equals(option));
        values.add(new OptionValue<>(option, value));
    }

    public boolean resetToDefault() {
        if(values.isEmpty())
            return false;
        values.clear();
        return true;
    }

    public String toString() {
        return "Settings[values=" + values + "]";
    }
}