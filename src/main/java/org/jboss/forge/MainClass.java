package org.jboss.forge;

import com.google.common.io.Files;
import org.jboss.forge.project.Dependency;
import org.jboss.forge.project.GradleProject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Simple UI for Gradle project manipulation.
 *
 * @author Adam Wyłuda
 */
public class MainClass {

    private final Scanner in = new Scanner(System.in);
    private final GradleProject gradleProject;

    public static void main(String... args) {
        new MainClass().start();
    }

    public MainClass() {
        System.out.println("Reading build.gradle...");
        String source = readGradleFile();
        gradleProject = new GradleProject(source);
    }

    String readGradleFile() {
        File gradleBuildFile = new File("build.gradle");
        if (!gradleBuildFile.isFile()) {
            System.out.println("Gradle build file not found!");
            System.exit(1);
        }
        String content = "";
        try {
            content = Files.toString(gradleBuildFile, Charset.defaultCharset());
        } catch (IOException exception) {
            exception.printStackTrace();
            System.exit(1);
        }
        return content;
    }

    void saveGradleFile() {
        File gradleBuildFile = new File("build.gradle");
        String source = gradleProject.getSource();
        try {
            Files.write(source, gradleBuildFile, Charset.defaultCharset());
        } catch (IOException exception) {
            exception.printStackTrace();
            System.exit(1);
        }
    }

    public void start() {
        runConsole();
    }

    void runConsole() {
        while (true) {
            showMenu();
            String option = in.next();
            execute(option);
        }
    }

    void showMenu() {
        System.out.println("\n*** Available options: list-dependencies, add-dependency, remove-dependency, exit");
        System.out.print("->");
    }

    void execute(String option) {
        switch (option) {
            case "list-dependencies":
                listDependencies();
                break;
            case "add-dependency":
                addDependency();
                break;
            case "remove-dependency":
                removeDependency();
                break;
            case "exit":
                exit();
                break;
        }
    }

    void listDependencies() {
        for (Dependency dependency : gradleProject.getDependencies()) {
            System.out.println(String.format("%s '%s'", dependency.getType().getMethodName(), dependency.toGradleString()));
        }
    }

    void addDependency() {
        Dependency dependency = readDependency();
        gradleProject.addDependency(dependency);
        saveGradleFile();
    }

    void removeDependency() {
        Dependency dependency = readDependency();
        gradleProject.removeDependency(dependency);
        saveGradleFile();
    }

    void exit() {
        System.exit(0);
    }

    Dependency readDependency() {
        System.out.println("Enter dependency string (in format: group:name:version):");
        String dependencyString = in.next();
        System.out.println("Enter configuration (compile, runtime, testCompile, testRuntime):");
        String config = in.next();
        Dependency.Type type = Dependency.Type.COMPILE;
        for (Dependency.Type dependencyType : Dependency.Type.values()) {
            if (dependencyType.getMethodName().equals(type)) {
                type = dependencyType;
            }
        }
        Dependency dependency = Dependency.fromString(dependencyString, type);
        return dependency;
    }
}
