package de.craftery.writer;

import java.util.ArrayList;
import java.util.List;

public abstract class StructuredFile extends WriterComponent {
    private final List<WritingSection> sections = new ArrayList<>();

    public void addSection(WritingSection section) {
        sections.add(section);
    }

    public abstract void buildSections();
    @Override
    public void prepareContent() {
        buildSections();
        for (WritingSection section : sections) {
            this.getContent().addAll(section.getIndentedLines());
        }
    }
}
