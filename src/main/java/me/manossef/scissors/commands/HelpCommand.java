package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.SharedConstants;

public class HelpCommand {

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("help")
            .executes(context -> help(context.getSource(), dispatcher))
        );

    }

    private static int help(ChatCommandSource source, CommandDispatcher<ChatCommandSource> dispatcher) {

        String[] usage = dispatcher.getAllUsage(dispatcher.getRoot(), source, true);
        StringBuilder builder = new StringBuilder();
        for(String line : usage) builder.append("- `" + SharedConstants.COMMAND_PREFIX).append(line).append("`\n");
        source.sendSuccess("All commands:\n" + builder);
        return usage.length;

    }

}
