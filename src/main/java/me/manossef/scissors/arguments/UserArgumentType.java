package me.manossef.scissors.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.commoncode.TypeChecks;
import me.manossef.scissors.Scissors;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;
import java.util.Collection;

public class UserArgumentType implements ArgumentType<User> {
    private static final SimpleCommandExceptionType INVALID_MENTION = new SimpleCommandExceptionType(new LiteralMessage("Invalid user mention"));
    public static final SimpleCommandExceptionType USER_NOT_FOUND = new SimpleCommandExceptionType(new LiteralMessage("No user was found"));
    private static final Collection<String> EXAMPLES = Arrays.asList("611151083141857286", "<@611151083141857286>");

    public static UserArgumentType user() {
        return new UserArgumentType();
    }

    @Override
    public User parse(StringReader reader) throws CommandSyntaxException {
        String remaining = reader.getRemaining().split(" ")[0];
        if(TypeChecks.isLong(remaining)) {
            reader.setCursor(reader.getCursor() + remaining.length());
            return this.getUser(remaining);
        }
        if(remaining.startsWith("<@") && remaining.endsWith(">")) {
            String middle = remaining.substring(2, remaining.length() - 1);
            if(TypeChecks.isLong(middle)) {
                reader.setCursor(reader.getCursor() + remaining.length());
                return this.getUser(middle);
            }
            String legacyMiddle = middle.replaceFirst("!", "");
            if(middle.startsWith("!") && TypeChecks.isLong(legacyMiddle)) {
                reader.setCursor(reader.getCursor() + remaining.length());
                return this.getUser(legacyMiddle);
            }
        }
        throw INVALID_MENTION.createWithContext(reader);
    }

    private User getUser(String id) throws CommandSyntaxException {
        try {
            User user = Scissors.DISCORD_API.retrieveUserById(Long.parseLong(id)).complete();
            if(user == null) throw USER_NOT_FOUND.create();
            return user;
        } catch(RuntimeException e) {
            throw USER_NOT_FOUND.create();
        }
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}