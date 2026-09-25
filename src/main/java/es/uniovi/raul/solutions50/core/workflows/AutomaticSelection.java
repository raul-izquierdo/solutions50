package es.uniovi.raul.solutions50.core.workflows;

import static es.uniovi.raul.solutions50.core.UserAction.ActionType.*;

import java.io.IOException;
import java.time.*;
import java.util.*;

import es.uniovi.raul.solutions50.core.UserAction;
import es.uniovi.raul.solutions50.github.*;
import es.uniovi.raul.solutions50.github.GithubApi.GithubApiException;
import es.uniovi.raul.solutions50.organization.Organization;
import es.uniovi.raul.solutions50.schedule.*;

/**
 * Automatically tries to guess:
 * - The group to grant access to, based on its schedule and the current time.
 * - The next solution that the group does not have access to.
 */

public final class AutomaticSelection {
    private final Clock clock;
    private GithubApi githubApi;

    public AutomaticSelection(Clock clock, GithubApi githubApi) {
        this.clock = clock;
        this.githubApi = githubApi;
    }

    public Optional<UserAction> tryAutomaticSelection(Organization organization, Schedule schedule)
            throws GithubApiException, IOException, InterruptedException {

        var guessedTeamOpt = guessTeam(organization.teams(), schedule);
        if (guessedTeamOpt.isEmpty())
            return Optional.empty();

        var guessedSolutionOpt = guessSolution(guessedTeamOpt.get(), organization.solutions());
        if (guessedSolutionOpt.isEmpty())
            return Optional.empty();

        return Optional.of(new UserAction(GRANT_ACCESS, guessedTeamOpt.get(), guessedSolutionOpt.get()));
    }

    public Optional<GithubTeam> guessTeam(List<GithubTeam> teams, Schedule schedule) {

        Optional<ScheduleEntry> matchingEntry = schedule.findMatchingEntry(today(), currentTime());

        if (matchingEntry.isEmpty())
            return Optional.empty();

        return teams.stream()
                .filter(team -> team.name().equalsIgnoreCase(matchingEntry.get().group()))
                .findAny();
    }

    public Optional<String> guessSolution(GithubTeam team, List<String> allSolutions)
            throws GithubApiException, IOException, InterruptedException {

        List<String> teamRepositories = githubApi.fetchRepositoriesForTeam(team.organization(), team.slug());

        // Return the first solution that the team does not have access to, sorted alphabetically
        return allSolutions.stream()
                .sorted()
                .filter(solution -> !teamRepositories.contains(solution))
                .findFirst();
    }

    private String today() {
        return LocalDate.now(clock).getDayOfWeek().toString().toLowerCase();
    }

    private LocalTime currentTime() {
        return LocalTime.now(clock);
    }

}
