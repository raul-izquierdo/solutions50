package es.uniovi.raul.solutions50.main;

import static es.uniovi.raul.solutions50.main.Console.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import es.uniovi.raul.solutions50.cli.*;
import es.uniovi.raul.solutions50.core.Core;
import es.uniovi.raul.solutions50.github.*;
import es.uniovi.raul.solutions50.github.GithubApi.GithubApiException;
import es.uniovi.raul.solutions50.main.solutions.*;
import es.uniovi.raul.solutions50.organization.Organization;
import es.uniovi.raul.solutions50.schedule.*;
import es.uniovi.raul.solutions50.schedule.ScheduleParser.InvalidScheduleFormat;

/**
 * Entry point for the application.
 */
public class Main {
    private static final int OK = 0;
    private static final int ERROR = 1;

    public static void main(String[] args) {

        Optional<Arguments> argumentsOpt = ArgumentsParser.parse(args);
        if (argumentsOpt.isEmpty()) {
            System.exit(ERROR);
            return;
        }

        int exitCode = OK;
        try {

            exitCode = loadAndRun(argumentsOpt.get());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            printError("Operation was interrupted.");
            exitCode = ERROR;
        } catch (Exception e) {
            printError(e.getMessage());
            exitCode = ERROR;
        }
        System.exit(exitCode);
    }

    private static int loadAndRun(Arguments arguments)
            throws IOException, GithubApiException, InterruptedException, InvalidOrganizationException,
            InvalidScheduleFormat {

        // Load...
        GithubApi connection = new GithubApiImpl(arguments.token);
        if (arguments.dryRun) {
            connection = new DryRunGithubApi(connection);
            System.out.println("=== DRY RUN MODE - No changes will be made ===\n");
        }

        System.out.println("## Fetching teams and solutions... ");
        var organization = createOrganization(arguments, connection);
        System.out.println("done.\n");

        final var schedule = findSchedule(arguments.scheduleFile);
        if (!checkScheduleGroups(schedule, organization))
            System.exit(ERROR);

        // ... and run
        Core.run(organization, connection, schedule);

        return OK;
    }

    //# ------------------------------------------------------------------
    //# Setup methods invoked from loadAndRun
    //# ------------------------------------------------------------------

    private static Organization createOrganization(Arguments arguments, GithubApi connection)
            throws GithubApiException, IOException, InterruptedException, InvalidOrganizationException {

        var teams = fetchTeams(arguments.organization, connection);

        var solutionsDetector = new RegexSolutionDetector(arguments.solutionRegex);
        var solutions = fetchSolutions(arguments.organization, connection, solutionsDetector);

        // If there are no groups or solutions, there's nothing to do. Print an informative message and exit.
        if (teams.isEmpty())
            throw new InvalidOrganizationException("No teams found in the organization. Exiting.");

        if (solutions.isEmpty())
            throw new InvalidOrganizationException(
                    "No repositories match the solution naming convention. Exiting.");

        return new Organization(arguments.organization, teams, solutions);
    }

    private static List<GithubTeam> fetchTeams(String organizationName, GithubApi githubApi)
            throws GithubApiException, IOException, InterruptedException {

        List<GithubTeam> allTeams = githubApi.fetchTeams(organizationName);
        System.out.printf("%d teams found in the organization '%s'.%n", allTeams.size(), organizationName);

        return allTeams;
    }

    private static List<String> fetchSolutions(String organizationName, GithubApi githubApi,
            SolutionsDetectionStrategy solutionsDetector)
            throws GithubApiException, IOException, InterruptedException {

        var allRepos = githubApi.fetchAllRepositories(organizationName);
        System.out.printf("%d repositories found in the organization '%s'.%n", allRepos.size(), organizationName);

        var solutionRepos = allRepos.stream()
                .filter(solutionsDetector::isSolutionRepository)
                .toList();
        System.out.printf("%d repositories match the solution naming convention.%n", solutionRepos.size());

        return solutionRepos;
    }

    /**
     * Determine if a schedule file was provided and, if so, load it. If no schedule file was provided, return an empty Optional.
     *
     * @param scheduleFile the path to the schedule file. May be null, indicating that no schedule file was provided.
     * @return an Optional containing the loaded Schedule, or an empty Optional if no schedule file was provided.
     *
     */
    private static Optional<Schedule> findSchedule(String scheduleFile) throws InvalidScheduleFormat, IOException {

        if (scheduleFile == null) {
            System.out.println("No schedule file provided. Skipping schedule loading.");
            return Optional.empty();
        }

        System.out.println("\nLoading schedule from '" + scheduleFile + "'... ");

        var schedule = ScheduleParser.load(Paths.get(scheduleFile));

        if (schedule.getEntryCount() == 0)
            throw new InvalidScheduleFormat(
                    "The schedule file is empty. Add entries or delete the file to skip schedule loading.");

        return Optional.of(schedule);
    }

    /**
     * Analyze the provided schedule and print warnings for any groups that do not have a matching team in the organization.
     *
     * @param schedule the optional schedule to analyze
     * @param organization the organization containing the teams
     * @return true if the analysis was successful, false if there were issues and the user wants to exit the program
     */
    private static boolean checkScheduleGroups(Optional<Schedule> schedule, Organization organization) {

        // If the user did not provide a schedule, there's nothing to analyze, so we can return true
        if (schedule.isEmpty())
            return true;

        // Find groups in the schedule that have no matching team in the organization (case-insensitive)
        var missingTeams = schedule.get().getEntries().stream()
                .map(ScheduleEntry::group)
                .filter(teamName -> organization.teams().stream()
                        .noneMatch(team -> team.name().equalsIgnoreCase(teamName)))
                .toList();

        // No missing teams
        if (missingTeams.isEmpty())
            return true;

        // Warn about groups in the schedule that don't have a matching team in the organization
        System.out.println(
                "\n>>>> Warning!!!: The following groups are present in the schedule but there are no matching teams in the organization:");

        missingTeams.stream()
                .map(team -> " - " + team)
                .forEach(System.out::println);

        System.out.printf("The available teams in the organization are: %s%n",
                String.join(", ", organization.teams().stream().map(GithubTeam::name).toList()));

        return Console.confirmation("Do you want to continue despite the missing teams?");
    }
}
