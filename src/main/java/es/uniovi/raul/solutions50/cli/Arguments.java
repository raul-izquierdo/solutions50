package es.uniovi.raul.solutions50.cli;

import picocli.CommandLine.*;

// CHECKSTYLE:OFF

@Command(name = "solutions", showDefaultValues = true, mixinStandardHelpOptions = true, usageHelpAutoWidth = true, description = Messages.DESCRIPTION, footer = Messages.CREDITS, versionProvider = PomVersionReader.class)
public class Arguments {

    @Option(names = "-t", description = "GitHub API access token. If not provided, it will try to read from the GITHUB_TOKEN environment variable or from a '.env' file.")
    public String token;

    @Option(names = "-s", description = "GitHub organization name where the solutions are hosted. If not provided, it will try to read from the SOLUTIONS_ORG environment variable or from a '.env' file.")
    public String organization;

    @Option(names = "-g", description = "The CSV file with the groups schedule")
    public String scheduleFile;

    @Option(names = "-e", defaultValue = ".*solution$", description = "A regular expression to identify solution repositories")
    public String solutionRegex;

    @Option(names = "--dry-run", description = "Preview what would happen without making any changes")
    public boolean dryRun;

}

class Messages {
    static final String DESCRIPTION = """

            Hides/shows repositories for a specific team of students.

            For more information, visit: https://github.com/raul-izquierdo/solutions50
            """;

    static final String CREDITS = """

            Escuela de Ingeniería
            Informática, Universidad de Oviedo.
            Raúl Izquierdo Castanedo (raul@uniovi.es)
            """;

}

class PomVersionReader implements IVersionProvider {
    public String[] getVersion() throws Exception {
        return new String[] { Arguments.class.getPackage().getImplementationVersion() };
    }
}
