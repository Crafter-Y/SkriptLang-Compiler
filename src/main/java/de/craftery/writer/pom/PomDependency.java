package de.craftery.writer.pom;

import lombok.Data;

@Data
public class PomDependency {
    private String groupId;
    private String artifactId;
    private String version;
    private String scope;
    //private String type;
}
