package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;

public class PingCommand {

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        String baseLiteral = "ping";
        dispatcher.register(Commands.literal(baseLiteral)
            .executes(context -> ping(context.getSource()))
        );
        HelpCommand.addLine(baseLiteral, "Replies with \"Pong!\"");
        HelpCommand.addLiteral(baseLiteral, "Replies with \"Pong!\" Need I say more?");

    }

    private static int ping(ChatCommandSource source) {

        source.sendSuccess("Pong!");
        return 1;

    }

}
