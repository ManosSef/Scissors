package me.manossef.scissors.config;

import java.util.HashMap;
import java.util.Map;

public enum Option {
    GPPCT_RESPONSES(BooleanOption.GPPCT_RESPONSES),
    GPPCT_RESPONSE_CHANCE(IntOption.GPPCT_RESPONSE_CHANCE),
    PING_RESPONSES(BooleanOption.PING_RESPONSES),
    SCISSORS_RESPONSES(BooleanOption.SCISSORS_RESPONSES),
    SCISSORS_RESPONSE_CHANCE(IntOption.SCISSORS_RESPONSE_CHANCE),
    REACT_TO_PAPER(BooleanOption.REACT_TO_PAPER);

    private static final Map<String, Option> NAME_TO_OPTION = mapNamesToOptions();
    private final TypedOption typedOption;

    Option(TypedOption option) {
        this.typedOption = option;
    }

    private static Map<String, Option> mapNamesToOptions() {
        Map<String, Option> map = new HashMap<>();
        for(Option option : values())
            map.put(option.properties().getName(), option);
        return map;
    }

    public static Option fromName(String name) {
        return NAME_TO_OPTION.get(name);
    }

    public OptionProperties<?> properties() {
        return this.typedOption.properties();
    }

    public boolean isBoolean() {
        return this.typedOption instanceof BooleanOption;
    }

    public boolean isInteger() {
        return this.typedOption instanceof IntOption;
    }

    public OptionProperties<Boolean> getAsBoolean() {
        if(this.typedOption instanceof BooleanOption)
            return ((BooleanOption) typedOption).properties();
        return null;
    }

    public OptionProperties<Integer> getAsInteger() {
        if(this.typedOption instanceof IntOption)
            return ((IntOption) typedOption).properties();
        return null;
    }

    private interface TypedOption {
        OptionProperties<?> properties();
    }

    public enum BooleanOption implements TypedOption {
        GPPCT_RESPONSES(new OptionProperties<>("gppctResponses", Boolean.class, true)),
        PING_RESPONSES(new OptionProperties<>("pingResponses", Boolean.class, true)),
        SCISSORS_RESPONSES(new OptionProperties<>("scissorsResponses", Boolean.class, true)),
        REACT_TO_PAPER(new OptionProperties<>("reactToPaper", Boolean.class, true));

        private final OptionProperties<Boolean> properties;

        BooleanOption(OptionProperties<Boolean> properties) {
            this.properties = properties;
        }

        public OptionProperties<Boolean> properties() {
            return this.properties;
        }
    }

    public enum IntOption implements TypedOption {
        GPPCT_RESPONSE_CHANCE(new OptionProperties.IntOptionProperties("gppctResponseChance", 10, 0, 100)),
        SCISSORS_RESPONSE_CHANCE(new OptionProperties.IntOptionProperties("scissorsResponseChance", 20, 0, 100));

        private final OptionProperties<Integer> properties;

        IntOption(OptionProperties<Integer> properties) {
            this.properties = properties;
        }

        public OptionProperties<Integer> properties() {
            return this.properties;
        }
    }
}