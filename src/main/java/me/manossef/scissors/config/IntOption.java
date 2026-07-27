package me.manossef.scissors.config;

import com.mojang.brigadier.arguments.IntegerArgumentType;

public class IntOption extends Option<Integer> {
    IntOption(String name) {
        super(name, Integer.class, IntegerArgumentType.integer());
    }

    IntOption(String name, int min) {
        super(name, Integer.class, IntegerArgumentType.integer(min));
    }

    IntOption(String name, int min, int max) {
        super(name, Integer.class, IntegerArgumentType.integer(min, max));
    }
}
