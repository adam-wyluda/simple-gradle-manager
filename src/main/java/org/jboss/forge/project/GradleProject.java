package org.jboss.forge.project;

import com.google.common.base.Optional;
import org.jboss.forge.parser.groovy.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Adam Wyłuda
 */
public class GradleProject {

    public static final String DEPENDENCIES = "dependencies";

    private String source;

    public GradleProject(String source) {
        this.source = source;
    }

    public List<Dependency> getDependencies() {
        SimpleGroovyParser groovyParser = new SimpleGroovyParser(source);
        for (InvocationWithClosure invocation : groovyParser.getInvocationsWithClosure())  {
            if (DEPENDENCIES.equals(invocation.getMethodName())) {
                return readDependenciesFromInvocation(invocation);
            }
        }
        // If dependencies invocation was not found, we return empty list
        return Collections.EMPTY_LIST;
    }

    public void addDependency(Dependency dependency) {
        Optional<InvocationWithClosure> dependenciesInvocation = findDependenciesInvocation();
        // If there was no dependencies closure
        if (!dependenciesInvocation.isPresent()) {
            createDependenciesClosure();
            dependenciesInvocation = findDependenciesInvocation();
        }
        addDependencyToSource(dependenciesInvocation.get(), dependency);
    }

    public void removeDependency(Dependency dependency) {
        Optional<InvocationWithClosure> dependenciesInvocation = findDependenciesInvocation();
        // If there is no dependencies closure then it will do nothing
        if (dependenciesInvocation.isPresent()) {
            removeDependencyFromSource(dependenciesInvocation.get(), dependency);
        }
    }

    public String getSource() {
        return source;
    }

    Optional<InvocationWithClosure> findDependenciesInvocation() {
        SimpleGroovyParser groovyParser = new SimpleGroovyParser(source);
        for (InvocationWithClosure invocation : groovyParser.getInvocationsWithClosure())  {
            if (DEPENDENCIES.equals(invocation.getMethodName())) {
                return Optional.of(invocation);
            }
        }
        return Optional.absent();
    }

    /**
     * Appends dependencies definition closure at the end of the source.
     */
    void createDependenciesClosure() {
        source += "\ndependencies {\n    \n}\n";
    }

    /**
     * Adds new line to dependencies closure with new dependency invocation.
     */
    void addDependencyToSource(InvocationWithClosure dependenciesClosure, Dependency dependency) {
        String line = String.format("    %s '%s'\n", dependency.getType().getMethodName(), dependency.toGradleString());

        // Closure is always composed of '{' and '}' characters, so we will insert our dependency just before '}'
        int lineNumber = dependenciesClosure.getLastLineNumber();
        int columnNumber = dependenciesClosure.getLastColumnNumber();
        columnNumber--;

        source = SourceUtil.insertString(source, line, lineNumber, columnNumber);
    }

    /**
     * Removes statically defined dependency, it doesn't work for dynamic dependencies.
     */
    void removeDependencyFromSource(InvocationWithClosure dependenciesClosure, Dependency dependency) {
        SimpleGroovyParser groovyParser = new SimpleGroovyParser(source);
        String dependencyGradleString = dependency.toGradleString();

        SourceCodeElement previousInvocation = null;
        // Search in invocations with string parameter
        for (InvocationWithString invocation : dependenciesClosure.getInternalStringInvocations()) {
            if (invocation.getMethodName().equals(dependency.getType().getMethodName()) &&
                    invocation.getString().equals(dependencyGradleString)) {
                removeDependencyInvocationFromSource(dependenciesClosure, previousInvocation, invocation);
            }
            previousInvocation = invocation;
        }
        // Search in invocations with map parameter
        for (InvocationWithMap invocation : dependenciesClosure.getInternalMapInvocations()) {
            if (invocation.getMethodName().equals(dependency.getType().getMethodName())) {
                Dependency dependencyFromInvocation =
                        Dependency.fromMap(invocation.getParameters(), Dependency.Type.fromMethodName(invocation.getMethodName()));
                if (dependencyFromInvocation.equals(dependency)) {
                    removeDependencyInvocationFromSource(dependenciesClosure, previousInvocation, invocation);
                }
            }
            previousInvocation = invocation;
        }
    }

    void removeDependencyInvocationFromSource(InvocationWithClosure dependenciesClosure, SourceCodeElement previousInvocation,
                                              SourceCodeElement dependencyInvocation) {
        int lineNumber = previousInvocation != null ? previousInvocation.getLastLineNumber() : dependenciesClosure.getLineNumber();
        int positionOfDependenciesInvocation =
                SourceUtil.positionInSource(source, dependenciesClosure.getLineNumber(), dependenciesClosure.getColumnNumber());
        int columnNumber = previousInvocation != null ? previousInvocation.getLastColumnNumber()
                : source.substring(positionOfDependenciesInvocation).indexOf("{") + 2;
        // + 2 because + 1 as we want next char, and +1 because it's column number which is indexed from 1
        int lastLineNumber = dependencyInvocation.getLastLineNumber();
        int lastColumnNumber = dependencyInvocation.getLastColumnNumber();
        source = SourceUtil.removeSourceFragment(source, lineNumber, columnNumber, lastLineNumber, lastColumnNumber);
    }

    static List<Dependency> readDependenciesFromInvocation(InvocationWithClosure dependenciesInvocation) {
        List<Dependency> dependencies = new ArrayList<>();
        // Read dependencies in a form of invocation with a constant, like: testRuntime 'a:b:1'
        for (InvocationWithString invocation : dependenciesInvocation.getInternalStringInvocations()) {
            processStringInvocation(dependencies, invocation);
        }
        // Read dependencies in a form of invocation with a map, like: compile group: 'a', name: 'b', version: '1'
        for (InvocationWithMap invocation : dependenciesInvocation.getInternalMapInvocations()) {
            processMapInvocation(dependencies, invocation);
        }
        return dependencies;
    }

    static void processStringInvocation(List<Dependency> dependencies, InvocationWithString invocation) {
        for (Dependency.Type type : Dependency.Type.values()) {
            if (type.getMethodName().equals(invocation.getMethodName())) {
                Dependency dependency = Dependency.fromString(invocation.getString(), type);
                dependencies.add(dependency);
                return;
            }
        }
    }

    static void processMapInvocation(List<Dependency> dependencies, InvocationWithMap invocation) {
        for (Dependency.Type type : Dependency.Type.values()) {
            if (type.getMethodName().equals(invocation.getMethodName())) {
                Dependency dependency = Dependency.fromMap(invocation.getParameters(), type);
                dependencies.add(dependency);
                return;
            }
        }
    }
}
