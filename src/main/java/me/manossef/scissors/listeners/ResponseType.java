package me.manossef.scissors.listeners;

import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.LazilyFormattedText;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.config.Configuration;
import me.manossef.scissors.config.Option;
import me.manossef.scissors.config.Options;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

public enum ResponseType {
    GPPCT(ResponseChecks::getGPPCTResponses, ResponseChecks::promptsGPPCT, Options.GPPCT_RESPONSES, Options.GPPCT_RESPONSE_CHANCE),
    PING(s -> Responses.PING_RESPONSES, ResponseChecks::promptsPing, Options.PING_RESPONSES, Options.PING_RESPONSE_CHANCE),
    MEME(s -> Responses.MEME_RESPONSES, ResponseChecks::promptsMeme, Options.GPPCT_RESPONSES),
    SCISSORS(s -> Responses.SCISSORS_RESPONSES, ResponseChecks::promptsScissors, Options.SCISSORS_RESPONSES, Options.SCISSORS_RESPONSE_CHANCE);

    private final Function<String, List<LazilyFormattedText>> responseProvider;
    private final BiPredicate<Message, Configuration> messageChecker;
    private final Option<Boolean> enabled;
    private final Option<Integer> chance;

    ResponseType(Function<String, List<LazilyFormattedText>> responseProvider, BiPredicate<Message, Configuration> messageChecker) {
        this.responseProvider = responseProvider;
        this.messageChecker = messageChecker;
        this.enabled = null;
        this.chance = null;
    }

    ResponseType(Function<String, List<LazilyFormattedText>> responseProvider, BiPredicate<Message, Configuration> messageChecker, Option<Boolean> enabled) {
        this.responseProvider = responseProvider;
        this.messageChecker = messageChecker;
        this.enabled = enabled;
        this.chance = null;
    }

    ResponseType(Function<String, List<LazilyFormattedText>> responseProvider, BiPredicate<Message, Configuration> messageChecker, Option<Boolean> enabled, Option<Integer> chance) {
        this.responseProvider = responseProvider;
        this.messageChecker = messageChecker;
        this.enabled = enabled;
        this.chance = chance;
    }

    public String getResponse(Message message) {
        List<LazilyFormattedText> responses = this.responseProvider.apply(message.getContentRaw());
        return responses.get(Scissors.RANDOM.nextInt(responses.size())).format(ChatCommandSource.of(message));
    }

    public boolean shouldRespondTo(Message message, Configuration config) {
        if(this.enabled == null) return true;
        Channel channel;
        return this.messageChecker.test(message, config)
            && config.getOptionForChannel(this.enabled, (channel = message.getChannel()))
            && (this.chance == null || Scissors.RANDOM.nextInt(100) < config.getOptionForChannel(this.chance, channel));
    }
}