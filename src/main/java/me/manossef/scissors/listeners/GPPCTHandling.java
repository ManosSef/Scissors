package me.manossef.scissors.listeners;

public enum GPPCTHandling {
    NUMBER("^-?(?:0|[1-9][0-9]*)(?:\\.[0-9]+)?(?:[eE][-+]?[0-9]+)?$"),
    INTEGER("^-?(?:0|[1-9][0-9]*)$"),
    NON_NEGATIVE_INTEGER("^(?:0|[1-9][0-9]*)$"),
    DIGITS("^[0-9]+$");

    private final String regex;

    GPPCTHandling(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return this.regex;
    }
}