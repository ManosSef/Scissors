package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicNCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import kong.unirest.core.UnirestException;
import me.manossef.scissors.*;
import me.manossef.scissors.arguments.UserArgument;
import me.manossef.scissors.jira.objects.Issue;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class SuggestCommand {
    private static final DynamicNCommandExceptionType ISSUE_CREATION_FAILED = new DynamicNCommandExceptionType(errors -> {
        StringBuilder builder = new StringBuilder();
        for(Object error : errors) builder.append("\n- ").append(error);
        return new LiteralMessage("The following error(s) occurred while trying to create the issue: " + builder);
    });
    private static final SimpleCommandExceptionType NO_ISSUE_TYPE = new SimpleCommandExceptionType(new LiteralMessage(
        "Please add " + monospace("bug") + ", " + monospace("feature") + " or " + monospace("improvement") + " after " + Commands.format("suggest") + " depending on what you're suggesting"
    ));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "suggest";
        dispatcher.register(Commands.literal(baseLiteral)
            .executes(context -> sendIssueTypeHint())
            .then(argumentsForIssueType("bug", IssueType.BUG))
            .then(argumentsForIssueType("feature", IssueType.FEATURE))
            .then(argumentsForIssueType("improvement", IssueType.IMPROVEMENT))
            .then(argumentsForIssueType("task", IssueType.TASK).requires(Commands.devRestricted()))
            .then(Commands.argument("reporter", UserArgument.user())
                .requires(Commands.devRestricted())
                .then(argumentsForIssueTypeWithUser("bug", IssueType.BUG))
                .then(argumentsForIssueTypeWithUser("feature", IssueType.FEATURE))
                .then(argumentsForIssueTypeWithUser("improvement", IssueType.IMPROVEMENT))
                .then(argumentsForIssueTypeWithUser("task", IssueType.TASK))
            )
            .then(Commands.argument("summary", StringArgumentType.greedyString())
                .executes(context -> sendIssueTypeHint())
            )
        );
        HelpCommand.addLine(baseLiteral, "Posts a suggestion for the bot.");
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Posts a suggestion or bug report for the bot. A work item with the provided summary is created in the bot's internal Jira instance for %s to eventually take a look at.
                
                Here are the available syntaxes for this command:
                - %s: Creates a work item with the "Bug" issue type. This should be used to report bugs.
                - %s: Creates a work item with the "New Feature" issue type. This should be used to suggest new features to be added to the bot.
                - %s: Creates a work item with the "Improvement" issue type. This should be used to suggest improvements to the bot's existing features.
                
                Fails if the provided summary is longer than 255 characters, or if anything else goes wrong while trying to submit the work item.""",
            SharedConstants.MY_MENTION,
            Commands.format(baseLiteral + " bug <summary>"),
            Commands.format(baseLiteral + " feature <summary>"),
            Commands.format(baseLiteral + " improvement <summary>")));
    }

    private static ArgumentBuilder<ChatCommandSource, ?> argumentsForIssueType(String literal, IssueType type) {
        return Commands.literal(literal)
            .then(Commands.argument("summary", StringArgumentType.greedyString())
                .executes(context -> {
                    try {
                        return createIssue(context.getSource(), type, context.getArgument("summary", String.class));
                    } catch(UnirestException e) {
                        throw Commands.IO_EXCEPTION.create();
                    }
                })
            );
    }

    private static ArgumentBuilder<ChatCommandSource, ?> argumentsForIssueTypeWithUser(String literal, IssueType type) {
        return Commands.literal(literal)
            .then(Commands.argument("summary", StringArgumentType.greedyString())
                .executes(context -> {
                    try {
                        return createIssue(context.getSource(), type, context.getArgument("summary", String.class), context.getArgument("reporter", User.class));
                    } catch(UnirestException e) {
                        throw Commands.IO_EXCEPTION.create();
                    }
                })
            );
    }

    private static int createIssue(ChatCommandSource source, IssueType type, String summary) throws CommandSyntaxException {
        return createIssue(source, type, summary, source.user());
    }

    private static int createIssue(ChatCommandSource source, IssueType type, String summary, User user) throws CommandSyntaxException {
        if(user == null) throw Commands.USER_NOT_FOUND.create();
        Issue issue = Scissors.JIRA_API.createIssue(
            summary,
            "Reported by " + user.getName() + " (" + user.getId() + ")\nOriginal message: " + Util.getMessageLink(source.commandMessage()),
            Scissors.JIRA_API.getIssuetype(type.id),
            Scissors.JIRA_API.getProject(SharedConstants.PROJECT_SCIS_ID),
            user.getId()
        );
        if(issue.id() == null) {
            List<String> errors = new ArrayList<>();
            if(issue.errorMessages() != null) errors.addAll(Arrays.stream(issue.errorMessages()).toList());
            if(issue.errors() != null) errors.addAll(issue.errors().values());
            throw ISSUE_CREATION_FAILED.create(null, errors.toArray());
        }
        source.sendSuccess("Successfully created issue " + issue.key() + ". Thanks for the feedback!");
        return 1;
    }

    private static int sendIssueTypeHint() throws CommandSyntaxException {
        throw NO_ISSUE_TYPE.create();
    }

    private enum IssueType {
        BUG(SharedConstants.ISSUETYPE_BUG_ID),
        FEATURE(SharedConstants.ISSUETYPE_FEATURE_ID),
        IMPROVEMENT(SharedConstants.ISSUETYPE_IMPROVEMENT_ID),
        TASK(SharedConstants.ISSUETYPE_TASK_ID);

        private final String id;

        IssueType(String id) {
            this.id = id;
        }
    }
}