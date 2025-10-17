package me.manossef.scissors.jira;

import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.jira.objects.Issue;

public record JiraAPI(String baseUrl) {

    public Issue getIssue(String key) {

        JsonNode node = get("issue/" + key);
        return Scissors.GSON.fromJson(node.toString(), Issue.class);

    }

    public Issue.Fields.Issuetype getIssuetype(String id) {

        JsonNode node = get("issuetype/" + id);
        return Scissors.GSON.fromJson(node.toString(), Issue.Fields.Issuetype.class);

    }

    public Issue.Fields.Project getProject(String id) {

        JsonNode node = get("project/" + id);
        return Scissors.GSON.fromJson(node.toString(), Issue.Fields.Project.class);

    }

    public Issue createIssue(String summary, String description, Issue.Fields.Issuetype issuetype, Issue.Fields.Project project, String reporterUserID) {

        JsonNode node = post("issue", Scissors.GSON.toJson(new Issue(null, null, new Issue.Fields(
            issuetype, project, null, null, null, null, summary, description, null, null, reporterUserID
        ), null)));
        return Scissors.GSON.fromJson(node.toString(), Issue.class);

    }

    private JsonNode get(String endpoint) {

        return Unirest.get(baseUrl + endpoint)
            .basicAuth(SharedConstants.JIRA_EMAIL, SharedConstants.JIRA_API_TOKEN)
            .header("Accept", "application/json")
            .asJson()
            .getBody();

    }

    private JsonNode post(String endpoint, String body) {

        return Unirest.post(baseUrl + endpoint)
            .basicAuth(SharedConstants.JIRA_EMAIL, SharedConstants.JIRA_API_TOKEN)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .body(body)
            .asJson()
            .getBody();

    }

}
