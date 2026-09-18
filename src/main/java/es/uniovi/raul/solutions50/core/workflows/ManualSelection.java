package es.uniovi.raul.solutions50.core.workflows;

import static es.uniovi.raul.solutions50.cli.selector.OptionsSelector.*;
import static es.uniovi.raul.solutions50.core.UserAction.ActionType.*;

import java.io.IOException;
import java.util.List;

import es.uniovi.raul.solutions50.core.UserAction;
import es.uniovi.raul.solutions50.github.*;
import es.uniovi.raul.solutions50.github.GithubApi.GithubApiException;
import es.uniovi.raul.solutions50.organization.Organization;

/**
 * Encapsulates interactive choosing logic for group and solution.
 */
public final class ManualSelection {

    private GithubApi githubApi;

    // private final Prompter prompter;

    public ManualSelection(GithubApi githubApi) {
        this.githubApi = githubApi;
    }

    public UserAction doManualSelection(Organization organization)
            throws IOException, GithubApiException, InterruptedException {

        GithubTeam chosenTeam = chooseTeam(organization);

        List<String> teamRepositories = githubApi.fetchRepositoriesForTeam(chosenTeam.organization(),
                chosenTeam.slug());

        var chosenSolution = chooseSolution(teamRepositories, organization.solutions());

        var actionType = teamRepositories.contains(chosenSolution)
                ? REVOKE_ACCESS
                : GRANT_ACCESS;

        return new UserAction(actionType, chosenTeam, chosenSolution);

        // if (chosenGroup.hasAccessTo(chosenSolution))
        //     confirmAndApply("revoke", Group::revokeAccess, chosenGroup, chosenSolution);
        // else
        //     confirmAndApply("grant", Group::grantAccess, chosenGroup, chosenSolution);
    }

    private GithubTeam chooseTeam(Organization organization) throws IOException {
        System.out.println("Choose a team:");
        int selectedGroupIndex = showOptions(organization.teams().stream().map(GithubTeam::name).toList());
        return organization.teams().get(selectedGroupIndex);
    }

    private String chooseSolution(List<String> teamRepos, List<String> allRepos)
            throws IOException {

        System.out.println("Choose the repository:");

        // One thing is the solution names ("solution1"), another is what the user sees ("solution1 [accessible]")
        var allReposSorted = allRepos.stream().sorted().toList();

        var userOptions = allReposSorted.stream()
                .map(solution -> solution + (teamRepos.contains(solution) ? " [accessible]" : " [hidden]"))
                .toList();

        int selectedSolutionIndex = showOptions(userOptions);
        return allReposSorted.get(selectedSolutionIndex);
    }

    // private void confirmAndApply(String verb, AccessAction action, Group group, String solution)
    //         throws GithubApiException, InterruptedException, IOException {

    //     var message = format("%nDo you want to %s group '%s' access to '%s'?", verb.toUpperCase(), group.name(),
    //             solution);
    //     if (prompter.confirm(message)) {
    //         action.apply(group, solution);
    //         System.out.println("Access " + verb.toLowerCase() + "ed."); // Very hacky and cutre
    //     } else
    //         System.out.println("Operation cancelled.");
    // }
}

// @FunctionalInterface
// interface AccessAction {
//     void apply(Group group, String solution)
//             throws GithubApiException, InterruptedException, IOException;
// }
