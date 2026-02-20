package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.SharedConstants;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class HelpCommand {

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("help")
            .executes(context -> help(context.getSource(), dispatcher))
        );

    }

    private static int help(ChatCommandSource source, CommandDispatcher<ChatCommandSource> dispatcher) {

        String[] usage = dispatcher.getAllUsage(dispatcher.getRoot(), source, true);
        StringBuilder builder = new StringBuilder();
        for(String line : usage)
            if(Character.isLowerCase(line.charAt(0)))
                builder.append("- ").append(monospace(SharedConstants.COMMAND_PREFIX + line + "\n"));
        source.sendSuccess("All commands:\n" + builder);
        return usage.length;

    }

}
