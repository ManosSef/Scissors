package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicNCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import kong.unirest.core.UnirestException;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;
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
            .then(argumentsForIssueType("bug", Issue.Fields.Issuetype.BUG, false))
            .then(argumentsForIssueType("feature", Issue.Fields.Issuetype.FEATURE, false))
            .then(argumentsForIssueType("improvement", Issue.Fields.Issuetype.IMPROVEMENT, false))
            .then(argumentsForIssueType("task", Issue.Fields.Issuetype.TASK, false).requires(Commands.devRestricted()))
            .then(Commands.argument("reporter", UserArgument.user())
                .requires(Commands.devRestricted())
                .then(argumentsForIssueType("bug", Issue.Fields.Issuetype.BUG, true))
                .then(argumentsForIssueType("feature", Issue.Fields.Issuetype.FEATURE, true))
                .then(argumentsForIssueType("improvement", Issue.Fields.Issuetype.IMPROVEMENT, true))
                .then(argumentsForIssueType("task", Issue.Fields.Issuetype.TASK, true))
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

    private static ArgumentBuilder<ChatCommandSource, ?> argumentsForIssueType(String literal, Issue.Fields.Issuetype type, boolean withUser) {
        return Commands.literal(literal)
            .then(argumentForPriority("vi", type, withUser, Issue.Fields.Priority.VERY_IMPORTANT))
            .then(argumentForPriority("i", type, withUser, Issue.Fields.Priority.IMPORTANT))
            .then(argumentForPriority("n", type, withUser, Issue.Fields.Priority.NORMAL))
            .then(argumentForPriority("l", type, withUser, Issue.Fields.Priority.LOW))
            .then(summaryArgument(type, withUser, null));
    }

    private static ArgumentBuilder<ChatCommandSource, ?> argumentForPriority(String literal, Issue.Fields.Issuetype type, boolean withUser, Issue.Fields.Priority priority) {
        return Commands.literal(literal)
            .requires(Commands.devRestricted())
            .then(summaryArgument(type, withUser, priority));
    }

    private static ArgumentBuilder<ChatCommandSource, ?> summaryArgument(Issue.Fields.Issuetype type, boolean withUser, Issue.Fields.Priority priority) {
        return Commands.argument("summary", StringArgumentType.greedyString())
            .executes(context -> createIssueWithContext(context, type, withUser, priority));
    }

    private static int createIssueWithContext(CommandContext<ChatCommandSource> context, Issue.Fields.Issuetype type, boolean withUser, Issue.Fields.Priority priority) throws CommandSyntaxException {
        try {
            return createIssue(context.getSource(), type, context.getArgument("summary", String.class), withUser ? context.getArgument("reporter", User.class) : context.getSource().user(), priority);
        } catch(UnirestException e) {
            throw Commands.IO_EXCEPTION.create();
        }
    }

    private static int createIssue(ChatCommandSource source, Issue.Fields.Issuetype type, String summary, User user, Issue.Fields.Priority priority) throws CommandSyntaxException {
        if(user == null) throw Commands.USER_NOT_FOUND.create();
        Issue issue = Scissors.JIRA_API.createIssue(
            summary,
            "Reported by " + user.getName() + " (" + user.getId() + ")\nOriginal message: " + source.commandMessage().getJumpUrl(),
            type,
            Issue.Fields.Project.SCIS,
            user.getId(),
            priority,
            null
        );
        if(issue.id() == null) {
            List<String> errors = new ArrayList<>();
            if(issue.errorMessages() != null) errors.addAll(Arrays.stream(issue.errorMessages()).toList());
            if(issue.errors() != null) errors.addAll(issue.errors().values());
            throw ISSUE_CREATION_FAILED.create(null, errors.toArray());
        }
        source.sendSuccess("Successfully created issue " + issue.key() + ". Thanks for the feedback!", true);
        return 1;
    }

    private static int sendIssueTypeHint() throws CommandSyntaxException {
        throw NO_ISSUE_TYPE.create();
    }
}