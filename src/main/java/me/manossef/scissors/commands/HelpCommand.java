package me.manossef.scissors.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;

import java.util.Set;
import java.util.TreeSet;

public class HelpCommand {
    private static final String BASE_LITERAL = "help";
    private static final LiteralArgumentBuilder<ChatCommandSource> BASE_ARGUMENT = Commands.literal(BASE_LITERAL)
        .executes(context -> showHelpMessage(context.getSource()));
    private static final Set<String> LINES = new TreeSet<>();

    public static void addLine(String baseLiteral, String line, String... aliases) {
        StringBuilder builder = new StringBuilder();
        builder.append(Commands.format(baseLiteral));
        for(String alias : aliases)
            builder.append("/").append(Commands.format(alias));
        builder.append(" - ").append(line);
        LINES.add(builder.toString());
    }

    public static void addLiteral(String baseLiteral, String text, String... aliases) {
        Command<ChatCommandSource> command = context -> showHelpForCommand(context.getSource(), baseLiteral, text, aliases);
        BASE_ARGUMENT.then(Commands.literal(baseLiteral).executes(command));
        for(String alias : aliases)
            BASE_ARGUMENT.then(Commands.literal(alias).executes(command));
    }

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        addLine(BASE_LITERAL, "Shows all available commands or explains a command.");
        addLiteral(BASE_LITERAL, String.format("""
                Lists all available commands or explains what a command does and how to use it in detail.
                
                Here are all available syntaxes for this command:
                - %s: Lists all available commands, explaining what each one does in one sentence.
                - %s: Explains the specified command in detail. Lists all available syntaxes for it, describes what each one does, and mentions any situations in which the command fails.""",
            Commands.format(BASE_LITERAL),
            Commands.format(BASE_LITERAL + " <command>")));
        dispatcher.register(BASE_ARGUMENT);
    }

    private static int showHelpMessage(ChatCommandSource source) {
        StringBuilder builder = new StringBuilder();
        builder.append("All available commands are listed below. To learn more about a command, use ").append(Commands.format("help <command>")).append(".");
        for(String line : LINES)
            builder.append("\n- ").append(line);
        source.sendSuccess(builder.toString(), false);
        return LINES.size();
    }

    private static int showHelpForCommand(ChatCommandSource source, String baseLiteral, String helpText, String[] aliases) {
        StringBuilder builder = new StringBuilder();
        builder.append(Commands.format(baseLiteral));
        if(aliases.length > 0) {
            builder.append("\n\nAliases: ");
            for(String alias : aliases)
                builder.append(Commands.format(alias)).append(", ");
            builder.delete(builder.length() - 2, builder.length());
        }
        builder.append("\n\n").append(helpText);
        source.sendSuccess(builder.toString(), false);
        return 1;
    }
}