package de.craftery.writer.pom;

import de.craftery.writer.PlainTextWritingSection;
import de.craftery.writer.WritingSection;

public class XMLGenerator {
    public static WritingSection xmlHeader() {
        WritingSection section = new PlainTextWritingSection(0);
        section.getLines().add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        return section;
    }
}
