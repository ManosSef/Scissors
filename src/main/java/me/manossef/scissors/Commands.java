package me.manossef.scissors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.*;
import me.manossef.scissors.commands.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.function.Predicate;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class Commands {
    public static final SimpleCommandExceptionType IO_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("Something went wrong; please try again"));
    public static final SimpleCommandExceptionType USER_NOT_FOUND = new SimpleCommandExceptionType(new LiteralMessage("No user was found"));
    public static final SimpleCommandExceptionType GUILD_NOT_FOUND = new SimpleCommandExceptionType(new LiteralMessage("No guild was found"));
    private static final CommandDispatcher<ChatCommandSource> DISPATCHER = new CommandDispatcher<>() {{
        registerCommands(this);
    }};
    private static ChatCommandSource source = new ChatCommandSource(null, null);

    static {
        CommandSyntaxException.BUILT_IN_EXCEPTIONS = new BuiltInExceptions();
    }

    public static void dispatch(Message message, User user) {
        String command = message.getContentRaw().replaceFirst(SharedConstants.COMMAND_PREFIX, "").strip();
        if(command.isEmpty()) return;
        source = source.withMessage(message).withUser(user);
        String username = user.getName().replace("_", "\\_");
        try {
            int result = DISPATCHER.execute(command, source);
            DevGuild.logCommand(shortenMiddle(username + " (" + user.getId() + ") executed command ", monospace(command), " in " + Util.getMessageLinkWithInfo(message) + " and succeeded with return value " + result));
        } catch(CommandSyntaxException e) {
            source.sendFailure(e.getMessage());
            DevGuild.logCommand(shortenMiddle(username + " (" + user.getId() + ") executed command ", monospace(command), " in " + Util.getMessageLinkWithInfo(message) + " and failed"));
        } catch(Exception e) {
            source.sendError(e.getMessage());
            DevGuild.logCommandError(shortenMiddle(username + " (" + user.getId() + ") executed command ", monospace(command), " in " + Util.getMessageLinkWithInfo(message) + " and threw an exception:"), e);
            Util.createIssueForException(e, "Command error: ", "Command: {{" + command + "}}");
        }
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

    public static LiteralArgumentBuilder<ChatCommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<ChatCommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    private static void registerCommands(CommandDispatcher<ChatCommandSource> dispatcher) {
        CatFactCommand.register(dispatcher);
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
        if(SharedConstants.IS_STAGING)
            ManualErrorCommand.register(dispatcher);
    }

    public static Predicate<ChatCommandSource> devRestricted() {
        return source -> source.user().getIdLong() == SharedConstants.MY_USER_ID;
    }

    public static String format(String command) {
        return monospace(SharedConstants.COMMAND_PREFIX + command);
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