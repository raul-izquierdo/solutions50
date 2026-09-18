package es.uniovi.raul.solutions50.cli.selector;

import static es.uniovi.raul.solutions50.cli.selector.Colours.*;

import java.io.IOException;
import java.util.*;

import org.jline.terminal.*;

/**
 * Utility class for displaying and selecting options in the terminal with filtering and navigation support.
 * Provides a static method to show a list of options, allowing the user to filter by typing and navigate using arrow keys.
 *
 * @author Raúl Izquierdo (raul@uniovi.es)
 */
public class OptionsSelector {
    private static final String PROMPT = "(type to filter or use arrows ↑/↓): ";

    private static final int KEY_ENTER = 10;
    private static final int KEY_RETURN = 13;
    private static final int KEY_ESCAPE = 27;
    private static final int KEY_BACKSPACE = 8;
    private static final int ARROW_UP = 'A';
    private static final int ARROW_DOWN = 'B';

    /**
     * Displays a list of options in the terminal, allowing the user to filter and select one.
     * <p>
     * The user can type to filter the options or use the arrow keys to navigate. The method returns
     * the index of the selected option.
     *
     * @param options the list of options to display and select from
     * @return the index of the selected option
     * @throws IOException if an I/O error occurs with the terminal
     */
    public static int showOptions(List<String> options) throws IOException {
        if (options == null || options.isEmpty())
            throw new IllegalArgumentException("Options list cannot be null or empty.");
        if (options.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("Options list cannot contain null values.");

        try (Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .streams(System.in, System.out)
                .build()) {

            OptionsArea optionsArea = new OptionsArea(terminal, options);
            terminal.enterRawMode();
            terminal.writer().flush();

            handleKeys(terminal, optionsArea);

            // Final draw to show selection
            finalDraw(terminal, optionsArea);

            return optionsArea.getSelectedIndex();

        } catch (IOException e) {
            System.err.println("Error with JLine terminal: " + e.getMessage());
            throw e;
        }
    }

    private static void handleKeys(Terminal terminal, OptionsArea optionsArea) throws IOException {

        while (true) {
            // Use draw to print prompt and options, and leave cursor at end of filter
            draw(terminal, optionsArea);

            int ch = terminal.reader().read();

            if (ch == KEY_ENTER || ch == KEY_RETURN) {
                if (optionsArea.hasFilteredOptions())
                    return;

            } else if (ch == KEY_ESCAPE) {
                terminal.reader().read(); // The second read is required to consume an unused character
                int arrow = terminal.reader().read(); // The third read is for the arrow key
                if (arrow == ARROW_UP)
                    optionsArea.decreaseSelectedIndex();
                else if (arrow == ARROW_DOWN)
                    optionsArea.increaseSelectedIndex();

            } else if (ch == KEY_BACKSPACE)
                optionsArea.removeLastCharFromFilter();

            else if (Character.isLetterOrDigit(ch) || Character.isWhitespace(ch) || ch >= 128)
                optionsArea.addToFilter((char) ch);

        }
    }

    // Expects the cursor to be at the prompt line and leaves it there
    private static void draw(Terminal terminal, OptionsArea optionsArea) {

        Cursor.hide(terminal); // Hide the cursor to avoid flickering

        // Estamos en la fila del prompt, así que hay que bajar para ponerse en la línea de la primera opción
        Cursor.goDown(terminal);
        optionsArea.draw(); // Al salir, el cursor seguirá en la línea de la primera opción

        Cursor.moveUp(terminal); // Volvemos a la línea del prompt
        terminal.writer().print(PROMPT + addColor(optionsArea.getFilter(), Colours.COLOR_HIGHLIGHT));
        Cursor.clearRestOfLine(terminal);

        Cursor.show(terminal);
    }

    // Borra el prompt y las opciones, dejando sólo impresa la opción seleccionada
    private static void finalDraw(Terminal terminal, OptionsArea optionsArea) {

        // Overwrite the prompt line with the selected option
        Cursor.goToFirstColumn(terminal); // Move cursor to the start of the prompt line
        terminal.writer().print("> " + addColor(optionsArea.getSelectedOption(), Colours.COLOR_HIGHLIGHT));
        Cursor.clearRestOfLine(terminal); // Delete the prompt line

        // Delete options area
        Cursor.printNewLine(terminal); // Go to the first option line
        optionsArea.clearAllOptions();

    }

}
