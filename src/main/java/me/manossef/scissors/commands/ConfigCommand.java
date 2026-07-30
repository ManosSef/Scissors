package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.config.Option;
import me.manossef.scissors.config.OptionValue;
import me.manossef.scissors.config.Options;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import static net.dv8tion.jda.api.utils.MarkdownUtil.bold;
import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class ConfigCommand {
    private static final SimpleCommandExceptionType INVALID_CONTEXT = new SimpleCommandExceptionType(new LiteralMessage("Invalid option context"));
    private static final SimpleCommandExceptionType NOT_IN_GUILD = new SimpleCommandExceptionType(new LiteralMessage("This channel is not in a server"));
    private static final SimpleCommandExceptionType ALREADY_DEFAULT_GLOBAL = new SimpleCommandExceptionType(new LiteralMessage("Nothing changed; the global values of all options are already the default ones"));
    private static final SimpleCommandExceptionType ALREADY_DEFAULT_GUILD = new SimpleCommandExceptionType(new LiteralMessage("Nothing changed; no explicit values for any option have been set for this server"));
    private static final SimpleCommandExceptionType ALREADY_DEFAULT_CHANNEL = new SimpleCommandExceptionType(new LiteralMessage("Nothing changed; no explicit values for any option have been set for this channel"));
    private static final SimpleCommandExceptionType IMPOSSIBLE_ERROR = new SimpleCommandExceptionType(new LiteralMessage("This error should not have happened! Discord must be freaking out!"));
    private static final SimpleCommandExceptionType NO_PERMS_IN_GUILD = new SimpleCommandExceptionType(new LiteralMessage("You need to have the \"Manage Server\" or \"Administrator\" permission to edit the bot's options for this server"));
    private static final SimpleCommandExceptionType NO_PERMS_IN_CHANNEL = new SimpleCommandExceptionType(new LiteralMessage("You need to have the \"Manage Channel\" or \"Administrator\" permission to edit the bot's options for this channel"));
    private static final DynamicCommandExceptionType NO_EXPLICIT_VALUE_IN_GUILD = new DynamicCommandExceptionType(option -> new LiteralMessage("No explicit value for the option " + option + " has been set for this server"));
    private static final DynamicCommandExceptionType NO_EXPLICIT_VALUE_IN_CHANNEL = new DynamicCommandExceptionType(option -> new LiteralMessage("No explicit value for the option " + option + " has been set for this channel"));
    private static final Dynamic2CommandExceptionType SAME_GLOBAL_VALUE = new Dynamic2CommandExceptionType((option, value) -> new LiteralMessage("The explicit global value of the option " + option + " is already " + value));
    private static final Dynamic2CommandExceptionType SAME_GUILD_VALUE = new Dynamic2CommandExceptionType((option, value) -> new LiteralMessage("The explicit value of the option " + option + " for this server is already " + value));
    private static final Dynamic2CommandExceptionType SAME_CHANNEL_VALUE = new Dynamic2CommandExceptionType((option, value) -> new LiteralMessage("The explicit value of the option " + option + " for this channel is already " + value));

    /*
    config global explicit remove <option>          me
    config server explicit remove <option>          manage server
    config server <server>                          me
    config server <server> reset                    me
    config server <server> explicit <option>        me
    config server <server> explicit remove <option> me
    config server <server> <option>                 me
    config server <server> <option> <value>         me
    config channel explicit remove <option>         manage channel
    config channel <channel>                        everyone
    config channel <channel> reset                  manage channel
    config channel <channel> explicit <option>      everyone
    config channel <channel> explicit remove <option> manage channel
    config channel <channel> <option>               everyone
    config channel <channel> <option> <value>       manage channel
     */

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "config";
        dispatcher.register(Commands.literal(baseLiteral)
            .then(Commands.literal("dump")
                .requires(Commands.devRestricted())
                .executes(context -> dumpConfig(context.getSource()))
            )
            .then(optionsArguments(Commands.literal("global"), OptionContext.GLOBAL).requires(Commands.devRestricted()))
            .then(optionsArguments(Commands.literal("server"), OptionContext.PER_GUILD))
            .then(optionsArguments(Commands.literal("channel"), OptionContext.PER_CHANNEL))
        );
        HelpCommand.addLine(baseLiteral, "Queries or edits the bot's settings.");
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Queries or edits the bot's configuration options.
                
                Option values can apply either to a whole server or to a specific channel. The effective value of an option for a channel is the value explicitly applied to it if any, or the value applied to the server if any, or the default value. \
                Option values persist across restarts of the bot.
                
                Here are all available syntaxes for this command:
                - %1$s: Returns the values of all options for the server/channel the command was run in. For each option, the explicitly applied value is returned, falling back to the effective value if no explicit value exists.
                - %2$s: Returns the effective value of the specified option for the server/channel the command was run in.
                - %3$s: Sets the value of the specified option for the server/channel the command was run in to the specified value.
                - %4$s: Resets the values of all options for the server/channel the command was run in to the default ones.
                - %5$s: Returns the explicitly applied value of the specified option for the server/channel the command was run in. Fails if this option has not been given an explicit value for this server/channel.
                
                Use %6$s as the first argument to affect the server, and %7$s to affect the channel the command was run in.
                
                %8$s commands always fail if run in a DM.
                
                You need the "Manage Server" permission to run %8$s commands, and the "Manage Channel" permission in the respective channel to run %9$s commands.
                
                Use %10$s to see all available options.""",
            Commands.format(baseLiteral + " (server|channel)"),
            Commands.format(baseLiteral + " (server|channel) <option>"),
            Commands.format(baseLiteral + " (server|channel) <option> <value>"),
            Commands.format(baseLiteral + " (server|channel) reset"),
            Commands.format(baseLiteral + " (server|channel) explicit <option>"),
            monospace("server"),
            monospace("channel"),
            Commands.format(baseLiteral + " server ..."),
            Commands.format(baseLiteral + " channel ..."),
            Commands.format("info options")));
    }

    private static ArgumentBuilder<ChatCommandSource, ?> optionsArguments(ArgumentBuilder<ChatCommandSource, ?> argument, OptionContext optionContext) {
        argument.executes(context -> getAllOptions(context.getSource(), optionContext))
            .then(Commands.literal("reset")
                .executes(context -> resetOptions(context.getSource(), optionContext))
            );
        ArgumentBuilder<ChatCommandSource, ?> onlyArgument = Commands.literal("explicit");
        for(Option<?> option : Options.values()) {
            ArgumentBuilder<ChatCommandSource, ?> optionArgument = Commands.literal(option.getName())
                .executes(context -> getOptionValue(context.getSource(), option, optionContext, true));
            optionArgument.then(Commands.argument("value", option.getArgumentType())
                .executes(context -> setOptionValue(context.getSource(), option.castValue(context.getArgument("value", option.getType())), optionContext))
            );
            argument.then(optionArgument);
            onlyArgument.then(Commands.literal(option.getName())
                .executes(context -> getOptionValue(context.getSource(), option, optionContext, false))
            );
        }
        argument.then(onlyArgument);
        return argument;
    }

    private static int dumpConfig(ChatCommandSource source) {
        source.sendSuccess(Scissors.getConfiguration().toString());
        return 1;
    }

    private static int resetOptions(ChatCommandSource source, OptionContext optionContext) throws CommandSyntaxException {
        switch(optionContext) {
            case GLOBAL -> {
                if(Scissors.getConfiguration().resetGlobal())
                    source.sendSuccess("Reset the global values of all options to the default ones");
                else
                    throw ALREADY_DEFAULT_GLOBAL.create();
            }
            case PER_GUILD -> {
                if(!(source.commandMessage().getChannel() instanceof GuildChannel guildChannel))
                    throw NOT_IN_GUILD.create();
                if(!canEditPerGuild(source.user(), guildChannel.getGuild()))
                    throw NO_PERMS_IN_GUILD.create();
                if(Scissors.getConfiguration().resetForGuild(source.commandMessage().getGuild()))
                    source.sendSuccess("Removed the explicit values of all options for this server");
                else
                    throw ALREADY_DEFAULT_GUILD.create();
            }
            case PER_CHANNEL -> {
                if(!canEditPerChannel(source.user(), source.commandMessage().getChannel()))
                    throw NO_PERMS_IN_CHANNEL.create();
                if(Scissors.getConfiguration().resetForChannel(source.commandMessage().getChannel()))
                    source.sendSuccess("Removed the explicit values of all options for this channel");
                else
                    throw ALREADY_DEFAULT_CHANNEL.create();
            }
        }
        Scissors.saveConfiguration();
        return 1;
    }

    private static int getAllOptions(ChatCommandSource source, OptionContext optionContext) throws CommandSyntaxException {
        switch(optionContext) {
            case GLOBAL -> {
                StringBuilder builder = new StringBuilder("Here are the global values of all options:");
                for(Option<?> option : Options.values())
                    builder.append("\n- ").append(monospace(option.getName())).append(": ")
                        .append(bold(Scissors.getConfiguration().getGlobalOption(option).toString()));
                source.sendSuccess(builder.toString());
            }
            case PER_GUILD -> {
                StringBuilder builder = new StringBuilder("Here are the values of all options for this server:");
                for(Option<?> option : Options.values()) {
                    builder.append("\n- ").append(monospace(option.getName())).append(": ");
                    Object value = Scissors.getConfiguration().getOptionForGuildOnly(option, source.commandMessage().getGuild());
                    if(value == null) {
                        builder.append("no explicit value; effective value: ");
                        value = Scissors.getConfiguration().getOptionForGuild(option, source.commandMessage().getGuild());
                    }
                    builder.append(bold(value.toString()));
                }
                source.sendSuccess(builder.toString());
            }
            case PER_CHANNEL -> {
                StringBuilder builder = new StringBuilder("Here are the values of all options for this channel:");
                for(Option<?> option : Options.values()) {
                    builder.append("\n- ").append(monospace(option.getName())).append(": ");
                    Object value = Scissors.getConfiguration().getOptionForChannelOnly(option, source.commandMessage().getChannel());
                    if(value == null) {
                        builder.append("no explicit value; effective value: ");
                        value = Scissors.getConfiguration().getOptionForChannel(option, source.commandMessage().getChannel());
                    }
                    builder.append(bold(value.toString()));
                }
                source.sendSuccess(builder.toString());
            }
            default -> throw INVALID_CONTEXT.create();
        }
        return 1;
    }

    private static <T> int getOptionValue(ChatCommandSource source, Option<T> option, OptionContext optionContext, boolean defaultToHigherPower) throws CommandSyntaxException {
        T value;
        switch(optionContext) {
            case GLOBAL -> {
                value = Scissors.getConfiguration().getGlobalOption(option);
                source.sendSuccess("The current global value of the option " + monospace(option.getName()) + " is " + bold(value.toString()));
            }
            case PER_GUILD -> {
                if(!(source.commandMessage().getChannel() instanceof GuildChannel))
                    throw NOT_IN_GUILD.create();
                if(defaultToHigherPower) {
                    value = Scissors.getConfiguration().getOptionForGuild(option, source.commandMessage().getGuild());
                    source.sendSuccess("The current effective value of the option " + monospace(option.getName()) + " for this server is " + bold(value.toString()));
                } else {
                    value = Scissors.getConfiguration().getOptionForGuildOnly(option, source.commandMessage().getGuild())
                        .orElseThrow(() -> NO_EXPLICIT_VALUE_IN_GUILD.create(monospace(option.getName())));
                    source.sendSuccess("The current explicit value of the option " + monospace(option.getName()) + " for this server is " + bold(value.toString()));
                }
            }
            case PER_CHANNEL -> {
                if(defaultToHigherPower) {
                    value = Scissors.getConfiguration().getOptionForChannel(option, source.commandMessage().getChannel());
                    source.sendSuccess("The current effective value of the option " + monospace(option.getName()) + " for this channel is " + bold(value.toString()));
                } else {
                    value = Scissors.getConfiguration().getOptionForChannelOnly(option, source.commandMessage().getChannel())
                        .orElseThrow(() -> NO_EXPLICIT_VALUE_IN_CHANNEL.create(monospace(option.getName())));
                    source.sendSuccess("The current explicit value of the option " + monospace(option.getName()) + " for this channel is " + bold(value.toString()));
                }
            }
            default -> throw INVALID_CONTEXT.create();
        }
        return getReturnValue(value);
    }

    private static <T> int setOptionValue(ChatCommandSource source, OptionValue<T> optionValue, OptionContext optionContext) throws CommandSyntaxException {
        Option<T> option = optionValue.option();
        T value = optionValue.value();
        switch(optionContext) {
            case GLOBAL -> {
                boolean success = Scissors.getConfiguration().setGlobalOption(option, value);
                if(!success) throw SAME_GLOBAL_VALUE.create(monospace(option.getName()), bold(value.toString()));
                source.sendSuccess("Set the explicit global value of the option " + monospace(option.getName()) + " to " + bold(value.toString()));
            }
            case PER_GUILD -> {
                if(!(source.commandMessage().getChannel() instanceof GuildChannel guildChannel))
                    throw NOT_IN_GUILD.create();
                if(canEditPerGuild(source.user(), guildChannel.getGuild())) {
                    boolean success = Scissors.getConfiguration().setOptionForGuild(option, value, source.commandMessage().getGuild());
                    if(!success) throw SAME_GUILD_VALUE.create(monospace(option.getName()), bold(value.toString()));
                    source.sendSuccess("Set the explicit value of the option " + monospace(option.getName()) + " for this server to " + bold(value.toString()));
                } else throw NO_PERMS_IN_GUILD.create();
            }
            case PER_CHANNEL -> {
                if(canEditPerChannel(source.user(), source.commandMessage().getChannel())) {
                    boolean success = Scissors.getConfiguration().setOptionForChannel(option, value, source.commandMessage().getChannel());
                    if(!success) throw SAME_CHANNEL_VALUE.create(monospace(option.getName()), bold(value.toString()));
                    source.sendSuccess("Set the explicit value of the option " + monospace(option.getName()) + " for this channel to " + bold(value.toString()));
                } else throw NO_PERMS_IN_CHANNEL.create();
            }
            default -> throw INVALID_CONTEXT.create();
        }
        Scissors.saveConfiguration();
        return getReturnValue(value);
    }

    private static int getReturnValue(Object value) {
        return value instanceof Number n ? (int) n :
            value instanceof Boolean b ? (b ? 1 : 0) :
            value instanceof Enum<?> e ? e.ordinal() : value.hashCode();
    }

    private static boolean canEditPerGuild(User user, Guild guild) throws CommandSyntaxException {
        if(user.getIdLong() == SharedConstants.MY_USER_ID)
            return true;
        Member member = guild.retrieveMemberById(user.getIdLong()).complete();
        if(member == null)
            throw IMPOSSIBLE_ERROR.create();
        return member.hasPermission(Permission.MANAGE_SERVER);
    }

    private static boolean canEditPerChannel(User user, MessageChannelUnion channel) throws CommandSyntaxException {
        if(user.getIdLong() == SharedConstants.MY_USER_ID)
            return true;
        if(!(channel instanceof GuildChannel guildChannel))
            return true;
        Member member = guildChannel.getGuild().retrieveMemberById(user.getIdLong()).complete();
        if(member == null)
            throw IMPOSSIBLE_ERROR.create();
        return member.hasPermission(guildChannel, Permission.MANAGE_CHANNEL);
    }

    private enum OptionContext {
        GLOBAL,
        PER_GUILD,
        PER_CHANNEL
    }
}