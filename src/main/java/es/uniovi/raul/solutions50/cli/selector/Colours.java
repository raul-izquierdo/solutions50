package es.uniovi.raul.solutions50.cli.selector;

/**
 * Utility class for terminal text colors.
 */
public class Colours {

    static final String COLOR_HIGHLIGHT = "\033[1;32m";
    static final String COLOR_RED = "\033[1;31m";

    static String addColor(String message, String color) {
        final String resetCode = "\033[0m";
        return color + message + resetCode;
    }

}
