package me.manossef.scissors.config;

import me.manossef.scissors.listeners.GPPCTHandling;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Options {
    private static final Map<String, Option<?>> OPTIONS = new LinkedHashMap<>();
    public static final Option<Boolean> GPPCT_RESPONSES = register(Option.bool("gppctResponses", true));
    public static final Option<Integer> GPPCT_RESPONSE_CHANCE = register(Option.integer("gppctResponseChance", 10, 0, 100));
    public static final Option<Boolean> PING_RESPONSES = register(Option.bool("pingResponses", true));
    public static final Option<Boolean> SCISSORS_RESPONSES = register(Option.bool("scissorsResponses", true));
    public static final Option<Integer> SCISSORS_RESPONSE_CHANCE = register(Option.integer("scissorsResponseChance", 20, 0, 100));
    public static final Option<Boolean> REACT_TO_PAPER = register(Option.bool("reactToPaper", true));
    public static final Option<GPPCTHandling> GPPCT_HANDLING = register(Option.enumOp("gppctHandling", GPPCTHandling.class, GPPCTHandling.NUMBER));

    private static final Map<String, Option<?>> LEGACY_OPTIONS = new HashMap<>();
    private static final Option<Boolean> GPPCT_ON_INTEGERS_ONLY = registerLegacy(Option.bool("gppctOnIntegersOnly", false));

    public static Option<?>[] values() {
        return OPTIONS.values().toArray(new Option[0]);
    }

    static Option<?> getByName(String name) {
        Option<?> option = OPTIONS.get(name);
        if(option == null) {
            option = LEGACY_OPTIONS.get(name);
            if(option == null) throw new IllegalArgumentException("Unknown option: " + name);
            return option;
        }
        return option;
    }

    static OptionValue<?> upgradeIfLegacy(OptionValue<?> value) {
        if(!isLegacy(value.option().getName())) return value;
        if(value.option().equals(GPPCT_ON_INTEGERS_ONLY)) {
            OptionValue<Boolean> cast = GPPCT_ON_INTEGERS_ONLY.castValue(value.value());
            if(cast.value()) return new OptionValue<>(GPPCT_HANDLING, GPPCTHandling.NON_NEGATIVE_INTEGER);
            else return new OptionValue<>(GPPCT_HANDLING, GPPCTHandling.NUMBER);
        }
        throw new IllegalStateException("No upgrader has been specified for value " + value);
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