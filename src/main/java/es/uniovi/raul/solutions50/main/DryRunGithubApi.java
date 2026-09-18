package es.uniovi.raul.solutions50.main;

import java.io.IOException;
import java.util.List;

import es.uniovi.raul.solutions50.github.*;

/**
 * A decorator for {@link GithubApi} that allows read operations but prints messages
 * instead of performing write operations (grant/revoke access).
 * This is useful for testing or previewing what changes would be made without actually
 * modifying repository permissions.
 */
public final class DryRunGithubApi implements GithubApi {

    private final GithubApi delegate;

    public DryRunGithubApi(GithubApi delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<GithubTeam> fetchTeams(String organization)
            throws GithubApiException, IOException, InterruptedException {
        return delegate.fetchTeams(organization);
    }

    @Override
    public List<String> fetchAllRepositories(String organization)
            throws GithubApiException, IOException, InterruptedException {
        return delegate.fetchAllRepositories(organization);
    }

    @Override
    public List<String> fetchRepositoriesForTeam(String organization, String teamSlug)
            throws GithubApiException, IOException, InterruptedException {
        return delegate.fetchRepositoriesForTeam(organization, teamSlug);
    }

    @Override
    public void grantAccess(String organization, String repository, String teamSlug)
            throws GithubApiException, IOException, InterruptedException {
        System.out.printf("[DRY RUN] Would grant access to team '%s' on repository '%s/%s'%n",
                teamSlug, organization, repository);
    }

    @Override
    public void revokeAccess(String organization, String repository, String teamSlug)
            throws GithubApiException, IOException, InterruptedException {
        System.out.printf("[DRY RUN] Would revoke access from team '%s' on repository '%s/%s'%n",
                teamSlug, organization, repository);
    }
}
