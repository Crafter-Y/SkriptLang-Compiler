package de.craftery.writer.pom;

import de.craftery.writer.WritingSection;

import java.util.ArrayList;
import java.util.List;

public class PomRepositories extends WritingSection {
    public PomRepositories() {
        super(1);
    }

    private final List<PomRepository> repositories = new ArrayList<>();

    public void addRepository(PomRepository repository) {
        repositories.add(repository);
    }

    @Override
    public void buildLines() {
        this.getLines().add("<repositories>");
        this.getLines().add("    <repository>");
        this.getLines().add("        <id>papermc-repo</id>");
        this.getLines().add("        <url>https://papermc.io/repo/repository/maven-public/</url>");
        this.getLines().add("    </repository>");
        for (PomRepository repository : repositories) {
            this.getLines().add("    <repository>");
            this.getLines().add("        <id>" + repository.getId() + "</id>");
            this.getLines().add("        <url>" + repository.getUrl() + "</url>");
            this.getLines().add("    </repository>");
        }
        this.getLines().add("</repositories>");
    }
}
