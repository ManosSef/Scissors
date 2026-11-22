package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.Messages;
import me.manossef.scissors.commands.core.CommandSource;
import me.manossef.scissors.commands.core.Commands;
import me.manossef.scissors.puzzles.Wordle;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

import static net.dv8tion.jda.api.interactions.commands.build.Commands.slash;

public class WordleCommand {
    private static final SimpleCommandExceptionType CANNOT_START = new SimpleCommandExceptionType(new LiteralMessage("Cannot start Wordle games in this session. Please yell at " + Messages.MY_MENTION + " to restart me"));

    public static void register(CommandDispatcher<CommandSource> dispatcher, List<SlashCommandData> slashCommands) {
        String baseLiteral = "wordle";
        String hard = "hard";
        dispatcher.register(Commands.literal(baseLiteral)
            .executes(context -> startWordle(context.getSource(), false))
            .then(Commands.literal(hard)
                .executes(context -> startWordle(context.getSource(), true))
            )
            .then(Commands.argument("answer", StringArgumentType.word())
                .requires(Commands.devRestricted())
                .executes(context -> startWordle(context.getSource(), false, context.getArgument("answer", String.class)))
                .then(Commands.literal(hard)
                    .executes(context -> startWordle(context.getSource(), true, context.getArgument("answer", String.class)))
                )
            )
        );
        String description = "Starts a game of Wordle.";
        HelpCommand.addLine(baseLiteral, s -> description);
        HelpCommand.addLiteral(baseLiteral, source -> {
            Channel channel = source.channel();
            return String.format("""
                    Starts a game of Wordle. A message that will keep track of your progress is posted after this command is run. Replying to that message with a 5-letter word will guess that word, causing the bot to edit the message \
                    to reveal the colors for each letter in the word.
                    
                    Here are all available syntaxes for this command:
                    - %s: Starts a normal game of Wordle.
                    - %s: Starts a game of Wordle in hard mode, which works like the hard mode in the New York Times' Wordle.""",
                Commands.format(baseLiteral, channel),
                Commands.format(baseLiteral + " hard", channel)
            );
        });
        String normal = "normal";
        slashCommands.add(slash(baseLiteral, description).addSubcommands(new SubcommandData(normal, description), new SubcommandData(hard, description)));
        Commands.registerSlashCommand(baseLiteral + " " + normal, source -> startWordle(source, false));
        Commands.registerSlashCommand(baseLiteral + " " + hard, source -> startWordle(source, true));
    }

    private static int startWordle(CommandSource source, boolean hardMode) throws CommandSyntaxException {
        if(Wordle.canStart()) {
            source.sendSuccess("Starting a game of Wordle" + (hardMode ? " in hard mode" : ""), true);
            new Wordle(source.channel(), hardMode);
            return 1;
        }
        throw CANNOT_START.create();
    }

    private static int startWordle(CommandSource source, boolean hardMode, String answer) throws CommandSyntaxException {
        if(Wordle.canStart()) {
            source.sendSuccess("Starting a game of Wordle" + (hardMode ? " in hard mode" : ""), true);
            new Wordle(source.channel(), hardMode, answer);
            return 1;
        }
        throw CANNOT_START.create();
    }
}