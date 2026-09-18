package es.uniovi.raul.solutions50.organization;

import static es.uniovi.raul.solutions50.debug.Debug.*;

import java.util.List;

import es.uniovi.raul.solutions50.github.GithubTeam;

/**
 * A organization is a layer of abstraction over a GitHub organization. Instead of teams and repositories,
 * a organization shows groups and solution repositories. That is, filters teams that correspond to groups and repositories that correspond to solutions.
 *
 * @param name the name of the organization
 * @param teams all the teams in the organization (both groups and non-groups)
 * @param solutions the names of the repositories that correspond to solutions of assignments in the organization
 */
public record Organization(String name, List<GithubTeam> teams, List<String> solutions) {

    public Organization {
        notNull(teams, solutions);

        teams = List.copyOf(teams);
        solutions = List.copyOf(solutions);
    }
}
