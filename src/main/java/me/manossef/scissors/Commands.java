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
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.util.function.Predicate;

public class Commands {

    private static final CommandDispatcher<ChatCommandSource> DISPATCHER = new CommandDispatcher<>() {{

        registerCommands(this);

    }};
    private static ChatCommandSource source = new ChatCommandSource(null, null);

    static {

        CommandSyntaxException.BUILT_IN_EXCEPTIONS = new BuiltInExceptions(new com.mojang.brigadier.exceptions.BuiltInExceptions());

    }

    public static void dispatch(Message message, User user) {

        String command = message.getContentRaw().replaceFirst(SharedConstants.COMMAND_PREFIX, "").strip();
        MessageChannel channel = message.getChannel();
        source = source.withMessage(message).withUser(user);
        String username = user.getName().replace("_", "\\_");
        try {

            int result = DISPATCHER.execute(command, source);
            DevGuild.logCommand(username + " (" + user.getId() + ") executed command `" + command.replace("`", "\\u0060") + "` in " + channel.getAsMention() + " (" + channel.getId() + ") and succeeded with return value " + result);

        } catch(CommandSyntaxException e) {

            source.sendFailure(e.getMessage());
            DevGuild.logCommand(username + " (" + user.getId() + ") executed command `" + command.replace("`", "\\u0060") + "` in " + channel.getAsMention() + " (" + channel.getId() + ") and failed");

        } catch(Exception e) {

            source.sendError(e.getMessage());
            StringBuilder stackTrace = new StringBuilder();
            for(StackTraceElement element : e.getStackTrace())
                stackTrace.append("\t`at ").append(element.toString()).append("`\n");
            DevGuild.logCommand(username + " (" + user.getId() + ") executed command `" + command.replace("`", "\\u0060") + "` in " + channel.getAsMention() + " (" + channel.getId() + ") and threw an exception: `" +
                e.getClass().getName() + ": " + e.getMessage() + "`\n" + stackTrace);

        }

    }

    public static LiteralArgumentBuilder<ChatCommandSource> literal(String name) {

        return LiteralArgumentBuilder.literal(name);

    }

    public static <T> RequiredArgumentBuilder<ChatCommandSource, T> argument(String name, ArgumentType<T> type) {

        return RequiredArgumentBuilder.argument(name, type);

    }

    private static void registerCommands(CommandDispatcher<ChatCommandSource> dispatcher) {

        NineCommand.register(dispatcher);
        CoinflipCommand.register(dispatcher);
        EchoCommand.register(dispatcher);
        GameCommand.register(dispatcher);
        HelpCommand.register(dispatcher);
        InfoCommand.register(dispatcher);
        IssueCommand.register(dispatcher);
        PingCommand.register(dispatcher);
        RockPaperScissorsCommand.register(dispatcher);
        RollCommand.register(dispatcher);
        SquaredleCommand.register(dispatcher);
        SuggestCommand.register(dispatcher);
        ListGuildsCommand.register(dispatcher);

    }

    public static Predicate<ChatCommandSource> devRestricted() {

        return source -> source.user().getIdLong() == SharedConstants.MY_USER_ID;

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

        private final com.mojang.brigadier.exceptions.BuiltInExceptions defaultExceptions;

        private BuiltInExceptions(com.mojang.brigadier.exceptions.BuiltInExceptions defaultExceptions) {

            this.defaultExceptions = defaultExceptions;

        }

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
            return this.defaultExceptions.readerInvalidEscape();
        }

        public DynamicCommandExceptionType readerInvalidBool() {
            return READER_INVALID_BOOL;
        }

        public DynamicCommandExceptionType readerInvalidInt() {
            return this.defaultExceptions.readerInvalidInt();
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
            return this.defaultExceptions.readerExpectedSymbol();
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
            return this.defaultExceptions.dispatcherParseException();
        }

    }

}
