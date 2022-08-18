package de.craftery.writer;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class WritingSection {
    @Getter
    private final List<String> lines = new ArrayList<>();

    public abstract void buildLines();

    private final List<String> indentedLines = new ArrayList<>();

    public List<String> getIndentedLines() {
        buildLines();
        buildIndentations();
        return indentedLines;
    }
    private void buildIndentations() {
        for (String line : lines) {
            StringBuilder lineBuilder = new StringBuilder();
            for (int i = 0; i < indentationLevel; i++) {
                lineBuilder.append("    ");
            }
            lineBuilder.append(line);
            this.indentedLines.add(lineBuilder.toString());
        }
    }

    public WritingSection(int indentationLevel) {
        this.indentationLevel = indentationLevel;
    }
    @Getter
    private final int indentationLevel;
}
