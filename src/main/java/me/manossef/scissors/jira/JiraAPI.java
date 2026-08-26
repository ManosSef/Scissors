package me.manossef.scissors.jira;

import me.manossef.scissors.Scissors;
import me.manossef.scissors.jira.objects.Issue;
import me.manossef.scissors.jira.objects.SearchResults;
import okhttp3.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;

public record JiraAPI(String baseUrl) {
    private static final String JIRA_EMAIL = System.getenv("JIRA_ALT_EMAIL");
    private static final String JIRA_API_TOKEN = System.getenv("JIRA_ALT_API_TOKEN");

    public Issue getIssue(String key) {
        return Scissors.GSON.fromJson(get(HttpUrl.get(this.baseUrl + "issue/" + key)), Issue.class);
    }

    public Issue.Fields.Issuetype getIssuetype(String id) {
        return Scissors.GSON.fromJson(get(HttpUrl.get(this.baseUrl + "issuetype/" + id)), Issue.Fields.Issuetype.class);
    }

    public Issue.Fields.Project getProject(String id) {
        return Scissors.GSON.fromJson(get(HttpUrl.get(this.baseUrl + "project/" + id)), Issue.Fields.Project.class);
    }

    public Issue.Fields.Priority getPriority(String id) {
        return Scissors.GSON.fromJson(get(HttpUrl.get(this.baseUrl + "priority/" + id)), Issue.Fields.Priority.class);
    }

    public Issue.Fields.CustomFieldOption getCustomFieldOption(String id) {
        return Scissors.GSON.fromJson(get(HttpUrl.get(this.baseUrl + "customFieldOption/" + id)), Issue.Fields.CustomFieldOption.class);
    }

    public SearchResults searchIssues(String jql, String fields) {
        HttpUrl url = HttpUrl.get(this.baseUrl + "search/jql").newBuilder()
            .addQueryParameter("jql", jql)
            .addQueryParameter("maxResults", "1000")
            .addQueryParameter("fields", fields)
            .build();
        return Scissors.GSON.fromJson(get(url), SearchResults.class);
    }

    public Issue createIssue(String summary, String description, Issue.Fields.Issuetype issuetype, Issue.Fields.Project project, String reporterUserID, Issue.Fields.Priority priority, Issue.Fields.CustomFieldOption flagged) {
        String node = post(HttpUrl.get(this.baseUrl + "issue"), Scissors.GSON.toJson(new Issue(null, null, new Issue.Fields(
            issuetype, project, null, priority, summary, description,
            flagged == null ? null : new Issue.Fields.CustomFieldOption[]{flagged}, null, reporterUserID
        ), null, null)));
        return Scissors.GSON.fromJson(node, Issue.class);
    }

    private String get(HttpUrl endpoint) {
        Request request = new Request.Builder()
            .url(endpoint)
            .header("Authorization", this.getAuthHeader())
            .build();
        try(Response response = Scissors.HTTP_CLIENT.newCall(request).execute()) {
            if(!response.isSuccessful()) return null;
            return response.body().string();
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String post(HttpUrl endpoint, String body) {
        Request request = new Request.Builder()
            .url(endpoint)
            .post(RequestBody.create(body, MediaType.get("application/json")))
            .header("Authorization", this.getAuthHeader())
            .header("Accept", "application/json")
            .build();
        try(Response response = Scissors.HTTP_CLIENT.newCall(request).execute()) {
            if(!response.isSuccessful()) return null;
            return response.body().string();
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String getAuthHeader() {
        return "Basic " + new String(Base64.getEncoder().encode((JIRA_EMAIL + ":" + JIRA_API_TOKEN).getBytes()));
    }
}