package me.manossef.scissors.config;

import java.util.HashMap;
import java.util.Map;

public enum Options {

    GPPCT_RESPONSES(BooleanOptions.GPPCT_RESPONSES),
    GPPCT_RESPONSE_CHANCE(IntOptions.GPPCT_RESPONSE_CHANCE),
    PING_RESPONSES(BooleanOptions.PING_RESPONSES),
    SCISSORS_RESPONSES(BooleanOptions.SCISSORS_RESPONSES),
    SCISSORS_RESPONSE_CHANCE(IntOptions.SCISSORS_RESPONSE_CHANCE),
    REACT_TO_PAPER(BooleanOptions.REACT_TO_PAPER);

    private static final Map<String, Option<?>> NAME_TO_OPTION = mapNamesToOptions();
    private final TypedOptions typedOption;

    Options(TypedOptions option) {

        this.typedOption = option;

    }

    private static Map<String, Option<?>> mapNamesToOptions() {

        Map<String, Option<?>> map = new HashMap<>();
        for(Options option : values())
            map.put(option.option().getName(), option.option());
        return map;

    }

    public static Option<?> fromName(String name) {

        return NAME_TO_OPTION.get(name);

    }

    public Option<?> option() {

        return this.typedOption.option();

    }

    public boolean isBoolean() {

        return this.typedOption instanceof BooleanOptions;

    }

    public boolean isInteger() {

        return this.typedOption instanceof IntOptions;

    }

    public Option<Boolean> getAsBoolean() {

        if(this.typedOption instanceof BooleanOptions)
            return ((BooleanOptions) typedOption).option();
        return null;

    }

    public Option<Integer> getAsInteger() {

        if(this.typedOption instanceof IntOptions)
            return ((IntOptions) typedOption).option();
        return null;

    }

    private interface TypedOptions {

        Option<?> option();

    }

    public enum BooleanOptions implements TypedOptions {

        GPPCT_RESPONSES(new Option<>("gppctResponses", Boolean.class, true)),
        PING_RESPONSES(new Option<>("pingResponses", Boolean.class, true)),
        SCISSORS_RESPONSES(new Option<>("scissorsResponses", Boolean.class, true)),
        REACT_TO_PAPER(new Option<>("reactToPaper", Boolean.class, true));

        private final Option<Boolean> option;

        BooleanOptions(Option<Boolean> option) {

            this.option = option;

        }

        public Option<Boolean> option() {

            return this.option;

        }

    }

    public enum IntOptions implements TypedOptions {

        GPPCT_RESPONSE_CHANCE(new Option.IntOption("gppctResponseChance", 10, 0, 100)),
        SCISSORS_RESPONSE_CHANCE(new Option.IntOption("scissorsResponseChance", 20, 0, 100));

        private final Option<Integer> option;

        IntOptions(Option<Integer> option) {

            this.option = option;

        }

        public Option<Integer> option() {

            return this.option;

        }

    }

}
