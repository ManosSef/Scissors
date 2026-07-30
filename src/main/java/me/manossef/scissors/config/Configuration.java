package me.manossef.scissors.config;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record Configuration(Settings global, Map<Long, Settings> perGuild, Map<Long, Settings> perChannel) {
    public Configuration() {
        this(new Settings(), new HashMap<>(), new HashMap<>());
    }

    public <T> T getGlobalOption(Option<T> option) {
        return global.get(option).orElseGet(option::getDefaultValue);
    }

    public <T> T getOptionForGuild(Option<T> option, Guild guild) {
        return getOptionForGuildOnly(option, guild).orElseGet(() -> getGlobalOption(option));
    }

    public <T> Optional<T> getOptionForGuildOnly(Option<T> option, Guild guild) {
        long id = guild.getIdLong();
        if(perGuild.containsKey(id)) return perGuild.get(id).get(option);
        return Optional.empty();
    }

    public <T> T getOptionForChannel(Option<T> option, Channel channel) {
        return getOptionForChannelOnly(option, channel).orElseGet(() -> {
            if(!channel.getType().isGuild()) return getGlobalOption(option);
            return getOptionForGuild(option, ((GuildChannel) channel).getGuild());
        });
    }

    public <T> Optional<T> getOptionForChannelOnly(Option<T> option, Channel channel) {
        long id = channel.getIdLong();
        if(perChannel.containsKey(id)) return perChannel.get(id).get(option);
        return Optional.empty();
    }

    public <T> boolean setGlobalOption(Option<T> option, T value) {
        return global.set(option, value);
    }

    public <T> boolean setOptionForGuild(Option<T> option, T value, Guild guild) {
        long id = guild.getIdLong();
        if(!perGuild.containsKey(id)) perGuild.put(id, new Settings());
        return perGuild.get(id).set(option, value);
    }

    public <T> boolean setOptionForChannel(Option<T> option, T value, Channel channel) {
        long id = channel.getIdLong();
        if(!perChannel.containsKey(id)) perChannel.put(id, new Settings());
        return perChannel.get(id).set(option, value);
    }

    public <T> boolean removeGlobalExplicitOption(Option<T> option) {
        return global.removeExplicit(option);
    }

    public <T> boolean removeExplicitOptionForGuild(Option<T> option, Guild guild) {
        long id = guild.getIdLong();
        if(perGuild.containsKey(id)) return perGuild.get(id).removeExplicit(option);
        return false;
    }

    public <T> boolean removeExplicitOptionForChannel(Option<T> option, Channel channel) {
        long id = channel.getIdLong();
        if(perChannel.containsKey(id)) return perChannel.get(id).removeExplicit(option);
        return false;
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