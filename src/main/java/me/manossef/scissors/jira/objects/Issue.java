package me.manossef.scissors.jira.objects;

import me.manossef.scissors.Scissors;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.dv8tion.jda.api.utils.MarkdownUtil.bold;

public record Issue(String id, String key, Fields fields, String[] errorMessages, Map<String, String> errors) {
    public record Fields(Issuetype issuetype, Project project, Resolution resolution, Priority priority, String summary,
                         String description, IssueLink[] issuelinks, CustomFieldOption[] customfield_10021,
                         String customfield_10152, String customfield_10153) {
        public record Issuetype(String id, String name, String description) {
            public static final Issuetype BUG = Scissors.JIRA_API.getIssuetype("10009");
            public static final Issuetype FEATURE = Scissors.JIRA_API.getIssuetype("10048");
            public static final Issuetype IMPROVEMENT = Scissors.JIRA_API.getIssuetype("10049");
            public static final Issuetype TASK = Scissors.JIRA_API.getIssuetype("10147");
        }

        public record Project(String id, String key, String name) {
            public static final Project SCIS = Scissors.JIRA_API.getProject("10039");
        }

        public record Resolution(String id, String name, String description) {
        }

        public record Priority(String id, String name) {
            public static final Priority VERY_IMPORTANT = Scissors.JIRA_API.getPriority("1");
            public static final Priority IMPORTANT = Scissors.JIRA_API.getPriority("2");
            public static final Priority NORMAL = Scissors.JIRA_API.getPriority("4");
            public static final Priority LOW = Scissors.JIRA_API.getPriority("5");
        }

        public record IssueLink(String id, Type type, Issue inwardIssue, Issue outwardIssue) {
            public record Type(String id, String name, String inward, String outward) {
            }
        }

        public record CustomFieldOption(String id, String value) {
            public static final CustomFieldOption IMPEDIMENT = Scissors.JIRA_API.getCustomFieldOption("10019");
        }

        public CustomFieldOption[] flagged() {
            return this.customfield_10021;
        }

        public String doneInCommit() {
            return this.customfield_10152;
        }

        public String reporterUserID() {
            return this.customfield_10153;
        }
    }

    public MessageEmbed makeEmbed() {
        List<MessageEmbed.Field> embedFields = new ArrayList<>();
        embedFields.add(new MessageEmbed.Field("Issue Type", this.fields.issuetype.name, true));
        int color;
        Fields.Resolution resolution = this.fields.resolution;
        if(resolution == null) {
            color = 0xB3B3B3;
            embedFields.add(new MessageEmbed.Field("Resolution", "Unresolved", true));
        } else {
            color = resolution.name.equals("Done") ? 0x69DF7D : 0xDE6868;
            String name = bold(resolution.name);
            if(resolution.name.equals("Duplicate"))
                if(this.fields.issuelinks != null && this.fields.issuelinks.length > 0) {
                    List<String> parents = new ArrayList<>();
                    for(Fields.IssueLink link : this.fields.issuelinks)
                        if(link.type.name.equals("Duplicate") && link.outwardIssue != null)
                            parents.add(link.outwardIssue.key);
                    name += " of " + bold(String.join(", ", parents));
                }
            embedFields.add(new MessageEmbed.Field("Resolution", "Resolved as " + name, true));
        }
        if(this.fields.issuelinks != null && this.fields.issuelinks.length > 0) {
            List<String> duplicates = new ArrayList<>();
            for(Fields.IssueLink link : this.fields.issuelinks)
                if(link.type.name.equals("Duplicate") && link.inwardIssue != null)
                    duplicates.add(link.inwardIssue.key);
            if(!duplicates.isEmpty())
                embedFields.add(new MessageEmbed.Field("Duplicates", String.join(", ", duplicates), true));
        }
        if(this.fields.doneInCommit() != null) embedFields.add(new MessageEmbed.Field("Done in Commit", this.fields.doneInCommit(), true));
        return new MessageEmbed(null, "[" + this.key + "] " + this.fields.summary, "Reported by <@" + this.fields.reporterUserID() + ">", EmbedType.RICH, null, color, null, null, null, null, null, null, embedFields, 0);
    }
}