package me.manossef.scissors.listeners;

import me.manossef.scissors.Scissors;
import me.manossef.scissors.jira.objects.Issue;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecretListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        if(!(channel instanceof PrivateChannel)) return;
        User user = event.getAuthor();
        if(user.isBot() || user.isSystem()) return;
        Message message = event.getMessage();
        String text = message.getContentRaw();
        if(!text.startsWith("-p ")) return;
        Issue issue = Scissors.JIRA_API.createIssue(
            text.replaceFirst("-p ", ""),
            "Reported by " + user.getName() + " (" + user.getId() + ")\nOriginal message: " + message.getJumpUrl(),
            Issue.Fields.Issuetype.BUG,
            Issue.Fields.Project.SCIS,
            user.getId(),
            null,
            Issue.Fields.CustomFieldOption.IMPEDIMENT
        );
        if(issue.id() == null) {
            List<String> errors = new ArrayList<>();
            if(issue.errorMessages() != null) errors.addAll(Arrays.stream(issue.errorMessages()).toList());
            if(issue.errors() != null) errors.addAll(issue.errors().values());
            StringBuilder builder = new StringBuilder();
            for(Object error : errors) builder.append("\n- ").append(error);
            message.reply("The following error(s) occurred while trying to create the issue: " + builder).queue();
            return;
        }
        message.reply("Successfully created issue " + issue.key() + ". Thank you for your report!").queue();
    }
}