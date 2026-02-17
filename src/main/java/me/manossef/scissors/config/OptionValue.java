package me.manossef.scissors.config;

public record OptionValue<T>(Option<T> option, T value) {

    public OptionValue {

        if(value != null && option instanceof Option.IntOption intOption) {

            int intValue = (int) value;
            if(!intOption.validate(intValue))
                throw new IllegalArgumentException("Option value (" + intValue + ") out of bounds (" + intOption.getMin() + ", " + intOption.getMax() + ")");

        }

    }

}
