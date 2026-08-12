package me.manossef.scissors.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Options {
    private static final Map<String, Option<?>> OPTIONS = new LinkedHashMap<>();
    private static final Map<String, Option<?>> LEGACY_OPTIONS = new HashMap<>();

    public static final Option<Boolean> GPPCT_RESPONSES = register(Option.bool("gppctResponses", true));
    public static final Option<Integer> GPPCT_RESPONSE_CHANCE = register(Option.integer("gppctResponseChance", 10, 0, 100));
    public static final Option<Boolean> PING_RESPONSES = register(Option.bool("pingResponses", true));
    public static final Option<Boolean> SCISSORS_RESPONSES = register(Option.bool("scissorsResponses", true));
    public static final Option<Integer> SCISSORS_RESPONSE_CHANCE = register(Option.integer("scissorsResponseChance", 20, 0, 100));
    public static final Option<Boolean> REACT_TO_PAPER = register(Option.bool("reactToPaper", true));
    public static final Option<Boolean> GPPCT_ON_INTEGERS_ONLY = register(Option.bool("gppctOnIntegersOnly", false));

    public static Option<?>[] values() {
        return OPTIONS.values().toArray(new Option[0]);
    }

    static Option<?> getByName(String name) {
        Option<?> option = OPTIONS.get(name);
        if(option == null) throw new IllegalArgumentException("Unknown option: " + name);
        return option;
    }

    static OptionValue<?> upgradeIfLegacy(OptionValue<?> value) {
        /*if(!isLegacy(value.option().getName()))*/ return value; // currently no legacy options
    }

    private static <T> Option<T> register(Option<T> option) {
        OPTIONS.put(option.getName(), option);
        return option;
    }

    private static <T> Option<T> registerLegacy(Option<T> option) {
        LEGACY_OPTIONS.put(option.getName(), option);
        return option;
    }

    private static boolean isLegacy(String name) {
        if(LEGACY_OPTIONS.containsKey(name)) return true;
        if(OPTIONS.containsKey(name)) return false;
        throw new IllegalArgumentException("Unknown option: " + name);
    }
}