package de.craftery.writer.javaFile;

import de.craftery.writer.WritingSection;

public class PackageSection extends WritingSection {
    public PackageSection() {
        super(0);
    }

    private String packageName = "";

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public void buildLines() {
        this.getLines().add("package " + this.packageName + ";");
        this.getLines().add("");
    }
}
