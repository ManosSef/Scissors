package me.manossef.scissors.commands.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.*;
import me.manossef.scissors.*;
import me.manossef.scissors.commands.*;
import me.manossef.scissors.commands.debug.*;
import me.manossef.scissors.config.Options;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class Commands {
    public static final SimpleCommandExceptionType IO_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("Something went wrong; please try again"));
    public static final SimpleCommandExceptionType GUILD_NOT_FOUND = new SimpleCommandExceptionType(new LiteralMessage("No guild was found"));
    public static final SimpleCommandExceptionType TEMP_NO_SLASH = new SimpleCommandExceptionType(new LiteralMessage("Slash commands not implemented yet")); // TODO temp temp temp
    private static final Map<String, SlashCommandDispatcher> SLASH_COMMANDS = new HashMap<>();
    private static final CommandDispatcher<CommandSource> DISPATCHER = new CommandDispatcher<>() {{
        registerCommands(this);
    }};

    static {
        CommandSyntaxException.BUILT_IN_EXCEPTIONS = new BuiltInExceptions();
    }

    public static void dispatch(Message message, User user) {
        String prefix = getPrefix(message.getChannel());
        String command = message.getContentRaw().replaceFirst(prefix, "").strip();
        if(command.isEmpty()) return;
        CommandSource source = new ChatCommandSource(message, user);
        String username = user.getName().replace("_", "\\_");
        try {
            int result = DISPATCHER.execute(command, source);
            DevGuild.logCommand(shortenMiddle(username + " (" + user.getId() + ") executed command ", monospace(command), " in " + Messages.getLinkWithInfo(message) + " and succeeded with return value " + result));
        } catch(CommandSyntaxException e) {
            source.sendFailure(e.getMessage());
            DevGuild.logCommand(shortenMiddle(username + " (" + user.getId() + ") executed command ", monospace(command), " in " + Messages.getLinkWithInfo(message) + " and failed"));
        } catch(RuntimeException e) {
            source.sendError(e.getMessage());
            DevGuild.logCommandError(shortenMiddle(username + " (" + user.getId() + ") executed command ", monospace(command), " in " + Messages.getLinkWithInfo(message) + " and threw an exception:"), e);
            Issues.createForException(e, "Command error: ", "Command: {{" + command + "}}\nSource: {{" + source.getDetails() + "}}");
        }
    }

    public static void dispatchSlash(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        String username = user.getName().replace("_", "\\_");
        Channel channel = event.getChannel();
        String command = stringifySlashCommand(event);
        SlashCommandSource source = new SlashCommandSource(event);
        try {
            SLASH_COMMANDS.get(event.getFullCommandName()).execute(source);
            DevGuild.logSlashCommand(shortenMiddle(username + " (" + user.getId() + ") executed command ", monospace(command), " in " + channel.getAsMention() + " and succeeded"));
        } catch(CommandSyntaxException e) {
            source.sendFailure(e.getMessage());
            DevGuild.logSlashCommand(shortenMiddle(username + " (" + user.getId() + ") executed command ", monospace(command), " in " + channel.getAsMention() + " and failed"));
        } catch(RuntimeException e) {
            source.sendError(e.getMessage());
            DevGuild.logSlashCommandError(shortenMiddle(username + " (" + user.getId() + ") executed command ", monospace(command), " in " + channel.getAsMention() + " and threw an exception:"), e);
            Issues.createForException(e, "Slash command error: ", "Command: {{" + command + "}}\nSource: {{" + source.getDetails() + "}}");
        }
    }

    private static String stringifySlashCommand(SlashCommandInteractionEvent event) {
        StringBuilder builder = new StringBuilder(event.getFullCommandName());
        for(OptionMapping option : event.getOptions()) builder.append(" ").append(option.getName()).append(":").append(option.getAsString());
        return builder.toString();
    }

    private static String shortenMiddle(String start, String middle, String end) {
        return start + shorten(middle, Message.MAX_CONTENT_LENGTH - start.length() - end.length()) + end;
    }

    private static String shorten(String string, int length) {
        if(length > string.length() - 3) return string;
        int remaining = length - 3;
        int fromStart = remaining / 2 + (remaining % 2 == 0 ? 0 : 1);
        int fromEnd = remaining / 2;
        return string.substring(0, fromStart) + "..." + string.substring(string.length() - fromEnd);
    }

    public static LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public static void registerSlashCommand(String name, SlashCommandDispatcher dispatcher) {
        SLASH_COMMANDS.put(name, dispatcher);
    }

    private static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        List<SlashCommandData> slashCommands = new ArrayList<>();
        CatFactCommand.register(dispatcher, slashCommands);
        CoinflipCommand.register(dispatcher);
        ConfigCommand.register(dispatcher);
        EchoCommand.register(dispatcher);
        HangmanCommand.register(dispatcher);
        InfoCommand.register(dispatcher);
        IssueCommand.register(dispatcher);
        PingCommand.register(dispatcher);
        RockPaperScissorsCommand.register(dispatcher);
        RollCommand.register(dispatcher);
        SquaredleCommand.register(dispatcher);
        SuggestCommand.register(dispatcher);
        TicTacToeCommand.register(dispatcher);
        WordleCommand.register(dispatcher);
        NineCommand.register(dispatcher);
        JiraCheckLoopCommand.register(dispatcher);
        LeaveCommand.register(dispatcher);
        ListChannelsCommand.register(dispatcher);
        ListGuildsCommand.register(dispatcher);
        RawHelpCommand.register(dispatcher);
        StopAllGamesCommand.register(dispatcher);
        HelpCommand.register(dispatcher);
        if(Environment.IS_STAGING)
            ManualErrorCommand.register(dispatcher);
        Scissors.DISCORD_API.updateCommands().addCommands(slashCommands.toArray(new SlashCommandData[0])).queue();
    }

    public static Predicate<CommandSource> devRestricted() {
        return source -> source.user().getIdLong() == Messages.MY_USER_ID;
    }

    public static String format(String command, Channel channel) {
        return monospace(getPrefix(channel) + command);
    }

    public static String defaultFormat(String command) {
        return monospace(Options.COMMAND_PREFIX.getDefaultValue() + command);
    }

    public static String getPrefix(Channel channel) {
        return Scissors.getConfiguration().getOptionForChannel(Options.COMMAND_PREFIX, channel);
    }

    public static LazilyFormattedText.ExceptionType lazyExceptionWithCommand(String message, String command) {
        return new LazilyFormattedText.ExceptionType(
            source -> message.formatted(Commands.format(command, source.channel())));
    }

    @FunctionalInterface
    public interface SlashCommandDispatcher {
        void execute(SlashCommandSource source) throws CommandSyntaxException;
    }

    private static class BuiltInExceptions implements BuiltInExceptionProvider {
        private static final Dynamic2CommandExceptionType DOUBLE_TOO_SMALL = new Dynamic2CommandExceptionType((found, min) -> new LiteralMessage("Expected a number not less than " + min + ", found " + found));
        private static final Dynamic2CommandExceptionType DOUBLE_TOO_BIG = new Dynamic2CommandExceptionType((found, max) -> new LiteralMessage("Expected a number not more than " + max + ", found " + found));
        private static final Dynamic2CommandExceptionType FLOAT_TOO_SMALL = new Dynamic2CommandExceptionType((found, min) -> new LiteralMessage("Expected a number not less than " + min + ", found " + found));
        private static final Dynamic2CommandExceptionType FLOAT_TOO_BIG = new Dynamic2CommandExceptionType((found, max) -> new LiteralMessage("Expected a number not more than " + max + ", found " + found));
        private static final Dynamic2CommandExceptionType INTEGER_TOO_SMALL = new Dynamic2CommandExceptionType((found, min) -> new LiteralMessage("Expected an integer not less than " + min + ", found " + found));
        private static final Dynamic2CommandExceptionType INTEGER_TOO_BIG = new Dynamic2CommandExceptionType((found, max) -> new LiteralMessage("Expected an integer not more than " + max + ", found " + found));
        private static final Dynamic2CommandExceptionType LONG_TOO_SMALL = new Dynamic2CommandExceptionType((found, min) -> new LiteralMessage("Expected an integer not less than " + min + ", found " + found));
        private static final Dynamic2CommandExceptionType LONG_TOO_BIG = new Dynamic2CommandExceptionType((found, max) -> new LiteralMessage("Expected an integer not more than " + max + ", found " + found));
        private static final DynamicCommandExceptionType LITERAL_INCORRECT = new DynamicCommandExceptionType(expected -> new LiteralMessage("Expected literal " + expected));
        private static final SimpleCommandExceptionType READER_EXPECTED_START_OF_QUOTE = new SimpleCommandExceptionType(new LiteralMessage("Expected a quotation mark to start a string"));
        private static final SimpleCommandExceptionType READER_EXPECTED_END_OF_QUOTE = new SimpleCommandExceptionType(new LiteralMessage("Missing closing quotation mark"));
        private static final DynamicCommandExceptionType READER_INVALID_BOOL = new DynamicCommandExceptionType(value -> new LiteralMessage("Expected true or false but found '" + value + "'"));
        private static final SimpleCommandExceptionType READER_EXPECTED_INT = new SimpleCommandExceptionType(new LiteralMessage("Expected an integer"));
        private static final DynamicCommandExceptionType READER_INVALID_LONG = new DynamicCommandExceptionType(value -> new LiteralMessage("Invalid integer '" + value + "'"));
        private static final SimpleCommandExceptionType READER_EXPECTED_LONG = new SimpleCommandExceptionType(new LiteralMessage("Expected an integer"));
        private static final DynamicCommandExceptionType READER_INVALID_DOUBLE = new DynamicCommandExceptionType(value -> new LiteralMessage("Invalid number '" + value + "'"));
        private static final SimpleCommandExceptionType READER_EXPECTED_DOUBLE = new SimpleCommandExceptionType(new LiteralMessage("Expected a number"));
        private static final DynamicCommandExceptionType READER_INVALID_FLOAT = new DynamicCommandExceptionType(value -> new LiteralMessage("Invalid number '" + value + "'"));
        private static final SimpleCommandExceptionType READER_EXPECTED_FLOAT = new SimpleCommandExceptionType(new LiteralMessage("Expected a number"));
        private static final SimpleCommandExceptionType READER_EXPECTED_BOOL = new SimpleCommandExceptionType(new LiteralMessage("Expected true or false"));
        private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_COMMAND = new SimpleCommandExceptionType(new LiteralMessage("Unknown command or missing argument"));
        private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_ARGUMENT = new SimpleCommandExceptionType(new LiteralMessage("Incorrect argument"));
        private static final SimpleCommandExceptionType DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR = new SimpleCommandExceptionType(new LiteralMessage("An argument was expected to end"));

        private static final com.mojang.brigadier.exceptions.BuiltInExceptions DEFAULT_EXCEPTIONS = new com.mojang.brigadier.exceptions.BuiltInExceptions();

        public Dynamic2CommandExceptionType doubleTooLow() {
            return DOUBLE_TOO_SMALL;
        }

        public Dynamic2CommandExceptionType doubleTooHigh() {
            return DOUBLE_TOO_BIG;
        }

        public Dynamic2CommandExceptionType floatTooLow() {
            return FLOAT_TOO_SMALL;
        }

        public Dynamic2CommandExceptionType floatTooHigh() {
            return FLOAT_TOO_BIG;
        }

        public Dynamic2CommandExceptionType integerTooLow() {
            return INTEGER_TOO_SMALL;
        }

        public Dynamic2CommandExceptionType integerTooHigh() {
            return INTEGER_TOO_BIG;
        }

        public Dynamic2CommandExceptionType longTooLow() {
            return LONG_TOO_SMALL;
        }

        public Dynamic2CommandExceptionType longTooHigh() {
            return LONG_TOO_BIG;
        }

        public DynamicCommandExceptionType literalIncorrect() {
            return LITERAL_INCORRECT;
        }

        public SimpleCommandExceptionType readerExpectedStartOfQuote() {
            return READER_EXPECTED_START_OF_QUOTE;
        }

        public SimpleCommandExceptionType readerExpectedEndOfQuote() {
            return READER_EXPECTED_END_OF_QUOTE;
        }

        public DynamicCommandExceptionType readerInvalidEscape() {
            return DEFAULT_EXCEPTIONS.readerInvalidEscape();
        }

        public DynamicCommandExceptionType readerInvalidBool() {
            return READER_INVALID_BOOL;
        }

        public DynamicCommandExceptionType readerInvalidInt() {
            return DEFAULT_EXCEPTIONS.readerInvalidInt();
        }

        public SimpleCommandExceptionType readerExpectedInt() {
            return READER_EXPECTED_INT;
        }

        public DynamicCommandExceptionType readerInvalidLong() {
            return READER_INVALID_LONG;
        }

        public SimpleCommandExceptionType readerExpectedLong() {
            return READER_EXPECTED_LONG;
        }

        public DynamicCommandExceptionType readerInvalidDouble() {
            return READER_INVALID_DOUBLE;
        }

        public SimpleCommandExceptionType readerExpectedDouble() {
            return READER_EXPECTED_DOUBLE;
        }

        public DynamicCommandExceptionType readerInvalidFloat() {
            return READER_INVALID_FLOAT;
        }

        public SimpleCommandExceptionType readerExpectedFloat() {
            return READER_EXPECTED_FLOAT;
        }

        public SimpleCommandExceptionType readerExpectedBool() {
            return READER_EXPECTED_BOOL;
        }

        public DynamicCommandExceptionType readerExpectedSymbol() {
            return DEFAULT_EXCEPTIONS.readerExpectedSymbol();
        }

        public SimpleCommandExceptionType dispatcherUnknownCommand() {
            return DISPATCHER_UNKNOWN_COMMAND;
        }

        public SimpleCommandExceptionType dispatcherUnknownArgument() {
            return DISPATCHER_UNKNOWN_ARGUMENT;
        }

        public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
            return DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR;
        }

        public DynamicCommandExceptionType dispatcherParseException() {
            return DEFAULT_EXCEPTIONS.dispatcherParseException();
        }
    }
}