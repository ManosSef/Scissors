package me.manossef.scissors.config;

public record OptionValue<T>(Option option, T value) {
    public OptionValue {
        if(option == null)
            throw new IllegalArgumentException("Option cannot be null");
        if(value != null && option.properties() instanceof OptionProperties.IntOptionProperties properties) {
            int intValue = (int) value;
            if(!properties.validate(intValue))
                throw new IllegalArgumentException("Option value (" + intValue + ") out of bounds (" + properties.getMin() + ", " + properties.getMax() + ")");
        }
    }
}
