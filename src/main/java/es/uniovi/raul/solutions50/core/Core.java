package es.uniovi.raul.solutions50.core;

import static java.time.Clock.*;

import java.io.IOException;
import java.util.Optional;

import es.uniovi.raul.solutions50.core.workflows.*;
import es.uniovi.raul.solutions50.github.GithubApi;
import es.uniovi.raul.solutions50.github.GithubApi.GithubApiException;
import es.uniovi.raul.solutions50.main.Console;
import es.uniovi.raul.solutions50.organization.Organization;
import es.uniovi.raul.solutions50.schedule.Schedule;

public class Core {

    /**
     * Runs the core logic of the application, which includes:
     * 1. Attempting to automatically select a team and solution based on the schedule.
     * 2. If automatic selection fails, prompting the user for manual selection.
     * 3. Confirming the action with the user before executing it.
     *
     * @param organization the organization containing teams and solutions
     * @param githubApi the GitHub API interface for performing actions
     * @param schedule an optional schedule for automatic selection. If empty, only manual selection will be available.
     */
    public static void run(Organization organization, GithubApi githubApi, Optional<Schedule> schedule)
            throws IOException, GithubApiException, InterruptedException {

        boolean automaticSelectionSucceeded = tryAutomaticSelection(organization, githubApi, schedule);
        if (automaticSelectionSucceeded)
            return;

        var manualSelection = new ManualSelection(githubApi);
        var action = manualSelection.doManualSelection(organization);

        boolean userConfirmed = confirmAction(action);
        if (!userConfirmed)
            return;

        executeAction(action, githubApi);
    }

    private static boolean tryAutomaticSelection(Organization organization, GithubApi githubApi,
            Optional<Schedule> schedule) throws GithubApiException, IOException, InterruptedException {

        if (schedule.isEmpty()) {
            System.out.println("No schedule provided, skipping automatic selection.");
            return false;
        }

        var automaticWorkflow = new AutomaticSelection(systemDefaultZone(), githubApi);
        var automaticAction = automaticWorkflow.tryAutomaticSelection(organization, schedule.get());

        if (automaticAction.isEmpty()) {
            System.out.println("Automatic selection can't guess the correct action, falling back to manual selection.");
            return false;
        }

        boolean userConfirmed = confirmAction(automaticAction.get());
        if (!userConfirmed)
            return false;

        executeAction(automaticAction.get(), githubApi);

        return true;
    }

    private static boolean confirmAction(UserAction action) {
        String actionVerb = switch (action.type()) {
            case GRANT_ACCESS -> "GRANT";
            case REVOKE_ACCESS -> "REVOKE";
        };

        return Console.confirmation("Are you sure you want to %s access to '%s' for team '%s'?",
                actionVerb, action.repository(), action.team().name());
    }

    private static void executeAction(UserAction action, GithubApi githubApi)
            throws GithubApiException, IOException, InterruptedException {

        switch (action.type()) {
            case GRANT_ACCESS ->
                githubApi.grantAccess(action.team().organization(), action.team().slug(), action.repository());
            case REVOKE_ACCESS ->
                githubApi.revokeAccess(action.team().organization(), action.team().slug(), action.repository());
            default -> throw new IllegalArgumentException("Unknown action type: " + action.type());
        }
        System.out.printf("Action '%s' executed successfully for team '%s' on repository '%s'.%n",
                action.type(), action.team().name(), action.repository());
    }

}
