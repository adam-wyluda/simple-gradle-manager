package org.jboss.forge.parser.groovy;

/**
 * Represents invocation of a method which takes string constant as a parameter.
 *
 * @author Adam Wyłuda
 */
public class InvocationWithString extends SourceCodeElement {

    private final String methodName;
    private final String string;

    public InvocationWithString(String methodName, String string,
                                int lineNumber, int columnNumber, int lastLineNumber, int lastColumnNumber) {
        super(lineNumber, columnNumber, lastLineNumber, lastColumnNumber);
        this.methodName = methodName;
        this.string = string;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getString() {
        return string;
    }
}
