package de.craftery.writer.pom;

import de.craftery.writer.WritingSection;

import java.util.ArrayList;
import java.util.List;

public class PomDependencies  extends WritingSection {
    public PomDependencies() {
        super(1);
    }

    private final List<PomDependency> dependencies = new ArrayList<>();

    public void addDependency(PomDependency dependency) {
        dependencies.add(dependency);
    }

    @Override
    public void buildLines() {
        this.getLines().add("<dependencies>");
        this.getLines().add("    <dependency>");
        this.getLines().add("        <groupId>io.papermc.paper</groupId>");
        this.getLines().add("        <artifactId>paper-api</artifactId>");
        this.getLines().add("        <version>1.18.2-R0.1-SNAPSHOT</version>");
        this.getLines().add("        <scope>provided</scope>");
        this.getLines().add("    </dependency>");
        for (PomDependency dependency : dependencies) {
            this.getLines().add("    <dependency>");
            this.getLines().add("        <groupId>" + dependency.getGroupId() + "</groupId>");
            this.getLines().add("        <artifactId>" + dependency.getArtifactId() + "</artifactId>");
            this.getLines().add("        <version>" + dependency.getVersion() + "</version>");
            this.getLines().add("        <scope>" + dependency.getScope() + "</scope>");
            this.getLines().add("    </dependency>");
        }
        this.getLines().add("</dependencies>");
    }
}
