package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;

public class RawHelpCommand {
    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        dispatcher.register(Commands.literal("rawhelp")
            .requires(Commands.devRestricted())
            .executes(context -> help(context.getSource(), dispatcher))
        );
    }

    private static int help(ChatCommandSource source, CommandDispatcher<ChatCommandSource> dispatcher) {
        String[] usage = dispatcher.getAllUsage(dispatcher.getRoot(), source, true);
        StringBuilder builder = new StringBuilder();
        for(String line : usage)
            builder.append("- ").append(Commands.format(line)).append("\n");
        source.sendSuccess("All commands:\n" + builder, false);
        return usage.length;
    }
}