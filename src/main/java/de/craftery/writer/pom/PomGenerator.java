package de.craftery.writer.pom;

import de.craftery.writer.StructuredFile;
import lombok.Getter;
import lombok.Setter;

public class PomGenerator extends StructuredFile {
    public PomGenerator() {
        this.setFileName("pom.xml");
        this.setFolderPrefix("");
    }

    @Override
    public void buildSections() {
        this.addSection(XMLGenerator.xmlHeader());
        this.addSection(this.projectSection);
    }

    public static void initialize() {
        getInstance().setNeeded(true);
    }

    @Getter
    private final PomProjectSection projectSection = new PomProjectSection();

    @Setter
    private static PomGenerator instance;
    public static PomGenerator getInstance() {
        if (instance == null) {
            instance = new PomGenerator();
        }
        return instance;
    }
}
