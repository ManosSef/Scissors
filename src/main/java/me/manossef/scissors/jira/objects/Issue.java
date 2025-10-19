package me.manossef.scissors.jira.objects;

import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;
import java.util.List;

public record Issue(String id, String key, Fields fields, String[] errorMessages) {

    public record Fields(Issuetype issuetype, Project project, Resolution resolution, String created, Priority priority,
                         Status status, String summary, String description, IssueLink[] issuelinks,
                         String customfield_10152, String customfield_10153) {

        public record Issuetype(String id, String name, String description) {
        }

        public record Project(String id, String key, String name) {
        }

        public record Resolution(String id, String name, String description) {
        }

        public record Priority(String id, String name) {
        }

        public record Status(String id, String name) {
        }

        public record IssueLink(String id, Type type, Issue inwardIssue, Issue outwardIssue) {

            public record Type(String id, String name, String inward, String outward) {
            }

        }

        public String doneInCommit() {

            return this.customfield_10152;

        }

        public String reporterUserID() {

            return this.customfield_10153;

        }

    }

    public MessageCreateData makeEmbed() {

        List<MessageEmbed.Field> embedFields = new ArrayList<>();
        embedFields.add(new MessageEmbed.Field("Issue Type", this.fields.issuetype.name, true));
        embedFields.add(new MessageEmbed.Field("Resolution", this.fields.resolution == null ? "Unresolved" : "Resolved as **" + this.fields.resolution.name + "**", true));
        if(this.fields.doneInCommit() != null) embedFields.add(new MessageEmbed.Field("Done in Commit", this.fields.doneInCommit(), true));
        return MessageCreateData.fromEmbeds(new MessageEmbed(null, "[" + this.key + "] " + this.fields.summary, "Reported by <@" + this.fields.reporterUserID() + ">", EmbedType.RICH, null, 0xDE6868, null, null, null, null, null, null, embedFields));

    }

}
