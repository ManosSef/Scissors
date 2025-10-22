package me.manossef.scissors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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

    public static void dispatch(Message message, User user) {

        String command = message.getContentRaw().replaceFirst(SharedConstants.COMMAND_PREFIX, "").strip();
        MessageChannel channel = message.getChannel();
        source = source.withMessage(message).withUser(user);
        try {

            int result = DISPATCHER.execute(command, source);
            DevGuild.logCommand(user.getName() + " (" + user.getId() + ") executed command `" + command.replace("`", "\\u0060") + "` in " + channel.getAsMention() + " (" + channel.getId() + ") and succeeded with return value " + result);

        } catch(CommandSyntaxException e) {

            source.sendFailure(e.getMessage());
            DevGuild.logCommand(user.getName() + " (" + user.getId() + ") executed command `" + command.replace("`", "\\u0060") + "` in " + channel.getAsMention() + " (" + channel.getId() + ") and failed");

        } catch(Exception e) {

            source.sendError(e.getMessage());
            StringBuilder stackTrace = new StringBuilder();
            for(StackTraceElement element : e.getStackTrace())
                stackTrace.append("\t`at ").append(element.toString()).append("`\n");
            DevGuild.logCommand(user.getName() + " (" + user.getId() + ") executed command `" + command.replace("`", "\\u0060") + "` in " + channel.getAsMention() + " (" + channel.getId() + ") and threw an exception: `" +
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

        EchoCommand.register(dispatcher);
        HelpCommand.register(dispatcher);
        IssueCommand.register(dispatcher);
        PingCommand.register(dispatcher);
        RockPaperScissorsCommand.register(dispatcher);
        RollCommand.register(dispatcher);
        SuggestCommand.register(dispatcher);
        ListGuildsCommand.register(dispatcher);

    }

    public static Predicate<ChatCommandSource> devRestricted() {

        return source -> source.user().getIdLong() == SharedConstants.MY_USER_ID;

    }

}
