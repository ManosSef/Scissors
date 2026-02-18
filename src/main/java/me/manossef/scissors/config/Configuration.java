package me.manossef.scissors.config;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.util.Map;

public record Configuration(Settings global, Map<Long, Settings> perGuild, Map<Long, Settings> perChannel) {

    public <T> T getGlobalOption(Options option, Class<T> type) {

        if(!option.option().getType().equals(type))
            throw new IllegalArgumentException("Option is not of the given type");
        return global.get(option, type);

    }

    public <T> T getOptionForGuild(Options option, Class<T> type, Guild guild) {

        if(!option.option().getType().equals(type))
            throw new IllegalArgumentException("Option is not of the given type");
        if(perGuild.containsKey(guild.getIdLong())) {

            Settings settings = perGuild.get(guild.getIdLong());
            if(settings.isPresent(option.option()))
                return settings.get(option, type);

        }
        return global.get(option, type);

    }

    public <T> T getOptionForChannel(Options option, Class<T> type, Channel channel) {

        if(!option.option().getType().equals(type))
            throw new IllegalArgumentException("Option is not of the given type");
        if(perChannel.containsKey(channel.getIdLong())) {

            Settings settings = perChannel.get(channel.getIdLong());
            if(settings.isPresent(option.option()))
                return settings.get(option, type);

        }
        if(channel.getType().isGuild()) {

            GuildChannel guildChannel = (GuildChannel) channel;
            if(perGuild.containsKey(guildChannel.getGuild().getIdLong())) {

                Settings settings = perGuild.get(guildChannel.getGuild().getIdLong());
                if(settings.isPresent(option.option()))
                    return settings.get(option, type);

            }

        }
        return global.get(option, type);

    }

    public <T> T getOptionForChannelOnly(Options option, Class<T> type, Channel channel) {

        if(!option.option().getType().equals(type))
            throw new IllegalArgumentException("Option is not of the given type");
        if(perChannel.containsKey(channel.getIdLong())) {

            Settings settings = perChannel.get(channel.getIdLong());
            if(settings.isPresent(option.option()))
                return settings.get(option, type);

        }
        return null;

    }

    public <T> void setGlobalOption(Option<T> option, T value) {

        global.set(option, value);

    }

    public <T> void setOptionForGuild(Option<T> option, T value, Guild guild) {

        long id = guild.getIdLong();
        if(!perGuild.containsKey(id))
            perGuild.put(id, new Settings());
        perGuild.get(id).set(option, value);

    }

    public <T> void setOptionForChannel(Option<T> option, T value, Channel channel) {

        long id = channel.getIdLong();
        if(!perChannel.containsKey(id))
            perChannel.put(id, new Settings());
        perChannel.get(id).set(option, value);

    }

}
