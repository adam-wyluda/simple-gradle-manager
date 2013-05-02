package org.jboss.forge.parser.groovy;

/**
 * @author Adam Wyłuda
 */
public class SourceCodeElement {

    private final int lineNumber;
    private final int columnNumber;
    private final int lastLineNumber;
    private final int lastColumnNumber;

    public SourceCodeElement(int lineNumber, int columnNumber, int lastLineNumber, int lastColumnNumber) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.lastLineNumber = lastLineNumber;
        this.lastColumnNumber = lastColumnNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public int getLastLineNumber() {
        return lastLineNumber;
    }

    public int getLastColumnNumber() {
        return lastColumnNumber;
    }
}
