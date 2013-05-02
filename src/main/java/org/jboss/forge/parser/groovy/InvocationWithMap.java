package org.jboss.forge.parser.groovy;

import java.util.Map;

/**
 * Represents invocations of a method which takes map as a parameter.
 *
 * @author Adam Wyłuda
 */
public class InvocationWithMap extends SourceCodeElement {

    private final String methodName;
    private final Map<String, String> parameters;

    public InvocationWithMap(String methodName, Map<String, String> parameters,
                             int lineNumber, int columnNumber, int lastLineNumber, int lastColumnNumber) {
        super(lineNumber, columnNumber, lastLineNumber, lastColumnNumber);
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
