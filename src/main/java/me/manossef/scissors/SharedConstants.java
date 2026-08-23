package me.manossef.scissors;

public class SharedConstants {
    private static final String ENVIRONMENT = System.getenv("SCISSORS_ENVIRONMENT");

    static {
        if(!("staging".equalsIgnoreCase(ENVIRONMENT) || "production".equalsIgnoreCase(ENVIRONMENT)))
            throw new IllegalStateException("Invalid environment");
    }

    public static final boolean IS_STAGING = "staging".equalsIgnoreCase(ENVIRONMENT);

    public static final long MY_USER_ID = 611151083141857286L;
    public static final String MY_MENTION = "<@" + MY_USER_ID + ">";

    public static final String COMMAND_PREFIX = "8<";
}