package es.uniovi.raul.solutions50.github;

import static java.net.http.HttpRequest.BodyPublishers.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.Builder;
import java.util.*;

import com.fasterxml.jackson.databind.*;

/**
 * Github API implementation.
 */
public final class GithubApiImpl implements GithubApi {

    // HTTP Status Codes
    private static final int HTTP_OK = 200;
    private static final int HTTP_NO_CONTENT = 204;

    // Error message literals
    private static final String ORGANIZATION_PREFIX = "Organization '";
    private static final String TEAM_PREFIX = "Team '";
    private static final String REPOSITORY_PREFIX = "Repository '";
    private static final String QUOTE_SUFFIX = "'";

    private final String token;
    private final HttpClient client;
    private final ObjectMapper mapper;

    public GithubApiImpl(String token) {
        if (token == null || token.isBlank())
            throw new IllegalArgumentException("Token cannot be null or blank.");
        this.token = token;
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    @Override
    public List<GithubTeam> fetchTeams(String organization)
            throws GithubApiException, IOException, InterruptedException {

        List<GithubTeam> teams = new ArrayList<>();
        String url = "https://api.github.com/orgs/" + organization + "/teams";
        HttpRequest request = createHttpRequestBuilder(url).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HTTP_OK)
            throw new RejectedOperationException(buildErrorMessage(response.statusCode(),
                    "fetch teams", ORGANIZATION_PREFIX + organization + QUOTE_SUFFIX, response.body()));

        JsonNode root = mapper.readTree(response.body());
        if (!root.isArray())
            throw new UnexpectedFormatException("Expected a JSON array for teams, got: " + root.getNodeType());

        for (JsonNode node : root) {
            JsonNode nameNode = node.get("name");
            JsonNode slugNode = node.get("slug");
            if (nameNode == null || !nameNode.isTextual() || slugNode == null || !slugNode.isTextual())
                throw new UnexpectedFormatException(
                        "Expected 'name' and 'slug' fields of type string in each team object, got: "
                                + node.toString());
            teams.add(new GithubTeam(organization, nameNode.asText(), slugNode.asText()));
        }
        return teams;
    }

    @Override
    public List<String> fetchAllRepositories(String organization)
            throws GithubApiException, IOException, InterruptedException {

        List<String> repositories = new ArrayList<>();
        String url = String.format("https://api.github.com/orgs/%s/repos?per_page=100", organization);

        while (url != null) {
            HttpRequest request = createHttpRequestBuilder(url).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HTTP_OK)
                throw new RejectedOperationException(buildErrorMessage(response.statusCode(),
                        "fetch repositories", ORGANIZATION_PREFIX + organization + QUOTE_SUFFIX, response.body()));

            JsonNode root = mapper.readTree(response.body());
            if (!root.isArray())
                throw new UnexpectedFormatException(
                        "Expected a JSON array for repositories, got: " + root.getNodeType());

            for (JsonNode node : root) {
                JsonNode nameNode = node.get("name");
                if (nameNode == null || !nameNode.isTextual())
                    throw new UnexpectedFormatException(
                            "Expected 'name' field of type string in each repository object, got: " + node.toString());
                repositories.add(nameNode.asText());
            }

            url = getNextPageUrl(response);
        }
        return repositories;
    }

    @Override
    public List<String> fetchRepositoriesForTeam(String organization, String teamSlug)
            throws GithubApiException, IOException, InterruptedException {

        List<String> repositories = new ArrayList<>();
        String url = String.format("https://api.github.com/orgs/%s/teams/%s/repos?per_page=100", organization,
                teamSlug);

        while (url != null) {
            HttpRequest request = createHttpRequestBuilder(url).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HTTP_OK)
                throw new RejectedOperationException(buildErrorMessage(response.statusCode(),
                        "fetch repositories for team '" + teamSlug + "'",
                        TEAM_PREFIX + teamSlug + "' or " + ORGANIZATION_PREFIX.toLowerCase() + organization
                                + QUOTE_SUFFIX,
                        response.body()));

            JsonNode root = mapper.readTree(response.body());
            if (!root.isArray())
                throw new UnexpectedFormatException(
                        "Expected a JSON array for the team's repositories, got: " + root.getNodeType());

            for (JsonNode node : root) {
                JsonNode fullNameNode = node.get("full_name");
                if (fullNameNode == null || !fullNameNode.isTextual())
                    throw new UnexpectedFormatException(
                            "Expected 'full_name' field of type string in each repository object, got: "
                                    + node.toString());
                repositories.add(fullNameNode.asText());
            }

            url = getNextPageUrl(response);
        }
        return repositories;
    }

    @Override
    public void grantAccess(String organization, String repository, String teamSlug)
            throws GithubApiException, IOException, InterruptedException {

        String url = String.format("https://api.github.com/orgs/%s/teams/%s/repos/%s/%s",
                organization, teamSlug, organization, repository);
        HttpRequest request = createHttpRequestBuilder(url)
                .header("Content-Type", "application/json")
                .PUT(ofString("{\"permission\":\"pull\"}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HTTP_NO_CONTENT)
            throw new RejectedOperationException(buildErrorMessage(response.statusCode(),
                    "grant access to repository '" + repository + "' for team '" + teamSlug + "'",
                    ORGANIZATION_PREFIX + organization + "', " + TEAM_PREFIX.toLowerCase() + teamSlug
                            + "', or " + REPOSITORY_PREFIX.toLowerCase() + repository + QUOTE_SUFFIX,
                    response.body()));
    }

    @Override
    public void revokeAccess(String organization, String repository, String teamSlug)
            throws GithubApiException, IOException, InterruptedException {

        String url = String.format("https://api.github.com/orgs/%s/teams/%s/repos/%s/%s",
                organization, teamSlug, organization, repository);
        HttpRequest request = createHttpRequestBuilder(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HTTP_NO_CONTENT)
            throw new RejectedOperationException(buildErrorMessage(response.statusCode(),
                    "revoke access to repository '" + repository + "' for team '" + teamSlug + "'",
                    ORGANIZATION_PREFIX + organization + "', " + TEAM_PREFIX.toLowerCase() + teamSlug
                            + "', or " + REPOSITORY_PREFIX.toLowerCase() + repository + QUOTE_SUFFIX,
                    response.body()));
    }

    //# Auxiliary methods -----------------------------------

    private Builder createHttpRequestBuilder(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json");
    }

    Optional<String> parseGithubErrorMessage(String responseBody) {
        try {
            JsonNode root = mapper.readTree(responseBody);
            JsonNode messageNode = root.get("message");
            if (messageNode != null && messageNode.isTextual())
                return Optional.of(messageNode.asText());
        } catch (Exception e) {
            // Ignore parsing errors
        }
        return Optional.empty();
    }

    String buildErrorMessage(int statusCode, String action, String resourceInfo, String responseBody) {
        String githubMessage = parseGithubErrorMessage(responseBody).orElse("");
        String details = githubMessage.isEmpty() ? "" : " GitHub says: " + githubMessage;

        return switch (statusCode) {
            case 401 -> String.format("Authentication failed. Please check your access token.%s", details);
            case 403 -> String.format("Access forbidden. You don't have permission to %s.%s", action, details);
            case 404 -> String.format("%s does not exist.%s", resourceInfo, details);
            case 422 -> String.format("Validation failed for %s.%s", action, details);
            default -> String.format("Failed to %s. Status: %d.%s", action, statusCode, details);
        };
    }

    private String getNextPageUrl(HttpResponse<String> response) {
        String link = response.headers().firstValue("Link").orElse("");
        if (link.isEmpty())
            return null;

        String[] links = link.split(",");
        for (String linkPart : links) {
            if (linkPart.contains("rel=\"next\"")) {
                int start = linkPart.indexOf('<') + 1;
                int end = linkPart.indexOf('>');
                if (start > 0 && end > start)
                    return linkPart.substring(start, end);
            }
        }
        return null;
    }

}
