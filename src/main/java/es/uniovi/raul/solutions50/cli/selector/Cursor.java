
package es.uniovi.raul.solutions50.cli.selector;

import static org.jline.utils.InfoCmp.Capability.*;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;

/**
 * Utility class for cursor movements and terminal line manipulations.
 *
 * Provides methods to move the cursor, clear lines, and hide/show the cursor in a terminal.
 */
public class Cursor {

    // Clears the rest of the line from the cursor position
    public static void clearRestOfLine(Terminal terminal) {
        writeAndFlush(terminal, clr_eol);
    }

    public static void printNewLine(Terminal terminal) {
        writeAndFlush(terminal, System.lineSeparator());
    }

    // Moves the cursor to the first column of the current line
    public static void goToFirstColumn(Terminal terminal) {
        writeAndFlush(terminal, carriage_return);
    }

    // Moves the cursor to the indicated column (the first column is 1)
    public static void goToColumn(Terminal terminal, int col) {
        goToFirstColumn(terminal);
        terminal.puts(column_address, col - 1);
        terminal.flush();
    }

    // Moves the cursor to the previous line
    public static void moveUp(Terminal terminal) {
        writeAndFlush(terminal, cursor_up);
    }

    public static void moveUp(Terminal terminal, int linesCount) {
        for (int counter = 0; counter < linesCount; counter++)
            moveUp(terminal);
    }

    public static void goDown(Terminal terminal) {
        terminal.puts(cursor_down);
        // terminal.flush();
    }

    public static void hide(Terminal terminal) {
        writeAndFlush(terminal, "\033[?25l");
    }

    public static void show(Terminal terminal) {
        writeAndFlush(terminal, "\033[?25h");
    }

    private static void writeAndFlush(Terminal terminal, Capability capability) {
        terminal.puts(capability);
        terminal.flush();
    }

    // Overloaded for ANSI escape codes
    private static void writeAndFlush(Terminal terminal, String ansiCode) {
        terminal.writer().print(ansiCode);
        terminal.writer().flush();
    }
}
