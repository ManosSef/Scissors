package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;

public class PingCommand {

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("ping")
            .executes(context -> ping(context.getSource()))
        );

    }

    private static int ping(ChatCommandSource source) {

        source.sendSuccess("Pong!");
        return 1;

    }

}
