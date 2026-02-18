package me.manossef.scissors.jira;

import kong.unirest.core.GetRequest;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.jira.objects.Issue;
import me.manossef.scissors.jira.objects.SearchResults;

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

    public SearchResults searchIssues(String jql, String fields) {

        JsonNode node = getRequest("search/jql")
            .queryString("jql", jql)
            .queryString("maxResults", "1000")
            .queryString("fields", fields)
            .asJson()
            .getBody();
        if(node == null) return new SearchResults(null);
        return Scissors.GSON.fromJson(node.toString(), SearchResults.class);

    }

    public Issue createIssue(String summary, String description, Issue.Fields.Issuetype issuetype, Issue.Fields.Project project, String reporterUserID) {

        JsonNode node = post("issue", Scissors.GSON.toJson(new Issue(null, null, new Issue.Fields(
            issuetype, project, null, null, null, null, summary, description, null, null, reporterUserID
        ), null)));
        return Scissors.GSON.fromJson(node.toString(), Issue.class);

    }

    private JsonNode get(String endpoint) {

        return getRequest(endpoint)
            .asJson()
            .getBody();

    }

    private GetRequest getRequest(String endpoint) {

        return Unirest.get(baseUrl + endpoint)
            .basicAuth(SharedConstants.JIRA_EMAIL, SharedConstants.JIRA_API_TOKEN)
            .header("Accept", "application/json");

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
