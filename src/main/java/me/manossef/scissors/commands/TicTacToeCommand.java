package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.arguments.UserArgumentType;
import me.manossef.scissors.commands.core.CommandSource;
import me.manossef.scissors.commands.core.Commands;
import me.manossef.scissors.games.TicTacToe;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;
import java.util.Objects;

import static net.dv8tion.jda.api.interactions.commands.build.Commands.slash;
import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class TicTacToeCommand {
    private static final SimpleCommandExceptionType SAME_USER = new SimpleCommandExceptionType(new LiteralMessage("You cannot play tic-tac-toe with yourself"));
    private static final SimpleCommandExceptionType NO_BOTS = new SimpleCommandExceptionType(new LiteralMessage("You cannot play tic-tac-toe with that bot"));

    public static void register(CommandDispatcher<CommandSource> dispatcher, List<SlashCommandData> slashCommands) {
        String baseLiteral = "tictactoe";
        String bot = "bot";
        String opponent = "opponent";
        dispatcher.register(Commands.literal(baseLiteral)
            .then(Commands.literal(bot)
                .executes(context -> startBotTicTacToeGame(context.getSource()))
            )
            .then(Commands.argument(opponent, UserArgumentType.user())
                .executes(context -> startTicTacToeGame(context.getSource(), context.getArgument(opponent, User.class)))
            )
        );
        String description = "Starts a tic-tac-toe game between you and the bot or another user.";
        HelpCommand.addLine(baseLiteral, s -> description);
        HelpCommand.addLiteral(baseLiteral, source -> {
            Channel channel = source.channel();
            return String.format("""
                    Starts a game of tic-tac-toe between you and the bot or the specified user.
                    
                    Here are all available syntaxes for this command:
                    - %s: Starts a game of tic-tac-toe between you and the bot.
                    - %s: Starts a game of tic-tac-toe between you and the specified user. Fails if the specified user does not exist in the server, is the same as the user running the command, or is a bot other than Scissors.
                    
                    For the %s argument, you can use a user ID or a mention (ping) of the user in question.""",
                Commands.format(baseLiteral + " bot", channel),
                Commands.format(baseLiteral + " <opponent>", channel),
                monospace("<opponent>"));
        });
        String user = "user";
        slashCommands.add(slash(baseLiteral, description)
            .addSubcommands(
                new SubcommandData(bot, "Starts a tic-tac-toe game between you and the bot."),
                new SubcommandData(user, "Starts a tic-tac-toe game between you and another user.").addOption(OptionType.USER, opponent, "The user to play against.", true)
            ));
        Commands.registerSlashCommand(baseLiteral + " " + bot, TicTacToeCommand::startBotTicTacToeGame);
        Commands.registerSlashCommand(baseLiteral + " " + user, source -> startTicTacToeGame(source, Objects.requireNonNull(source.event().getOption(opponent, OptionMapping::getAsUser))));
    }

    private static int startBotTicTacToeGame(CommandSource source) {
        source.sendSuccess("Starting a tic-tac-toe game with the bot", true);
        new TicTacToe(source.user(), Scissors.DISCORD_API.getSelfUser(), source.channel());
        return 1;
    }

    private static int startTicTacToeGame(CommandSource source, User user) throws CommandSyntaxException {
        if(user.isBot() && user.getIdLong() == Scissors.DISCORD_API.getSelfUser().getIdLong())
            return startBotTicTacToeGame(source);
        if(user.isBot() || user.isSystem()) throw NO_BOTS.create();
        if(user.getIdLong() == source.user().getIdLong()) throw SAME_USER.create();
        source.sendSuccess("Starting a tic-tac-toe game with " + user.getAsMention(), true);
        new TicTacToe(source.user(), user, source.channel());
        return 1;
    }
}