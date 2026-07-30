package me.manossef.scissors.config;

import java.util.*;

public final class Settings {
    private final Map<Option<?>, OptionValue<?>> values;

    Settings() {
        this.values = new TreeMap<>((o1, o2) -> Comparator.<String>naturalOrder().compare(o1.getName(), o2.getName()));
    }

    Settings(Set<OptionValue<?>> values) {
        this();
        for(OptionValue<?> value : values) this.values.put(value.option(), value);
    }

    public <T> Optional<T> get(Option<T> option) {
        Objects.requireNonNull(option, "Option cannot be null");
        OptionValue<?> value = values.get(option);
        if(value == null) return Optional.empty();
        return Optional.ofNullable(option.getType().cast(value.value()));
    }

    public <T> boolean set(Option<T> option, T value) {
        Objects.requireNonNull(option, "Option cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");
        OptionValue<T> optionValue = new OptionValue<>(option, value);
        return !optionValue.equals(values.put(option, optionValue));
    }

    public <T> boolean removeExplicit(Option<T> option) {
        Objects.requireNonNull(option, "Option cannot be null");
        return values.remove(option) != null;
    }

    public boolean resetToDefault() {
        if(values.isEmpty()) return false;
        values.clear();
        return true;
    }

    Collection<OptionValue<?>> getValues() {
        return values.values();
    }

    public String toString() {
        return "Settings[values=" + values.values() + "]";
    }
}