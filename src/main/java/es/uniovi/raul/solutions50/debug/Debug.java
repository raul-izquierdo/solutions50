package es.uniovi.raul.solutions50.debug;

/**
 * An utility class with precondition checks.
 */
public final class Debug {

    public static void notNull(Object obj, String paramName) {
        if (obj == null)
            throw new IllegalArgumentException("Argument '" + paramName + "' cannot be null");
    }

    public static void notNull(String text, String paramName) {
        if (text == null || text.isBlank())
            throw new IllegalArgumentException("Argument '" + paramName + "' (string) cannot be null or empty");
    }

    public static void notNull(Object... arguments) {

        if (arguments == null || arguments.length == 0)
            throw new IllegalArgumentException("At least one argument must be provided");

        for (int i = 0; i < arguments.length; i++) {

            Object argument = arguments[i];

            if (argument == null)
                throw new IllegalArgumentException("Argument #" + (i + 1) + " cannot be null");

            if (argument instanceof String text && text.isBlank())
                throw new IllegalArgumentException("Argument #" + (i + 1) + " cannot be an empty string");
        }
    }

}
