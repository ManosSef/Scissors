package me.manossef.scissors.config;

public class Option<T> {

    private final String name;
    private final Class<T> type;
    private final T defaultValue;

    Option(String name, Class<T> type, T defaultValue) {

        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;

    }

    public String getName() {

        return this.name;

    }

    public Class<T> getType() {

        return this.type;

    }

    public T getDefault() {

        return this.defaultValue;

    }

    public static class IntOption extends Option<Integer> {

        private final int min;
        private final int max;

        IntOption(String name, Integer defaultValue, int min, int max) {

            super(name, Integer.class, defaultValue);
            this.min = min;
            this.max = max;
            if(!validate(defaultValue))
                throw new IllegalArgumentException("Default option value (" + defaultValue + ") out of bounds (" + this.min + ", " + this.max + ")");

        }

        public int getMin() {

            return this.min;

        }

        public int getMax() {

            return this.max;

        }

        public boolean validate(int value) {

            return value >= min && value <= max;

        }

    }

}
