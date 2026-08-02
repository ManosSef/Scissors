package me.manossef.scissors.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.commoncode.TypeChecks;
import me.manossef.scissors.Scissors;
import net.dv8tion.jda.api.entities.channel.Channel;

import java.util.Arrays;
import java.util.Collection;

public class ChannelArgument implements ArgumentType<Channel> {
    private static final SimpleCommandExceptionType INVALID_MENTION = new SimpleCommandExceptionType(new LiteralMessage("Invalid channel mention"));
    private static final SimpleCommandExceptionType CHANNEL_NOT_FOUND = new SimpleCommandExceptionType(new LiteralMessage("No channel was found"));
    private static final Collection<String> EXAMPLES = Arrays.asList("1428451434373845114", "<#1428451434373845114>");

    public static ChannelArgument channel() {
        return new ChannelArgument();
    }

    @Override
    public Channel parse(StringReader reader) throws CommandSyntaxException {
        String remaining = reader.getRemaining().split(" ")[0];
        if(TypeChecks.isLong(remaining)) {
            reader.setCursor(reader.getCursor() + remaining.length());
            Channel channel = Scissors.DISCORD_API.getChannelById(Channel.class, Long.parseLong(remaining));
            if(channel == null) throw CHANNEL_NOT_FOUND.create();
            return channel;
        }
        if(remaining.startsWith("<#") && remaining.endsWith(">")) {
            String middle = remaining.substring(2, remaining.length() - 1);
            if(TypeChecks.isLong(middle)) {
                reader.setCursor(reader.getCursor() + remaining.length());
                Channel channel = Scissors.DISCORD_API.getChannelById(Channel.class, Long.parseLong(middle));
                if(channel == null) throw CHANNEL_NOT_FOUND.create();
                return channel;
            }
        }
        throw INVALID_MENTION.createWithContext(reader);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}