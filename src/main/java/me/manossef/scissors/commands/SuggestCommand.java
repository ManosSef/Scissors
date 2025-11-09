package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicNCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.arguments.UserArgument;
import me.manossef.scissors.jira.objects.Issue;
import net.dv8tion.jda.api.entities.User;

public class SuggestCommand {

    private static final DynamicNCommandExceptionType ISSUE_CREATION_FAILED = new DynamicNCommandExceptionType(errors -> {

        StringBuilder builder = new StringBuilder();
        for(Object error : errors) builder.append(error).append(", ");
        builder.delete(builder.length() - 2, builder.length());
        return new LiteralMessage("Failed to create issue: " + builder);

    });
    private static final SimpleCommandExceptionType USER_NOT_FOUND = new SimpleCommandExceptionType(new LiteralMessage("No user was found"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("suggest")
            .then(argumentsForIssueType("bug", IssueType.BUG))
            .then(argumentsForIssueType("feature", IssueType.FEATURE))
            .then(argumentsForIssueType("improvement", IssueType.IMPROVEMENT))
            .then(Commands.argument("reporter", UserArgument.user())
                .requires(Commands.devRestricted())
                .then(argumentsForIssueTypeWithUser("bug", IssueType.BUG))
                .then(argumentsForIssueTypeWithUser("feature", IssueType.FEATURE))
                .then(argumentsForIssueTypeWithUser("improvement", IssueType.IMPROVEMENT))
            )
        );

    }

    private static ArgumentBuilder<ChatCommandSource, ?> argumentsForIssueType(String literal, IssueType type) {

        return Commands.literal(literal)
            .then(Commands.argument("summary", StringArgumentType.greedyString())
                .executes(context -> createIssue(context.getSource(), type, context.getArgument("summary", String.class)))
            );

    }

    private static ArgumentBuilder<ChatCommandSource, ?> argumentsForIssueTypeWithUser(String literal, IssueType type) {

        return Commands.literal(literal)
            .then(Commands.argument("summary", StringArgumentType.greedyString())
                .executes(context -> createIssue(context.getSource(), type, context.getArgument("summary", String.class), context.getArgument("reporter", User.class)))
            );

    }

    private static int createIssue(ChatCommandSource source, IssueType type, String summary) throws CommandSyntaxException {

        return createIssue(source, type, summary, source.user());

    }

    private static int createIssue(ChatCommandSource source, IssueType type, String summary, User user) throws CommandSyntaxException {

        if(user == null) throw USER_NOT_FOUND.create();
        Issue issue = Scissors.JIRA_API.createIssue(
            summary,
            "Reported by " + user.getName() + " (" + user.getId() + ")\nOriginal message: https://discordapp.com/channels/" + source.commandMessage().getGuildId() + "/" + source.commandMessage().getChannelId() + "/" + source.commandMessage().getId(),
            Scissors.JIRA_API.getIssuetype(type.id),
            Scissors.JIRA_API.getProject(SharedConstants.PROJECT_SCIS_ID),
            user.getId()
        );
        if(issue.id() == null) throw ISSUE_CREATION_FAILED.create(null, (Object[]) issue.errorMessages());
        source.sendSuccess("Successfully created issue " + issue.key() + ". Thanks for the feedback!");
        return 1;

    }

    private enum IssueType {

        BUG(SharedConstants.ISSUETYPE_BUG_ID),
        FEATURE(SharedConstants.ISSUETYPE_FEATURE_ID),
        IMPROVEMENT(SharedConstants.ISSUETYPE_IMPROVEMENT_ID);

        private final String id;

        IssueType(String id) {

            this.id = id;

        }

    }

}
