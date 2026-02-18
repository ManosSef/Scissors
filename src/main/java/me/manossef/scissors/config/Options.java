package me.manossef.scissors.config;

import java.util.HashMap;
import java.util.Map;

public enum Options {

    GPPCT_RESPONSES(new Option<>("gppctResponses", Boolean.class, true)),
    GPPCT_RESPONSE_CHANCE(new Option.IntOption("gppctResponseChance", 10, 0, 100)),
    PING_RESPONSES(new Option<>("pingResponses", Boolean.class, true)),
    SCISSORS_RESPONSES(new Option<>("scissorsResponses", Boolean.class, true)),
    SCISSORS_RESPONSE_CHANCE(new Option.IntOption("scissorsResponseChance", 20, 0, 100)),
    REACT_TO_PAPER(new Option<>("reactToPaper", Boolean.class, true));

    static final Map<String, Option<?>> NAME_TO_OPTION = mapNamesToOptions();
    private final Option<?> option;

    Options(Option<?> option) {

        this.option = option;

    }

    private static Map<String, Option<?>> mapNamesToOptions() {

        Map<String, Option<?>> map = new HashMap<>();
        for(Options option : values())
            map.put(option.option.getName(), option.option);
        return map;

    }

    public static Option<?> fromName(String name) {

        return NAME_TO_OPTION.get(name);

    }

    public Option<?> option() {

        return this.option;

    }

}
