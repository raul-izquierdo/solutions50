package es.uniovi.raul.solutions50.cli.selector;

import java.text.Normalizer;
import java.util.*;

import org.jline.terminal.Terminal;

/**
 * Manages the display and interaction with a list of options in a terminal.
 * Allows filtering, selecting, and drawing options.
 *
 * Changing a property does not automatically update the display; call draw() to refresh.
 *
 * Cursor-related methods expect the cursor at the line for the first option. At exit, the cursor will be left at the same line.
 */
class OptionsArea {
    private final Terminal terminal;
    private final List<String> options;

    private int selectedIndex = 0;
    private String filter = "";
    private int linesPrintedLastTime = 0;

    OptionsArea(Terminal terminal, List<String> options) {
        if (terminal == null)
            throw new IllegalArgumentException("Terminal cannot be null.");

        if (options == null || options.isEmpty())
            throw new IllegalArgumentException("Options cannot be null or empty.");

        if (options.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("Options cannot contain null values.");

        this.terminal = terminal;
        this.options = new ArrayList<>(options);
    }

    void setFilter(String newFilter) {
        if (newFilter == null)
            throw new IllegalArgumentException("Filter cannot be null");

        if (!newFilter.equals(this.filter)) {
            this.filter = newFilter;
            selectedIndex = getFirstFilteredIndex();
        }
    }

    private int getFirstFilteredIndex() {
        for (int i = 0; i < options.size(); i++)
            if (matchesFilter(options.get(i)))
                return i;
        return 0;
    }

    String getFilter() {
        return filter;
    }

    void addToFilter(char character) {
        setFilter(filter + character);
    }

    void removeLastCharFromFilter() {
        if (!filter.isEmpty())
            setFilter(filter.substring(0, filter.length() - 1));
    }

    // Expects the cursor to be at line for the first option and leaves it there
    void draw() {
        Cursor.goToFirstColumn(terminal);

        int linesJustPrinted = hasFilteredOptions()
                ? printFilteredOptions()
                : printNoOptions();

        clearExcessLines(linesJustPrinted, linesPrintedLastTime);

        // Move cursor up to the original line
        int totalLines = linesJustPrinted + Math.max(0, linesPrintedLastTime - linesJustPrinted);
        Cursor.moveUp(terminal, totalLines);

        linesPrintedLastTime = linesJustPrinted;
    }

    private int printFilteredOptions() {
        int linesJustPrinted = 0;
        for (int i = 0; i < options.size(); i++) {
            String option = options.get(i);
            if (matchesFilter(option)) {
                String display = highlightMatchingText(option, filter);
                String prefix = (i == selectedIndex ? "> " : "  ");
                printFullLine(prefix + display);
                linesJustPrinted++;
            }
        }
        return linesJustPrinted;
    }

    private int printNoOptions() {
        printFullLine(Colours.addColor("No options match your filter", Colours.COLOR_RED));
        return 1;
    }

    // Delete the lines printed last time that have not been overwritten
    private void clearExcessLines(int linesJustPrinted, int linesPrintedLastTime) {
        if (linesJustPrinted < linesPrintedLastTime)
            for (int i = 0; i < linesPrintedLastTime - linesJustPrinted; i++) {
                Cursor.clearRestOfLine(terminal);
                Cursor.printNewLine(terminal);
            }
    }

    // Expects the cursor to be at the line for the first option and leaves it there.
    void clearAllOptions() {
        clearExcessLines(0, linesPrintedLastTime);
        Cursor.moveUp(terminal, linesPrintedLastTime);
    }

    void increaseSelectedIndex() {
        int current = selectedIndex;
        do {
            current++;
        } while (current < options.size() && !matchesFilter(options.get(current)));

        if (current < options.size())
            selectedIndex = current;
    }

    void decreaseSelectedIndex() {
        int current = selectedIndex;
        do {
            current--;
        } while (current >= 0 && !matchesFilter(options.get(current)));
        if (current >= 0)
            selectedIndex = current;
    }

    int getSelectedIndex() {
        return selectedIndex;
    }

    String getSelectedOption() {
        return options.get(selectedIndex);
    }

    boolean hasFilteredOptions() {
        return options.stream().anyMatch(this::matchesFilter);
    }

    private boolean matchesFilter(String option) {
        if (filter.isEmpty())
            return true;

        String optionNorm = removeAccents(option.toLowerCase());
        String filterNorm = removeAccents(filter.toLowerCase());
        return optionNorm.contains(filterNorm);
    }

    /**
     * Prints the options to the terminal, highlighting the selected option and applying the filter.
     * This method should be called with the cursor in the line where you want to display the first option.
     * It saves the cursor position before printing and restores it after printing. So, after returning from this method,
     * the cursor will be in the same position as before.
     */

    private static String highlightMatchingText(String option, String filter) {

        if (filter.isEmpty())
            return option;

        String normalizedOption = removeAccents(option.toLowerCase());
        String normalizedFilter = removeAccents(filter.toLowerCase());

        if (normalizedOption.isEmpty() || normalizedFilter.isEmpty())
            return option;

        int matchIndex = normalizedOption.indexOf(normalizedFilter);
        if (matchIndex == -1)
            return option;

        int endIndex = Math.min(option.length(), matchIndex + filter.length());
        String beforeMatch = option.substring(0, Math.max(0, matchIndex));
        String matchedText = Colours.addColor(option.substring(Math.max(0, matchIndex), endIndex),
                Colours.COLOR_HIGHLIGHT);
        String afterMatch = option.substring(endIndex);
        return beforeMatch + matchedText + afterMatch;
    }

    private static String removeAccents(String text) {
        if (text == null)
            throw new IllegalArgumentException("Input cannot be null");

        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    // In addition to printing the message, it clears the rest of the line and moves to the next line.
    private void printFullLine(String message) {
        terminal.writer().print(message);
        Cursor.clearRestOfLine(terminal);
        Cursor.printNewLine(terminal);
    }
}
