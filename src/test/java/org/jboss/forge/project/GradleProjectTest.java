package org.jboss.forge.project;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Adam Wyłuda
 */
public class GradleProjectTest {

    @Test
    public void listDependenciesTest() {
        String source = "dependencies {\n" +
                "    compile 'a:b:1.2.3'\n" +
                "    testCompile group: 'x', name: 'y', version: '2.5'\n" +
                "}\n";

        GradleProject gradleProject = new GradleProject(source);
        List<Dependency> dependencies = gradleProject.getDependencies();
        assertEquals(2, dependencies.size());

        Dependency firstDependency = dependencies.get(0);
        assertEquals("a", firstDependency.getGroupId());
        assertEquals("b", firstDependency.getArtifactId());
        assertEquals("1.2.3", firstDependency.getVersion());

        Dependency secondDependency = dependencies.get(1);
        assertEquals("x", secondDependency.getGroupId());
        assertEquals("y", secondDependency.getArtifactId());
        assertEquals("2.5", secondDependency.getVersion());
    }

    @Test
    public void listDependenciesTestEmpty() {
        String source = "dependencies{}";

        GradleProject gradleProject = new GradleProject(source);
        assertEquals(0, gradleProject.getDependencies().size());
    }

    @Test
    public void listDependenciesTestNoClosure() {
        String source = "import javax.inject.Inject\n" +
                "repositories { mavenCentral() }\n";

        GradleProject gradleProject = new GradleProject(source);
        assertEquals(0, gradleProject.getDependencies().size());
    }

    @Test
    public void addDependencyTest() {
        String source = "dependencies {\n" +
                "    compile 'a:b:2'\n" +
                "}\n";
        String expectedOutput = "dependencies {\n" +
                "    compile 'a:b:2'\n" +
                "    compile 'x:y:1'\n" +
                "}\n";
        Dependency dependency = Dependency.fromString("x:y:1", Dependency.Type.COMPILE);

        GradleProject gradleProject = new GradleProject(source);
        gradleProject.addDependency(dependency);
        assertEquals(expectedOutput, gradleProject.getSource());
    }

    @Test
    public void removeDependencyTest() {
        String source = "dependencies {\n" +
                "    compile 'a:b:2'\n" +
                "    compile 'x:y:1'\n" +
                "}\n";
        String expectedOutput = "dependencies {\n" +
                "    compile 'a:b:2'\n" +
                "}\n";
        Dependency dependency = Dependency.fromString("x:y:1", Dependency.Type.COMPILE);

        GradleProject gradleProject = new GradleProject(source);
        gradleProject.removeDependency(dependency);
        assertEquals(expectedOutput, gradleProject.getSource());
    }

    @Test
    public void removeDependencyTestRemoveFirst() {
        String source = "repositories {}\n" +
                "dependencies {\n" +
                "    compile 'x:y:1'\n" +
                "    compile 'a:b:2'\n" +
                "}\n";
        String expectedOutput = "repositories {}\n" +
                "dependencies {\n" +
                "    compile 'a:b:2'\n" +
                "}\n";
        Dependency dependency = Dependency.fromString("x:y:1", Dependency.Type.COMPILE);

        GradleProject gradleProject = new GradleProject(source);
        gradleProject.removeDependency(dependency);
        assertEquals(expectedOutput, gradleProject.getSource());
    }
}
