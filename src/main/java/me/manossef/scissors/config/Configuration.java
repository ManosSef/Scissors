package me.manossef.scissors.config;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.util.Map;

public record Configuration(Settings global, Map<Long, Settings> perGuild, Map<Long, Settings> perChannel) {
    public <T> T getGlobalOption(Option option, Class<T> type) {
        if(!option.properties().getType().equals(type))
            throw new IllegalArgumentException("Option is not of the given type");
        return global.get(option, type);
    }

    public <T> T getOptionForGuild(Option option, Class<T> type, Guild guild) {
        if(!option.properties().getType().equals(type))
            throw new IllegalArgumentException("Option is not of the given type");
        if(perGuild.containsKey(guild.getIdLong())) {
            Settings settings = perGuild.get(guild.getIdLong());
            if(settings.isPresent(option))
                return settings.get(option, type);
        }
        return global.get(option, type);
    }

    public <T> T getOptionForChannel(Option option, Class<T> type, Channel channel) {
        if(!option.properties().getType().equals(type))
            throw new IllegalArgumentException("Option is not of the given type");
        if(perChannel.containsKey(channel.getIdLong())) {
            Settings settings = perChannel.get(channel.getIdLong());
            if(settings.isPresent(option))
                return settings.get(option, type);
        }
        if(channel.getType().isGuild()) {
            GuildChannel guildChannel = (GuildChannel) channel;
            if(perGuild.containsKey(guildChannel.getGuild().getIdLong())) {
                Settings settings = perGuild.get(guildChannel.getGuild().getIdLong());
                if(settings.isPresent(option))
                    return settings.get(option, type);
            }
        }
        return global.get(option, type);
    }

    public <T> void setGlobalOption(Option option, T value) {
        global.set(option, value);
    }

    public <T> void setOptionForGuild(Option option, T value, Guild guild) {
        long id = guild.getIdLong();
        if(!perGuild.containsKey(id))
            perGuild.put(id, new Settings());
        perGuild.get(id).set(option, value);
    }

    public <T> void setOptionForChannel(Option option, T value, Channel channel) {
        long id = channel.getIdLong();
        if(!perChannel.containsKey(id))
            perChannel.put(id, new Settings());
        perChannel.get(id).set(option, value);
    }

    public boolean resetGlobal() {
        return global.resetToDefault();
    }

    public boolean resetForGuild(Guild guild) {
        return perGuild.remove(guild.getIdLong()) != null;
    }

    public boolean resetForChannel(Channel channel) {
        return perChannel.remove(channel.getIdLong()) != null;
    }
}