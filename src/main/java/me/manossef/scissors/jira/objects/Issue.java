package me.manossef.scissors.jira.objects;

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

}
