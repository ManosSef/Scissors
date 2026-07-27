package me.manossef.scissors.config;

public class Options {
    public static final Option<Boolean> GPPCT_RESPONSES = Option.bool("gppctResponses", true);
    public static final Option<Integer> GPPCT_RESPONSE_CHANCE = Option.integer("gppctResponseChance", 10, 0, 100);
    public static final Option<Boolean> PING_RESPONSES = Option.bool("pingResponses", true);
    public static final Option<Boolean> SCISSORS_RESPONSES = Option.bool("scissorsResponses", true);
    public static final Option<Integer> SCISSORS_RESPONSE_CHANCE = Option.integer("scissorsResponseChance", 20, 0, 100);
    public static final Option<Boolean> REACT_TO_PAPER = Option.bool("reactToPaper", true);
    public static final Option<Boolean> GPPCT_ON_INTEGERS_ONLY = Option.bool("gppctOnIntegersOnly", false);
}