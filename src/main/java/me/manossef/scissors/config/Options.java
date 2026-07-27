package me.manossef.scissors.config;

import java.util.HashSet;
import java.util.Set;

public class Options {
    private static final Set<Option<?>> OPTIONS = new HashSet<>();

    public static final Option<Boolean> GPPCT_RESPONSES = register(Option.bool("gppctResponses", true));
    public static final Option<Integer> GPPCT_RESPONSE_CHANCE = register(Option.integer("gppctResponseChance", 10, 0, 100));
    public static final Option<Boolean> PING_RESPONSES = register(Option.bool("pingResponses", true));
    public static final Option<Boolean> SCISSORS_RESPONSES = register(Option.bool("scissorsResponses", true));
    public static final Option<Integer> SCISSORS_RESPONSE_CHANCE = register(Option.integer("scissorsResponseChance", 20, 0, 100));
    public static final Option<Boolean> REACT_TO_PAPER = register(Option.bool("reactToPaper", true));
    public static final Option<Boolean> GPPCT_ON_INTEGERS_ONLY = register(Option.bool("gppctOnIntegersOnly", false));

    public static Option<?>[] values() {
        return OPTIONS.toArray(new Option[0]);
    }

    private static <T> Option<T> register(Option<T> option) {
        OPTIONS.add(option);
        return option;
    }
}