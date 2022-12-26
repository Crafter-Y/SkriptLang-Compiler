package de.craftery.parser;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.parser.structure.RootNode;
import de.craftery.parser.structure.StructureNode;
import lombok.Getter;

import java.util.Stack;
import java.util.logging.Level;

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
    public static StructureNode exitNode() {
        SkriptParser.getInstance().structureLevel.pop();
        return SkriptParser.getInstance().structureLevel.peek();
    }

    private final Stack<StructureNode> structureLevel = new Stack<>();
    public void acceptLine(String line, int lineNumber) {
        if (line.trim().equals("")) {
            Main.log(Level.INFO, "SkriptParser", "Skipping Line " + lineNumber + " :'" + line + "'");
            return;
        }
        Main.log(Level.SEVERE, "SkriptParser", "Parsing Line " + lineNumber + " :'" + line + "'");
        int indentationSpaces = 0;
        while (line.startsWith(" ")) {
            indentationSpaces++;
            line = line.substring(1);
        }
        if (indentationSpaces % 4 != 0) {
            Main.log(Level.WARNING, "SkriptParser", "Indentation error on line " + lineNumber + ": '" + line + "'");
            System.exit(1);
        }
        structureLevel.peek().acceptLine(new Fragment(line), indentationSpaces / 4);
    }

    public void finish() {
        structureLevel.peek().acceptLine(new Fragment(""), 0);
    }
}
