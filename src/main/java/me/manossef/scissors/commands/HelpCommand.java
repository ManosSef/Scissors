package me.manossef.scissors.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.LazilyFormattedText;
import net.dv8tion.jda.api.entities.channel.Channel;

import java.util.HashSet;
import java.util.Set;

public class HelpCommand {
    private static final String BASE_LITERAL = "help";
    private static final LiteralArgumentBuilder<ChatCommandSource> BASE_ARGUMENT = Commands.literal(BASE_LITERAL)
        .executes(context -> showHelpMessage(context.getSource()));
    private static final Set<LazilyFormattedText> LINES = new HashSet<>();

    public static void addLine(String baseLiteral, LazilyFormattedText line, String... aliases) {
        LINES.add(source -> {
            StringBuilder builder = new StringBuilder();
            Channel channel = source.commandMessage().getChannel();
            builder.append(Commands.format(baseLiteral, channel));
            for(String alias : aliases)
                builder.append("/").append(Commands.format(alias, channel));
            builder.append(" - ").append(line.format(source));
            return builder.toString();
        });
    }

    public static void addLiteral(String baseLiteral, LazilyFormattedText text, String... aliases) {
        Command<ChatCommandSource> command = context -> showHelpForCommand(context.getSource(), baseLiteral, text, aliases);
        BASE_ARGUMENT.then(Commands.literal(baseLiteral).executes(command));
        for(String alias : aliases)
            BASE_ARGUMENT.then(Commands.literal(alias).executes(command));
    }

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        addLine(BASE_LITERAL, s -> "Shows all available commands or explains a command.");
        addLiteral(BASE_LITERAL, source -> {
            Channel channel = source.commandMessage().getChannel();
            return String.format("""
                    Lists all available commands or explains what a command does and how to use it in detail.
                    
                    Here are all available syntaxes for this command:
                    - %s: Lists all available commands, explaining what each one does in one sentence.
                    - %s: Explains the specified command in detail. Lists all available syntaxes for it, describes what each one does, and mentions any situations in which the command fails.""",
                Commands.format(BASE_LITERAL, channel),
                Commands.format(BASE_LITERAL + " <command>", channel));
        });
        dispatcher.register(BASE_ARGUMENT);
    }

    private static int showHelpMessage(ChatCommandSource source) {
        StringBuilder builder = new StringBuilder();
        builder.append("All available commands are listed below. To learn more about a command, use ")
            .append(Commands.format("help <command>", source.commandMessage().getChannel())).append(".");
        for(LazilyFormattedText line : LINES)
            builder.append("\n- ").append(line.format(source));
        source.sendSuccess(builder.toString(), false);
        return LINES.size();
    }

    private static int showHelpForCommand(ChatCommandSource source, String baseLiteral, LazilyFormattedText helpText, String[] aliases) {
        StringBuilder builder = new StringBuilder();
        Channel channel = source.commandMessage().getChannel();
        builder.append(Commands.format(baseLiteral, channel));
        if(aliases.length > 0) {
            builder.append("\n\nAliases: ");
            for(String alias : aliases)
                builder.append(Commands.format(alias, channel)).append(", ");
            builder.delete(builder.length() - 2, builder.length());
        }
        builder.append("\n\n").append(helpText.format(source));
        source.sendSuccess(builder.toString(), false);
        return 1;
    }
}