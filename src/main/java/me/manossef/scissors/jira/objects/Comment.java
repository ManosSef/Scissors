package me.manossef.scissors.jira.objects;

import java.util.Map;

public record Comment(String body, String[] errorMessages, Map<String, String> errors) {
}