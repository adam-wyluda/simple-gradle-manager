package org.jboss.forge.project;

import com.google.common.collect.Maps;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Adam Wyłuda
 */
public class Dependency {

    private static final Map<String, Type> TYPE_BY_METHOD_NAME_MAP = Maps.newHashMap();

    public enum Type {
        COMPILE("compile"), RUNTIME("runtime"), TEST_COMPILE("testCompile"), TEST_RUNTIME("testRuntime");

        private final String methodName;

        private Type(String methodName) {
            this.methodName = methodName;
            TYPE_BY_METHOD_NAME_MAP.put(methodName, this);
        }

        public String getMethodName() {
            return methodName;
        }

        @Override
        public String toString() {
            return methodName;
        }

        public static Type fromMethodName(String methodName) {
            return TYPE_BY_METHOD_NAME_MAP.get(methodName);
        }
    }

    public static final String GROUP_ID = "group";
    public static final String ARTIFACT_ID = "name";
    public static final String VERSION = "version";

    private final String groupId;
    private final String artifactId;
    private final String version;
    private final Type type;

    public Dependency(String groupId, String artifactId, String version, Type type) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;
    }

    /**
     * Creates dependency from string like "group:artifact:version".
     */
    public static Dependency fromString(String dependencyString, Type type) {
        String[] split = dependencyString.split(":");
        checkArgument(split.length == 3);

        String groupId = split[0];
        String artifactId = split[1];
        String version = split[2];

        return new Dependency(groupId, artifactId, version, type);
    }

    /**
     * Creates dependency from map.
     */
    public static Dependency fromMap(Map<String, String> map, Type type) {
        String groupId = map.get(GROUP_ID);
        String artifactId = map.get(ARTIFACT_ID);
        String version = map.get(VERSION);

        return new Dependency(groupId, artifactId, version, type);
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public Type getType() {
        return type;
    }

    public String toGradleString() {
        return String.format("%s:%s:%s", groupId, artifactId, version);
    }

    @Override
    public String toString() {
        return String.format("%s:%s:%s (%s)", groupId, artifactId, version, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dependency that = (Dependency) o;

        if (artifactId != null ? !artifactId.equals(that.artifactId) : that.artifactId != null) return false;
        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;
        if (type != that.type) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
