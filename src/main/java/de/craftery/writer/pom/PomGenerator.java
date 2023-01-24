package de.craftery.writer.pom;

import de.craftery.writer.StructuredFile;
import lombok.Getter;

public class PomGenerator extends StructuredFile {
    public PomGenerator() {
        this.setNeeded(true);
        this.setFileName("pom.xml");
        this.setFolderPrefix("");
    }

    @Override
    public void buildSections() {
        this.addSection(XMLGenerator.xmlHeader());
        this.addSection(this.projectSection);
    }

    @Getter
    private final PomProjectSection projectSection = new PomProjectSection();
}
