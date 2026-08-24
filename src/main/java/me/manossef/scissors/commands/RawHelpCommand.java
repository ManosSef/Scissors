package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.config.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RawHelpCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(RawHelpCommand.class);

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
            builder.append("- ").append(Options.COMMAND_PREFIX.getDefaultValue()).append(line).append("\n");
        LOGGER.info(builder.toString());
        source.sendSuccess("Logged all command syntaxes", true);
        return usage.length;
    }
}