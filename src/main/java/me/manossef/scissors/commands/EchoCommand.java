package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.SharedConstants;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.util.Collections;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class EchoCommand {
    private static final SimpleCommandExceptionType CANNOT_DELETE = new SimpleCommandExceptionType(new LiteralMessage("Cannot delete this type of message"));
    private static final SimpleCommandExceptionType NOT_IN_GUILD = new SimpleCommandExceptionType(new LiteralMessage("Cannot delete messages in channels outside servers"));
    private static final SimpleCommandExceptionType NO_PERMISSION = new SimpleCommandExceptionType(new LiteralMessage("Cannot delete messages in this channel; no permission"));
    private static final SimpleCommandExceptionType TOO_LONG = new SimpleCommandExceptionType(new LiteralMessage("Cannot send messages longer than " + Message.MAX_CONTENT_LENGTH + " characters"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "echo";
        dispatcher.register(Commands.literal(baseLiteral)
            .then(Commands.argument("text", StringArgumentType.greedyString())
                .executes(context -> echo(context.getSource(), context.getArgument("text", String.class)))
            )
        );
        HelpCommand.addLine(baseLiteral, "Posts a message as the bot.");
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Deletes your command message and posts a new message with the provided text as the bot.
                
                Syntax: %s
                
                Fails when the bot doesn't have permission to delete messages. Always fails when used in a DM.""",
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " <text>")));
    }

    private static int echo(ChatCommandSource source, String message) throws CommandSyntaxException {
        Message commandMessage = source.commandMessage();
        if(!commandMessage.getType().canDelete()) throw CANNOT_DELETE.create();
        MessageChannelUnion channel = commandMessage.getChannel();
        if(!(channel instanceof GuildChannel)) throw NOT_IN_GUILD.create();
        if(message.length() > Message.MAX_CONTENT_LENGTH) throw TOO_LONG.create();
        try {
            commandMessage.delete().queue();
        } catch(InsufficientPermissionException e) {
            throw NO_PERMISSION.create();
        }
        channel.sendMessage(message).setAllowedMentions(Collections.emptyList()).queue();
        return 1;
    }
}
