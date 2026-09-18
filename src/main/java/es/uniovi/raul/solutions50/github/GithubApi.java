package es.uniovi.raul.solutions50.github;

import java.io.IOException;
import java.util.List;

/**
 * Interface for interacting with the GitHub API to manage teams and their members within an organization.
 */
public interface GithubApi {

    /**
     * Downloads the list of teams from the specified organization.
     *
     * @param organization Organization name
     * @return List of teams in the organization
     * @throws GithubApiException if the operation is rejected by GitHub API or response format is unexpected
     * @throws IOException if a network error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    List<GithubTeam> fetchTeams(String organization)
            throws GithubApiException, IOException, InterruptedException;

    /**
     * Downloads the list of repositories from the specified organization.
     *
     * @param organization Organization name
     * @return List of repositories in the organization
     * @throws GithubApiException if the operation is rejected by GitHub API or response format is unexpected
     * @throws IOException if a network error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    List<String> fetchAllRepositories(String organization)
            throws GithubApiException, IOException, InterruptedException;

    /**
    * Returns the list of repositories in which the team is a member.
    *
    * @param organization Organization name
    * @param teamSlug     Slug of the team
    * @return List of repositories for the team
    * @throws GithubApiException if the operation is rejected by GitHub API or response format is unexpected
    * @throws IOException if a network error occurs
    * @throws InterruptedException if the operation is interrupted
    */
    List<String> fetchRepositoriesForTeam(String organization, String teamSlug)
            throws GithubApiException, IOException, InterruptedException;

    /**
     * Adds a team to a repository in the specified organization.
     *
     * @param organization Organization name
     * @param repository   Name of the repository
     * @param teamSlug     Slug of the team to add
     * @throws GithubApiException if the operation is rejected by GitHub API or response format is unexpected
     * @throws IOException if a network error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    void grantAccess(String organization, String repository, String teamSlug)
            throws GithubApiException, IOException, InterruptedException;

    /**
     * Removes a team from a repository in the specified organization.
     *
     * @param organization Organization name
     * @param repository   Name of the repository
     * @param teamSlug     Slug of the team to remove
     * @throws GithubApiException if the operation is rejected by GitHub API or response format is unexpected
     * @throws IOException if a network error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    void revokeAccess(String organization, String repository, String teamSlug)
            throws GithubApiException, IOException, InterruptedException;

    /**
     * Base exception for all GitHub API-related errors.
     */
    class GithubApiException extends Exception {
        public GithubApiException(String message) {
            super(message);
        }

        public GithubApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
    * Exception thrown when the response format is unexpected. Probably the format has changed and this code needs to be updated.
    */
    class UnexpectedFormatException extends GithubApiException {
        public UnexpectedFormatException(String message) {
            super(message);
        }
    }

    /**
    * Exception thrown when the operation is rejected by GitHub API.
    */
    class RejectedOperationException extends GithubApiException {
        public RejectedOperationException(String message) {
            super(message);
        }
    }
}
