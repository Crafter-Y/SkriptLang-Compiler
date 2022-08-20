package de.craftery.writer.javaFile;

import de.craftery.writer.WritingSection;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class RawMethodSection extends WritingSection {
    public RawMethodSection(int indentationLevel) {
        super(indentationLevel);
    }

    @Setter
    private String rawMethodSignature = "";

    private final List<String> bodyLines = new ArrayList<>();

    public void addLine(String line) {
        this.bodyLines.add(line);
    }

    private final List<String> annotations = new ArrayList<>();

    public void addAnnotation(String annotation) {
        this.annotations.add(annotation);
    }

    @Override
    public void buildLines() {
        for (String annotation : annotations) {
            this.getLines().add(annotation);
        }
        this.getLines().add(rawMethodSignature + " {");
        for (String line : bodyLines) {
            this.getLines().add("    " + line);
        }
        this.getLines().add("}");
        this.getLines().add("");
    }
}
