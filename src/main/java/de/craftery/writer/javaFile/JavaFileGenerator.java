package de.craftery.writer.javaFile;

import de.craftery.writer.StructuredFile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class JavaFileGenerator extends StructuredFile {
    public JavaFileGenerator() {
        this.setPackagePrefix("de.craftery.autogenerated");
        this.packageSection.setPackageName("de.craftery.autogenerated");
    }
    private final PackageSection packageSection = new PackageSection();
    private final ImportSection importSection = new ImportSection();

    @Getter
    private final List<ClassSection> classes = new ArrayList<>();
    @Override
    public void buildSections() {
        this.addSection(packageSection);
        this.addSection(importSection);
        for (ClassSection classSection : classes) {
            this.addSection(classSection);
        }
    }

    @Override
    public void setFileName(String fileName) {
        super.setFileName(fileName + ".java");
    }

    public void setPackage(String packageName) {
        this.packageSection.setPackageName("de.craftery.autogenerated." + packageName);
        this.setPackageName(packageName);
    }

    public void requireImport(String importName) {
        this.importSection.requireImport(importName);
    }

    public void addClass(ClassSection classSection) {
        this.classes.add(classSection);
    }
}
