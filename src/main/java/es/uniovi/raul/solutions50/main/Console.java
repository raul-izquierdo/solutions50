package es.uniovi.raul.solutions50.main;

import static java.lang.String.*;

import java.util.Scanner;

/**
 * Utility class for console input and output operations.
  */
public final class Console {

    public static void printError(String message) {
        System.err.println(format("%n[Error] %s%n", message));
    }

    public static void printWarning(String message) {
        System.out.println(format("%n[Warning] %s%n", message));
    }

    /**
     * Prompts the user for confirmation with a yes/no question.
     *
     * @param message the confirmation message to display
     * @return true if the user confirms with 'y' or 'Y', false otherwise
     */
    public static boolean confirmation(String message) {
        System.out.print(format("%s (y/N): ", message));
        @SuppressWarnings("resource")
        String response = new Scanner(System.in).nextLine().trim().toLowerCase();
        // Don't close the scanner, because showOptions will fail (System.in will be closed)
        return "y".equals(response);
    }

    public static boolean confirmation(String message, Object... args) {
        return confirmation(String.format(message, args));
    }
}
