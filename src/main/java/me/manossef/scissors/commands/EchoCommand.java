package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.arguments.ChannelArgumentType;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.util.Collections;

public class EchoCommand {
    private static final SimpleCommandExceptionType CANNOT_DELETE = new SimpleCommandExceptionType(new LiteralMessage("Cannot delete this type of message"));
    private static final SimpleCommandExceptionType NOT_IN_GUILD = new SimpleCommandExceptionType(new LiteralMessage("Cannot delete messages in channels outside servers"));
    private static final SimpleCommandExceptionType NO_PERMISSION = new SimpleCommandExceptionType(new LiteralMessage("Cannot delete messages in this channel; no permission"));
    private static final SimpleCommandExceptionType TOO_LONG = new SimpleCommandExceptionType(new LiteralMessage("Cannot send messages longer than " + Message.MAX_CONTENT_LENGTH_COMPONENT_V2 + " characters"));
    private static final SimpleCommandExceptionType NOT_MESSAGE_CHANNEL = new SimpleCommandExceptionType(new LiteralMessage("That channel is not a message channel"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "echo";
        dispatcher.register(Commands.literal(baseLiteral)
            .then(Commands.literal("tenfold")
                .requires(Commands.devRestricted())
                .then(Commands.argument("text", StringArgumentType.greedyString())
                    .executes(context -> echoTenfold(context.getSource(), context.getArgument("text", String.class)))
                )
            )
            .then(Commands.argument("channel", ChannelArgumentType.channel())
                .requires(Commands.devRestricted())
                .then(Commands.argument("text", StringArgumentType.greedyString())
                    .executes(context -> echo(context.getSource(), context.getArgument("text", String.class), context.getArgument("channel", Channel.class)))
                )
            )
            .then(Commands.argument("text", StringArgumentType.greedyString())
                .executes(context -> echo(context.getSource(), context.getArgument("text", String.class)))
            )
        );
        HelpCommand.addLine(baseLiteral, s -> "Posts a message as the bot.");
        HelpCommand.addLiteral(baseLiteral, source -> String.format("""
                Deletes your command message and posts a new message with the provided text as the bot.
                
                Syntax: %s
                
                Fails when the bot doesn't have permission to delete messages. Always fails when used in a DM.""",
            Commands.format(baseLiteral + " <text>", source.commandMessage().getChannel())));
    }

    private static int echo(ChatCommandSource source, String message, Channel channel) throws CommandSyntaxException {
        Message commandMessage = source.commandMessage();
        if(!commandMessage.getType().canDelete()) throw CANNOT_DELETE.create();
        if(!(channel instanceof GuildChannel)) throw NOT_IN_GUILD.create();
        if(!(channel instanceof MessageChannelUnion messageChannel)) throw NOT_MESSAGE_CHANNEL.create();
        if(message.length() > Message.MAX_CONTENT_LENGTH_COMPONENT_V2) throw TOO_LONG.create();
        Message referenced = commandMessage.getReferencedMessage();
        boolean isRemote = channel.getIdLong() != source.commandMessage().getChannel().getIdLong();
        if(!isRemote) try {
            commandMessage.delete().queue();
        } catch(InsufficientPermissionException e) {
            throw NO_PERMISSION.create();
        }
        MessageCreateAction toCreate = message.length() > Message.MAX_CONTENT_LENGTH
            ? messageChannel.sendMessageComponents(Container.of(TextDisplay.of(message)))
                .useComponentsV2()
                .setAllowedMentions(Collections.emptyList())
            : messageChannel.sendMessage(message).setAllowedMentions(Collections.emptyList());
        if(referenced != null) toCreate.setMessageReference(referenced).mentionRepliedUser(false).queue();
        else toCreate.queue();
        if(isRemote) source.sendSuccess("Posted the specified message to " + messageChannel.getAsMention() + " (ID: " + messageChannel.getId() + ")", true);
        return 1;
    }

    private static int echo(ChatCommandSource source, String message) throws CommandSyntaxException {
        return echo(source, message, source.commandMessage().getChannel());
    }

    private static int echoTenfold(ChatCommandSource source, String message) throws CommandSyntaxException {
        return echo(source, message.repeat(10), source.commandMessage().getChannel());
    }
}