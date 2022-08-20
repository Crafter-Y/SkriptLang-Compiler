package de.craftery.parser;

import de.craftery.parser.structure.RootNode;
import de.craftery.parser.structure.StructureNode;
import lombok.Getter;

import java.util.Stack;

public class SkriptParser {
    @Getter
    private static SkriptParser instance;
    public SkriptParser() {
        instance = this;
        structureLevel.push(new RootNode());
    }

    public static void entryNode(StructureNode node) {
        SkriptParser.getInstance().structureLevel.push(node);
    }
    private int lineNumber = 0;

    private final Stack<StructureNode> structureLevel = new Stack<>();
    public void acceptLine(String line) {
        lineNumber++;
        if (line.trim().equals("")) {
            System.out.println("Skipping Line " + lineNumber + " :'" + line + "'");
            return;
        }
        System.out.println("Parsing Line " + lineNumber + " :'" + line + "'");
        int indentationSpaces = 0;
        while (line.startsWith(" ")) {
            indentationSpaces++;
            line = line.substring(1);
        }
        if (indentationSpaces % 4 != 0) {
            System.err.println("Indentation error on line " + lineNumber + ": '" + line + "'");
            System.exit(1);
        }
        structureLevel.peek().acceptLine(line, indentationSpaces / 4);

    }
}
