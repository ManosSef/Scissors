package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.*;
import me.manossef.commoncode.function.TriFunction;
import me.manossef.commoncode.objects.Either;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.arguments.ChannelArgument;
import me.manossef.scissors.config.Option;
import me.manossef.scissors.config.OptionValue;
import me.manossef.scissors.config.Options;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

import static net.dv8tion.jda.api.utils.MarkdownUtil.bold;
import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class ConfigCommand {
    private static final IntFunction<String> GLOBAL_RESET_SUCCESS = value -> {
        if(value == 1) return "Reset the global value of " + value + " option to the default one";
        else return "Reset the global values of " + value + " options to the default ones";
    };
    private static final SimpleCommandExceptionType GLOBAL_RESET_ERROR = new SimpleCommandExceptionType(new LiteralMessage(
        "Nothing changed; the global values of all options are already the default ones"));
    private static final IntFunction<String> GUILD_RESET_SUCCESS = value -> {
        if(value == 1) return "Removed the explicit value of " + value + " option for this server";
        else return "Removed the explicit values of " + value + " options for this server";
    };
    private static final SimpleCommandExceptionType GUILD_RESET_ERROR = new SimpleCommandExceptionType(new LiteralMessage(
        "Nothing changed; no explicit values for any options have been set for this server"));
    private static final BiFunction<Integer, String, String> GUILD_ID_RESET_SUCCESS = (value, guild) -> {
        if(value == 1) return "Removed the explicit value of " + value + " option for the server \"" + guild + "\"";
        else return "Removed the explicit values of " + value + " options for the server \"" + guild + "\"";
    };
    private static final DynamicCommandExceptionType GUILD_ID_RESET_ERROR = new DynamicCommandExceptionType(guild -> new LiteralMessage(
        "Nothing changed; no explicit values for any options have been set for the server \"" + guild + "\""));
    private static final IntFunction<String> CHANNEL_RESET_SUCCESS = value -> {
        if(value == 1) return "Removed the explicit value of " + value + " option for this channel";
        else return "Removed the explicit values of " + value + " options for this channel";
    };
    private static final SimpleCommandExceptionType CHANNEL_RESET_ERROR = new SimpleCommandExceptionType(new LiteralMessage(
        "Nothing changed; no explicit values for any options have been set for this channel"));
    private static final BiFunction<Integer, String, String> CHANNEL_ID_RESET_SUCCESS = (value, channel) -> {
        if(value == 1) return "Removed the explicit value of " + value + " option for " + channel;
        else return "Removed the explicit values of " + value + " options for " + channel;
    };
    private static final DynamicCommandExceptionType CHANNEL_ID_RESET_ERROR = new DynamicCommandExceptionType(channel -> new LiteralMessage(
        "Nothing changed; no explicit values for any options have been set for " + channel));
    private static final BinaryOperator<String> GLOBAL_GET_SUCCESS = (option, value) ->
        "The current global value of the option " + option + " is " + value;
    private static final BinaryOperator<String> GUILD_GET_SUCCESS = (option, value) ->
        "The current effective value of the option " + option + " for this server is " + value;
    private static final BinaryOperator<String> GUILD_EXPLICIT_SUCCESS = (option, value) ->
        "The current explicit value of the option " + option + " for this server is " + value;
    private static final DynamicCommandExceptionType GUILD_EXPLICIT_ERROR = new DynamicCommandExceptionType(option -> new LiteralMessage(
        "No explicit value for the option " + option + " has been set for this server"));
    private static final TriFunction<String, String, String, String> GUILD_ID_GET_SUCCESS = (option, value, guild) ->
        "The current effective value of the option " + option + " for the server \"" + guild + "\" is " + value;
    private static final TriFunction<String, String, String, String> GUILD_ID_EXPLICIT_SUCCESS = (option, value, guild) ->
        "The current explicit value of the option " + option + " for the server \"" + guild + "\" is " + value;
    private static final Dynamic2CommandExceptionType GUILD_ID_EXPLICIT_ERROR = new Dynamic2CommandExceptionType((option, guild) -> new LiteralMessage(
        "No explicit value for the option " + option + " has been set for the server \"" + guild + "\""));
    private static final BinaryOperator<String> CHANNEL_GET_SUCCESS = (option, value) ->
        "The current effective value of the option " + option + " for this channel is " + value;
    private static final BinaryOperator<String> CHANNEL_EXPLICIT_SUCCESS = (option, value) ->
        "The current explicit value of the option " + option + " for this channel is " + value;
    private static final DynamicCommandExceptionType CHANNEL_EXPLICIT_ERROR = new DynamicCommandExceptionType(option -> new LiteralMessage(
        "No explicit value for the option " + option + " has been set for this channel"));
    private static final TriFunction<String, String, String, String> CHANNEL_ID_GET_SUCCESS = (option, value, channel) ->
        "The current effective value of the option " + option + " for " + channel + " is " + value;
    private static final TriFunction<String, String, String, String> CHANNEL_ID_EXPLICIT_SUCCESS = (option, value, channel) ->
        "The current explicit value of the option " + option + " for " + channel + " is " + value;
    private static final Dynamic2CommandExceptionType CHANNEL_ID_EXPLICIT_ERROR = new Dynamic2CommandExceptionType((option, channel) -> new LiteralMessage(
        "No explicit value for the option " + option + " has been set for " + channel));
    private static final BinaryOperator<String> GLOBAL_SET_SUCCESS = (option, value) ->
        "Set the global value of the option " + option + " to " + value;
    private static final Dynamic2CommandExceptionType GLOBAL_SET_ERROR = new Dynamic2CommandExceptionType((option, value) -> new LiteralMessage(
        "The global value of the option " + option + " is already " + value));
    private static final BinaryOperator<String> GUILD_SET_SUCCESS = (option, value) ->
        "Set the explicit value of the option " + option + " for this server to " + value;
    private static final Dynamic2CommandExceptionType GUILD_SET_ERROR = new Dynamic2CommandExceptionType((option, value) -> new LiteralMessage(
        "The explicit value of the option " + option + " for this server is already " + value));
    private static final TriFunction<String, String, String, String> GUILD_ID_SET_SUCCESS = (option, value, guild) ->
        "Set the explicit value of the option " + option + " for the server \"" + guild + "\" to " + value;
    private static final Dynamic3CommandExceptionType GUILD_ID_SET_ERROR = new Dynamic3CommandExceptionType((option, value, guild) -> new LiteralMessage(
        "The explicit value of the option " + option + " for the server \"" + guild + "\" is already " + value));
    private static final BinaryOperator<String> CHANNEL_SET_SUCCESS = (option, value) ->
        "Set the explicit value of the option " + option + " for this channel to " + value;
    private static final Dynamic2CommandExceptionType CHANNEL_SET_ERROR = new Dynamic2CommandExceptionType((option, value) -> new LiteralMessage(
        "The explicit value of the option " + option + " for this channel is already " + value));
    private static final TriFunction<String, String, String, String> CHANNEL_ID_SET_SUCCESS = (option, value, channel) ->
        "Set the explicit value of the option " + option + " for " + channel + " to " + value;
    private static final Dynamic3CommandExceptionType CHANNEL_ID_SET_ERROR = new Dynamic3CommandExceptionType((option, value, channel) -> new LiteralMessage(
        "The explicit value of the option " + option + " for " + channel + " is already " + value));
    private static final UnaryOperator<String> GLOBAL_EXPLICIT_REMOVE_SUCCESS = option ->
        "Reset the global value of the option " + option + " to the default one";
    private static final DynamicCommandExceptionType GLOBAL_EXPLICIT_REMOVE_ERROR = new DynamicCommandExceptionType(option -> new LiteralMessage(
        "No global value for the option " + option + " has been set"));
    private static final UnaryOperator<String> GUILD_EXPLICIT_REMOVE_SUCCESS = option ->
        "Removed the explicit value of the option " + option + " for this server";
    private static final BinaryOperator<String> GUILD_ID_EXPLICIT_REMOVE_SUCCESS = (option, guild) ->
        "Removed the explicit value of the option " + option + " for the server \"" + guild + "\"";
    private static final UnaryOperator<String> CHANNEL_EXPLICIT_REMOVE_SUCCESS = option ->
        "Removed the explicit value of the option " + option + " for this channel";
    private static final BinaryOperator<String> CHANNEL_ID_EXPLICIT_REMOVE_SUCCESS = (option, channel) ->
        "Removed the explicit value of the option " + option + " for " + channel;
    private static final SimpleCommandExceptionType NOT_IN_GUILD = new SimpleCommandExceptionType(new LiteralMessage(
        "This channel is not in a server"));
    private static final SimpleCommandExceptionType NO_PERMS_IN_GUILD = new SimpleCommandExceptionType(new LiteralMessage(
        "You need to have the \"Manage Server\" or \"Administrator\" permission to edit the bot's options for this server"));
    private static final SimpleCommandExceptionType NO_PERMS_IN_CHANNEL = new SimpleCommandExceptionType(new LiteralMessage(
        "You need to have the \"Manage Channel\" or \"Administrator\" permission to edit the bot's options for this channel"));
    private static final SimpleCommandExceptionType INVALID_CONTEXT = new SimpleCommandExceptionType(new LiteralMessage(
        "Invalid option context"));
    private static final SimpleCommandExceptionType IMPOSSIBLE_ERROR = new SimpleCommandExceptionType(new LiteralMessage(
        "This error should not have happened! Discord must be freaking out!"));
    private static final SimpleCommandExceptionType CANNOT_EDIT_DM_FROM_OUTSIDE = new SimpleCommandExceptionType(new LiteralMessage(
        "You cannot see or edit the bot's options for a DM channel from outside"));
    private static final SimpleCommandExceptionType CANNOT_EDIT_CHANNEL_FROM_OUTSIDE_GUILD =  new SimpleCommandExceptionType(new LiteralMessage(
        "You cannot see or edit the bot's options for a server channel from outside the server it's in"));
    private static final DynamicCommandExceptionType NO_PERMS_IN_TARGET_CHANNEL = new DynamicCommandExceptionType(channel -> new LiteralMessage(
        "You need to have the \"Manage Channel\" or \"Administrator\" permission in " + channel + " to edit the bot's options for it"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "config";
        dispatcher.register(Commands.literal(baseLiteral)
            .then(Commands.literal("dump")
                .requires(Commands.devRestricted())
                .executes(context -> dumpConfig(context.getSource()))
            )
            .then(optionsArguments(Commands.literal("global"), context -> new OptionContext(Either.ofLeft(OptionContext.Source.GLOBAL))).requires(Commands.devRestricted()))
            .then(optionsArguments(Commands.literal("server"), context -> new OptionContext(Either.ofLeft(OptionContext.Source.GUILD)))
                .then(optionsArguments(Commands.argument("id", LongArgumentType.longArg()), context -> {
                    Guild guild = Scissors.DISCORD_API.getGuildById(LongArgumentType.getLong(context, "id"));
                    if(guild == null) throw Commands.GUILD_NOT_FOUND.create();
                    return new OptionContext(Either.ofRight(Either.ofLeft(guild)));
                }).requires(Commands.devRestricted()))
            )
            .then(optionsArguments(Commands.literal("channel"), context -> new OptionContext(Either.ofLeft(OptionContext.Source.CHANNEL)))
                .then(optionsArguments(Commands.argument("channel", ChannelArgument.channel()), context -> new OptionContext(Either.ofRight(Either.ofRight(context.getArgument("channel", Channel.class))))))
            )
        );
        HelpCommand.addLine(baseLiteral, "Queries or edits the bot's settings.");
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Queries or edits the bot's configuration options.
                
                Option values can apply either to a whole server or to a specific channel. The effective value of an option for a channel is the value explicitly applied to it if any, or the value applied to the server if any, or the default value. \
                Option values persist across restarts of the bot.
                
                For all syntaxes, %12$s can be any one of the following:
                - %7$s: The command will apply to the whole server it was run in.
                - %8$s: The command will apply to the channel it was run in.
                - %13$s: The command will apply to the specified channel, so long as it's in the same server the command was run in. Channels can be specified either by their ID or by their mention (%14$s format).
                
                Here are all available syntaxes for this command:
                - %1$s: Returns the values of all options. For each option, the explicitly applied value is returned, falling back to the effective value if no explicit value exists.
                - %2$s: Returns the effective value of the specified option.
                - %3$s: Sets the value of the specified option to the specified value.
                - %4$s: Removes the explicitly applied values of all options.
                - %5$s: Returns the explicitly applied value of the specified option. Fails if this option has not been given an explicit value for this scope.
                - %6$s: Removes the explicitly applied value of the specified option. Fails if this option has not been given an explicit value for this scope.
                
                %9$s commands always fail if run in a DM.
                
                You need the "Manage Server" permission to run %9$s commands, and the "Manage Channel" permission in the respective channel to run %10$s commands.
                
                Use %11$s to see all available options.""",
            Commands.format(baseLiteral + " <scope>"),
            Commands.format(baseLiteral + " <scope> <option>"),
            Commands.format(baseLiteral + " <scope> <option> <value>"),
            Commands.format(baseLiteral + " <scope> reset"),
            Commands.format(baseLiteral + " <scope> explicit <option>"),
            Commands.format(baseLiteral + " <scope> explicit remove <option>"),
            monospace("server"),
            monospace("channel"),
            Commands.format(baseLiteral + " server ..."),
            Commands.format(baseLiteral + " channel ..."),
            Commands.format("info options"),
            monospace("<scope>"),
            monospace("channel <channel>"),
            monospace("<#ID>")));
    }

    private static ArgumentBuilder<ChatCommandSource, ?> optionsArguments(ArgumentBuilder<ChatCommandSource, ?> argument, OptionContextFunction optionContext) {
        argument.executes(context -> getAllOptions(context.getSource(), optionContext.apply(context)))
            .then(Commands.literal("reset")
                .executes(context -> resetOptions(context.getSource(), optionContext.apply(context)))
            );
        ArgumentBuilder<ChatCommandSource, ?> onlyArgument = Commands.literal("explicit");
        for(Option<?> option : Options.values()) {
            ArgumentBuilder<ChatCommandSource, ?> optionArgument = Commands.literal(option.getName())
                .executes(context -> getOptionValue(context.getSource(), option, optionContext.apply(context), true));
            optionArgument.then(Commands.argument("value", option.getArgumentType())
                .executes(context -> setOptionValue(context.getSource(), option.castValue(context.getArgument("value", option.getType())), optionContext.apply(context)))
            );
            argument.then(optionArgument);
            onlyArgument.then(Commands.literal(option.getName())
                .executes(context -> getOptionValue(context.getSource(), option, optionContext.apply(context), false))
            ).then(Commands.literal("remove").then(Commands.literal(option.getName())
                .executes(context -> removeExplicitOptionValue(context.getSource(), option, optionContext.apply(context)))
            ));
        }
        argument.then(onlyArgument);
        return argument;
    }

    private static int dumpConfig(ChatCommandSource source) {
        source.sendSuccess(Scissors.getConfiguration().toString(), false);
        return 1;
    }

    private static int resetOptions(ChatCommandSource source, OptionContext optionContext) throws CommandSyntaxException {
        int result;
        switch(optionContext.type()) {
            case GLOBAL -> {
                result = Scissors.getConfiguration().resetGlobal();
                if(result == 0) throw GLOBAL_RESET_ERROR.create();
                source.sendSuccess(GLOBAL_RESET_SUCCESS.apply(result), true);
            }
            case SOURCE_GUILD -> {
                if(!(source.commandMessage().getChannel() instanceof GuildChannel guildChannel))
                    throw NOT_IN_GUILD.create();
                if(canEditPerGuild(source.user(), guildChannel.getGuild())) {
                    result = Scissors.getConfiguration().resetForGuild(source.commandMessage().getGuild());
                    if(result == 0) throw GUILD_RESET_ERROR.create();
                    source.sendSuccess(GUILD_RESET_SUCCESS.apply(result), true);
                } else throw NO_PERMS_IN_GUILD.create();
            }
            case SOURCE_CHANNEL -> {
                if(canEditPerChannel(source.user(), source.commandMessage().getChannel())) {
                    result = Scissors.getConfiguration().resetForChannel(source.commandMessage().getChannel());
                    if(result == 0) throw CHANNEL_RESET_ERROR.create();
                    source.sendSuccess(CHANNEL_RESET_SUCCESS.apply(result), true);
                } else throw NO_PERMS_IN_CHANNEL.create();
            }
            case SPECIFIC_GUILD -> {
                Guild guild = optionContext.target.right().orElseThrow().left().orElseThrow();
                result = Scissors.getConfiguration().resetForGuild(guild);
                if(result == 0) throw GUILD_ID_RESET_ERROR.create(guild.getName());
                source.sendSuccess(GUILD_ID_RESET_SUCCESS.apply(result, guild.getName()), true);
            }
            case SPECIFIC_CHANNEL -> {
                Channel channel = optionContext.target.right().orElseThrow().right().orElseThrow();
                if(canEditChannelFromOutside(source, channel)) {
                    result = Scissors.getConfiguration().resetForChannel(channel);
                    if(result == 0) throw CHANNEL_ID_RESET_ERROR.create(channel.getAsMention());
                    source.sendSuccess(CHANNEL_ID_RESET_SUCCESS.apply(result, channel.getAsMention()), true);
                } else throw NO_PERMS_IN_TARGET_CHANNEL.create(channel.getAsMention());
            }
            default -> throw INVALID_CONTEXT.create();
        }
        Scissors.saveConfiguration();
        return result;
    }

    private static int getAllOptions(ChatCommandSource source, OptionContext optionContext) throws CommandSyntaxException {
        switch(optionContext.type()) {
            case GLOBAL -> {
                StringBuilder builder = new StringBuilder("Here are the global values of all options:");
                for(Option<?> option : Options.values())
                    builder.append("\n- ").append(monospace(option.getName())).append(": ")
                        .append(bold(Scissors.getConfiguration().getGlobalOption(option).toString()));
                source.sendSuccess(builder.toString(), false);
            }
            case SOURCE_GUILD -> {
                StringBuilder builder = new StringBuilder("Here are the values of all options for this server:");
                for(Option<?> option : Options.values()) {
                    builder.append("\n- ").append(monospace(option.getName())).append(": ");
                    Optional<?> value = Scissors.getConfiguration().getOptionForGuildOnly(option, source.commandMessage().getGuild());
                    if(value.isEmpty()) builder.append("no explicit value; effective value: ")
                        .append(bold(Scissors.getConfiguration().getOptionForGuild(option, source.commandMessage().getGuild()).toString()));
                    else builder.append(bold(value.orElseThrow().toString()));
                }
                source.sendSuccess(builder.toString(), false);
            }
            case SOURCE_CHANNEL -> {
                StringBuilder builder = new StringBuilder("Here are the values of all options for this channel:");
                for(Option<?> option : Options.values()) {
                    builder.append("\n- ").append(monospace(option.getName())).append(": ");
                    Optional<?> value = Scissors.getConfiguration().getOptionForChannelOnly(option, source.commandMessage().getChannel());
                    if(value.isEmpty()) builder.append("no explicit value; effective value: ")
                        .append(bold(Scissors.getConfiguration().getOptionForChannel(option, source.commandMessage().getChannel()).toString()));
                    else builder.append(bold(value.orElseThrow().toString()));
                }
                source.sendSuccess(builder.toString(), false);
            }
            case SPECIFIC_GUILD -> {
                Guild guild = optionContext.target.right().orElseThrow().left().orElseThrow();
                StringBuilder builder = new StringBuilder("Here are the values of all options for the server \"" + guild.getName() + "\":");
                for(Option<?> option : Options.values()) {
                    builder.append("\n- ").append(monospace(option.getName())).append(": ");
                    Optional<?> value = Scissors.getConfiguration().getOptionForGuildOnly(option, guild);
                    if(value.isEmpty()) builder.append("no explicit value; effective value: ")
                        .append(bold(Scissors.getConfiguration().getOptionForGuild(option, guild).toString()));
                    else builder.append(bold(value.orElseThrow().toString()));
                }
                source.sendSuccess(builder.toString(), false);
            }
            case SPECIFIC_CHANNEL -> {
                Channel channel = optionContext.target.right().orElseThrow().right().orElseThrow();
                canSeeChannelFromOutside(source, channel);
                StringBuilder builder = new StringBuilder("Here are the values of all options for " + channel.getAsMention() + ":");
                for(Option<?> option : Options.values()) {
                    builder.append("\n- ").append(monospace(option.getName())).append(": ");
                    Optional<?> value = Scissors.getConfiguration().getOptionForChannelOnly(option, channel);
                    if(value.isEmpty()) builder.append("no explicit value; effective value: ")
                        .append(bold(Scissors.getConfiguration().getOptionForChannel(option, channel).toString()));
                    else builder.append(bold(value.orElseThrow().toString()));
                }
                source.sendSuccess(builder.toString(), false);
            }
            default -> throw INVALID_CONTEXT.create();
        }
        return 1;
    }

    private static <T> int getOptionValue(ChatCommandSource source, Option<T> option, OptionContext optionContext, boolean defaultToHigherPower) throws CommandSyntaxException {
        T value;
        switch(optionContext.type()) {
            case GLOBAL -> {
                value = Scissors.getConfiguration().getGlobalOption(option);
                source.sendSuccess(GLOBAL_GET_SUCCESS.apply(monospace(option.getName()), bold(value.toString())), false);
            }
            case SOURCE_GUILD -> {
                if(!(source.commandMessage().getChannel() instanceof GuildChannel))
                    throw NOT_IN_GUILD.create();
                if(defaultToHigherPower) {
                    value = Scissors.getConfiguration().getOptionForGuild(option, source.commandMessage().getGuild());
                    source.sendSuccess(GUILD_GET_SUCCESS.apply(monospace(option.getName()), bold(value.toString())), false);
                } else {
                    value = Scissors.getConfiguration().getOptionForGuildOnly(option, source.commandMessage().getGuild())
                        .orElseThrow(() -> GUILD_EXPLICIT_ERROR.create(monospace(option.getName())));
                    source.sendSuccess(GUILD_EXPLICIT_SUCCESS.apply(monospace(option.getName()), bold(value.toString())), false);
                }
            }
            case SOURCE_CHANNEL -> {
                if(defaultToHigherPower) {
                    value = Scissors.getConfiguration().getOptionForChannel(option, source.commandMessage().getChannel());
                    source.sendSuccess(CHANNEL_GET_SUCCESS.apply(monospace(option.getName()), bold(value.toString())), false);
                } else {
                    value = Scissors.getConfiguration().getOptionForChannelOnly(option, source.commandMessage().getChannel())
                        .orElseThrow(() -> CHANNEL_EXPLICIT_ERROR.create(monospace(option.getName())));
                    source.sendSuccess(CHANNEL_EXPLICIT_SUCCESS.apply(monospace(option.getName()), bold(value.toString())), false);
                }
            }
            case SPECIFIC_GUILD -> {
                Guild guild = optionContext.target.right().orElseThrow().left().orElseThrow();
                if(defaultToHigherPower) {
                    value = Scissors.getConfiguration().getOptionForGuild(option, guild);
                    source.sendSuccess(GUILD_ID_GET_SUCCESS.apply(monospace(option.getName()), bold(value.toString()), guild.getName()), false);
                } else {
                    value = Scissors.getConfiguration().getOptionForGuildOnly(option, guild)
                        .orElseThrow(() -> GUILD_ID_EXPLICIT_ERROR.create(monospace(option.getName()), guild.getName()));
                    source.sendSuccess(GUILD_ID_EXPLICIT_SUCCESS.apply(monospace(option.getName()), bold(value.toString()), guild.getName()), false);
                }
            }
            case SPECIFIC_CHANNEL -> {
                Channel channel = optionContext.target.right().orElseThrow().right().orElseThrow();
                canSeeChannelFromOutside(source, channel);
                if(defaultToHigherPower) {
                    value = Scissors.getConfiguration().getOptionForChannel(option, channel);
                    source.sendSuccess(CHANNEL_ID_GET_SUCCESS.apply(monospace(option.getName()), bold(value.toString()), channel.getAsMention()), false);
                } else {
                    value = Scissors.getConfiguration().getOptionForChannelOnly(option, channel)
                        .orElseThrow(() -> CHANNEL_ID_EXPLICIT_ERROR.create(monospace(option.getName()), channel.getAsMention()));
                    source.sendSuccess(CHANNEL_ID_EXPLICIT_SUCCESS.apply(monospace(option.getName()), bold(value.toString()), channel.getAsMention()), false);
                }
            }
            default -> throw INVALID_CONTEXT.create();
        }
        return getReturnValue(value);
    }

    private static <T> int setOptionValue(ChatCommandSource source, OptionValue<T> optionValue, OptionContext optionContext) throws CommandSyntaxException {
        Option<T> option = optionValue.option();
        T value = optionValue.value();
        switch(optionContext.type()) {
            case GLOBAL -> {
                boolean success = Scissors.getConfiguration().setGlobalOption(option, value);
                if(!success) throw GLOBAL_SET_ERROR.create(monospace(option.getName()), bold(value.toString()));
                source.sendSuccess(GLOBAL_SET_SUCCESS.apply(monospace(option.getName()), bold(value.toString())), true);
            }
            case SOURCE_GUILD -> {
                if(!(source.commandMessage().getChannel() instanceof GuildChannel guildChannel))
                    throw NOT_IN_GUILD.create();
                if(canEditPerGuild(source.user(), guildChannel.getGuild())) {
                    boolean success = Scissors.getConfiguration().setOptionForGuild(option, value, source.commandMessage().getGuild());
                    if(!success) throw GUILD_SET_ERROR.create(monospace(option.getName()), bold(value.toString()));
                    source.sendSuccess(GUILD_SET_SUCCESS.apply(monospace(option.getName()), bold(value.toString())), true);
                } else throw NO_PERMS_IN_GUILD.create();
            }
            case SOURCE_CHANNEL -> {
                if(canEditPerChannel(source.user(), source.commandMessage().getChannel())) {
                    boolean success = Scissors.getConfiguration().setOptionForChannel(option, value, source.commandMessage().getChannel());
                    if(!success) throw CHANNEL_SET_ERROR.create(monospace(option.getName()), bold(value.toString()));
                    source.sendSuccess(CHANNEL_SET_SUCCESS.apply(monospace(option.getName()), bold(value.toString())), true);
                } else throw NO_PERMS_IN_CHANNEL.create();
            }
            case SPECIFIC_GUILD -> {
                Guild guild = optionContext.target.right().orElseThrow().left().orElseThrow();
                boolean success = Scissors.getConfiguration().setOptionForGuild(option, value, guild);
                if(!success) throw GUILD_ID_SET_ERROR.create(monospace(option.getName()), bold(value.toString()), guild.getName());
                source.sendSuccess(GUILD_ID_SET_SUCCESS.apply(monospace(option.getName()), bold(value.toString()), guild.getName()), true);
            }
            case SPECIFIC_CHANNEL -> {
                Channel channel = optionContext.target.right().orElseThrow().right().orElseThrow();
                if(canEditChannelFromOutside(source, channel)) {
                    boolean success = Scissors.getConfiguration().setOptionForChannel(option, value, channel);
                    if(!success) throw CHANNEL_ID_SET_ERROR.create(monospace(option.getName()), bold(value.toString()), channel.getAsMention());
                    source.sendSuccess(CHANNEL_ID_SET_SUCCESS.apply(monospace(option.getName()), bold(value.toString()), channel.getAsMention()), true);
                } else throw NO_PERMS_IN_TARGET_CHANNEL.create(channel.getAsMention());
            }
            default -> throw INVALID_CONTEXT.create();
        }
        Scissors.saveConfiguration();
        return getReturnValue(value);
    }

    private static <T> int removeExplicitOptionValue(ChatCommandSource source, Option<T> option, OptionContext optionContext) throws CommandSyntaxException {
        switch(optionContext.type()) {
            case GLOBAL -> {
                boolean success = Scissors.getConfiguration().removeGlobalExplicitOption(option);
                if(!success) throw GLOBAL_EXPLICIT_REMOVE_ERROR.create(monospace(option.getName()));
                source.sendSuccess(GLOBAL_EXPLICIT_REMOVE_SUCCESS.apply(monospace(option.getName())), true);
            }
            case SOURCE_GUILD -> {
                if(!(source.commandMessage().getChannel() instanceof GuildChannel guildChannel))
                    throw NOT_IN_GUILD.create();
                if(canEditPerGuild(source.user(), guildChannel.getGuild())) {
                    boolean success = Scissors.getConfiguration().removeExplicitOptionForGuild(option, source.commandMessage().getGuild());
                    if(!success) throw GUILD_EXPLICIT_ERROR.create(monospace(option.getName()));
                    source.sendSuccess(GUILD_EXPLICIT_REMOVE_SUCCESS.apply(monospace(option.getName())), true);
                } else throw NO_PERMS_IN_GUILD.create();
            }
            case SOURCE_CHANNEL -> {
                if(canEditPerChannel(source.user(), source.commandMessage().getChannel())) {
                    boolean success = Scissors.getConfiguration().removeExplicitOptionForChannel(option, source.commandMessage().getChannel());
                    if(!success) throw CHANNEL_EXPLICIT_ERROR.create(monospace(option.getName()));
                    source.sendSuccess(CHANNEL_EXPLICIT_REMOVE_SUCCESS.apply(monospace(option.getName())), true);
                } else throw NO_PERMS_IN_CHANNEL.create();
            }
            case SPECIFIC_GUILD -> {
                Guild guild = optionContext.target.right().orElseThrow().left().orElseThrow();
                boolean success = Scissors.getConfiguration().removeExplicitOptionForGuild(option, guild);
                if(!success) throw GUILD_ID_EXPLICIT_ERROR.create(monospace(option.getName()), guild.getName());
                source.sendSuccess(GUILD_ID_EXPLICIT_REMOVE_SUCCESS.apply(monospace(option.getName()), guild.getName()), true);
            }
            case SPECIFIC_CHANNEL -> {
                Channel channel = optionContext.target.right().orElseThrow().right().orElseThrow();
                if(canEditChannelFromOutside(source, channel)) {
                    boolean success = Scissors.getConfiguration().removeExplicitOptionForChannel(option, channel);
                    if(!success) throw CHANNEL_ID_EXPLICIT_ERROR.create(monospace(option.getName()), channel.getAsMention());
                    source.sendSuccess(CHANNEL_ID_EXPLICIT_REMOVE_SUCCESS.apply(monospace(option.getName()), channel.getAsMention()), true);
                } else throw NO_PERMS_IN_TARGET_CHANNEL.create(channel.getAsMention());
            }
            default -> throw INVALID_CONTEXT.create();
        }
        Scissors.saveConfiguration();
        return 1;
    }

    private static int getReturnValue(Object value) {
        return value instanceof Number n ? (int) n :
            value instanceof Boolean b ? (b ? 1 : 0) :
            value instanceof Enum<?> e ? e.ordinal() : value.hashCode();
    }

    private static boolean canEditPerGuild(User user, Guild guild) throws CommandSyntaxException {
        long userId = user.getIdLong();
        if(userId == SharedConstants.MY_USER_ID) return true;
        Member member = guild.retrieveMemberById(userId).complete();
        if(member == null) throw IMPOSSIBLE_ERROR.create();
        return member.hasPermission(Permission.MANAGE_SERVER);
    }

    private static boolean canEditPerChannel(User user, Channel channel) throws CommandSyntaxException {
        long userId = user.getIdLong();
        if(userId == SharedConstants.MY_USER_ID) return true;
        if(!(channel instanceof GuildChannel guildChannel)) return true;
        Member member = guildChannel.getGuild().retrieveMemberById(userId).complete();
        if(member == null) throw IMPOSSIBLE_ERROR.create();
        return member.hasPermission(guildChannel, Permission.MANAGE_CHANNEL);
    }

    private static void canSeeChannelFromOutside(ChatCommandSource source, Channel channel) throws CommandSyntaxException {
        long userId = source.user().getIdLong();
        if(userId == SharedConstants.MY_USER_ID) return;
        Channel sourceChannel = source.commandMessage().getChannel();
        if(!(sourceChannel instanceof GuildChannel sourceGuildChannel)) {
            if(sourceChannel.getIdLong() == channel.getIdLong()) return;
            if(channel instanceof GuildChannel) throw CANNOT_EDIT_CHANNEL_FROM_OUTSIDE_GUILD.create();
            else throw CANNOT_EDIT_DM_FROM_OUTSIDE.create();
        }
        if(!(channel instanceof GuildChannel guildChannel)) throw CANNOT_EDIT_DM_FROM_OUTSIDE.create();
        if(sourceGuildChannel.getGuild().getIdLong() != guildChannel.getGuild().getIdLong()) throw CANNOT_EDIT_CHANNEL_FROM_OUTSIDE_GUILD.create();
    }

    private static boolean canEditChannelFromOutside(ChatCommandSource source, Channel channel) throws CommandSyntaxException {
        canSeeChannelFromOutside(source, channel);
        return canEditPerChannel(source.user(), channel);
    }

    private record OptionContext(Either<Source, Either<Guild, Channel>> target) {
        Type type() {
            if(target.left().isPresent()) return target.left().orElseThrow().getType();
            Either<Guild, Channel> either = target.right().orElseThrow();
            if(either.left().isPresent()) return Type.SPECIFIC_GUILD;
            else return Type.SPECIFIC_CHANNEL;
        }

        private enum Source {
            GLOBAL(Type.GLOBAL),
            GUILD(Type.SOURCE_GUILD),
            CHANNEL(Type.SOURCE_CHANNEL);

            private final Type type;

            Source(Type type) {
                this.type = type;
            }

            Type getType() {
                return this.type;
            }
        }

        private enum Type {
            GLOBAL,
            SOURCE_GUILD,
            SOURCE_CHANNEL,
            SPECIFIC_GUILD,
            SPECIFIC_CHANNEL
        }
    }

    @FunctionalInterface
    private interface OptionContextFunction {
        OptionContext apply(CommandContext<ChatCommandSource> context) throws CommandSyntaxException;
    }
}