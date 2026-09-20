package es.uniovi.raul.solutions50.cli;

import static java.lang.String.*;

import java.io.*;
import java.util.Optional;
import java.util.regex.Pattern;

import io.github.cdimascio.dotenv.Dotenv;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

/** Parses and validates command line arguments. */
public class ArgumentsParser {

    private static final String DEFAULT_SCHEDULE_FILE = "groups.csv";

    /**
     * Parses command line args.
     * Prints usage, version, or errors as needed.
     *
     * @param args the command line arguments
     * @return an Optional containing the parsed Arguments or empty if parsing failed
     */
    public static Optional<Arguments> parse(String[] args) {
        return parse(args, System.out, System.err);
    }

    public static Optional<Arguments> parse(String[] args, PrintStream out, PrintStream err) {

        final Arguments arguments = new Arguments();

        final CommandLine picocli = new CommandLine(arguments)
                .setCaseInsensitiveEnumValuesAllowed(true)
                // .setColorScheme(CommandLine.Help.defaultColorScheme(Help.Ansi.ON))
                .setSeparator(" "); // Use space (`-g file`) instead of "=" (`-g=file`);

        try {
            picocli.parseArgs(args);

            if (picocli.isUsageHelpRequested()) {
                picocli.usage(out);
                return Optional.empty();
            }

            if (picocli.isVersionHelpRequested()) {
                picocli.printVersionHelp(out);
                return Optional.empty();
            }

            validateSolutionRegex(arguments, picocli);

            arguments.token = ensureArgument(arguments.token, "GITHUB_TOKEN", picocli);
            arguments.organization = ensureArgument(arguments.organization, "SOLUTIONS_ORG", picocli);

            arguments.scheduleFile = resolveScheduleFile(arguments.scheduleFile);

            return Optional.of(arguments);

        } catch (ParameterException ex) {
            System.err.println(format("%n[Error] %s%n", ex.getMessage()));
            picocli.usage(err);
            return Optional.empty();
        }
    }

    private static String resolveScheduleFile(String scheduleFile) {

        // If provided, use it
        if (scheduleFile != null)
            return scheduleFile;

        // If not provided, check if the default file exists
        File file = new File(DEFAULT_SCHEDULE_FILE);
        if (file.exists() && file.isFile()) {
            System.out.printf(">> Default schedule file '%s' found.%n", DEFAULT_SCHEDULE_FILE);
            return DEFAULT_SCHEDULE_FILE;
        }

        return null; // No schedule file provided and default does not exist
    }

    private static void validateSolutionRegex(final Arguments arguments, final CommandLine picocli) {
        try {
            Pattern.compile(arguments.solutionRegex);
        } catch (Exception e) {
            throw new ParameterException(picocli, format("The provided solution regex is not valid: %s",
                    e.getMessage()));
        }
    }

    //#  -----------------------------------

    // Helper methods for environment variables
    private static String ensureArgument(String argValue, String envKey, final CommandLine picocli) {
        if (argValue != null)
            return argValue;

        return getEnvironmentVariable(envKey)
                .orElseThrow(() -> new ParameterException(picocli,
                        format("Missing required arguments: %s should be provided either via command line or in a '.env' file",
                                envKey)));
    }

    private static Optional<String> getEnvironmentVariable(String key) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String value = dotenv.get(key);
        if (value == null)
            value = System.getenv(key);
        return Optional.ofNullable(value);
    }

}
