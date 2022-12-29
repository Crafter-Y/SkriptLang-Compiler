package de.craftery.writer.javaFile;

import de.craftery.writer.WritingSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImportSection extends WritingSection {
    public ImportSection() {
        super(0);
    }

    private final List<String> imports = new ArrayList<>();

    public void requireImport(String importName) {
        if (!this.imports.contains(importName)) {
            this.imports.add(importName);
        }
    }

    @Override
    public void buildLines() {
        Collections.sort(imports);
        for (String importName : imports) {
            this.getLines().add("import " + importName + ";");
        }
        this.getLines().add("");
    }
}
