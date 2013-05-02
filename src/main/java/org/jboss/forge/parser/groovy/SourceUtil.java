package org.jboss.forge.parser.groovy;

import static com.google.common.base.Preconditions.*;

/**
 * @author Adam Wyłuda
 */
public class SourceUtil {

    /**
     * Inserts string at specified position in source.
     *
     * @param lineNumber   Position of inserted line, indexed from 1.
     * @param columnNumber Position of inserted line, indexed from 1.
     */
    public static String insertString(String source, String string, int lineNumber, int columnNumber) {
        int position = positionInSource(source, lineNumber, columnNumber);
        return source.substring(0, position) + string + source.substring(position);
    }

    /**
     * @param lineNumber   Position indexed from 1.
     * @param columnNumber Position indexed from 1.
     * @return Real position of given coordinates in file.
     */
    public static int positionInSource(String source, int lineNumber, int columnNumber) {
        checkArgument(lineNumber >= 1, "Line number must be greater than 0");
        checkArgument(columnNumber >= 1, "Column number must be greater than 0");

        // Position is indexed from 1, arrays are indexed from 0, so we fix it
        lineNumber--;
        columnNumber--;

        String[] sourceLines = source.split("\n");

        checkArgument(lineNumber < sourceLines.length, "Given line number exceeds line count");
        checkArgument(columnNumber <= sourceLines[lineNumber].length(), "Given column number exceeds column count in line");

        int precedingCharactersCount = 0;
        for (int lineIndex = 0; lineIndex < lineNumber; lineIndex++) {
            // Add length of the line plus one, which counts for end line character
            precedingCharactersCount += sourceLines[lineIndex].length() + 1;
        }

        // Position is sum of character count in preceding lines and given column number
        int position = precedingCharactersCount + columnNumber;
        return position;
    }

    public static String removeSourceFragment(String source, int lineNumber, int columnNumber,
                                              int lastLineNumber, int lastColumnNumber) {
        int begginingPosition = positionInSource(source, lineNumber, columnNumber);
        int endingPosition = positionInSource(source, lastLineNumber, lastColumnNumber);
        return source.substring(0, begginingPosition) + source.substring(endingPosition);
    }
}
