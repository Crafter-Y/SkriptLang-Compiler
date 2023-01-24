package de.craftery.parser;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.ProjectGenerator;
import de.craftery.parser.structure.RootNode;
import de.craftery.parser.structure.StructureNode;
import de.craftery.writer.core.FormatterGenerator;
import de.craftery.writer.core.MainGenerator;
import de.craftery.writer.core.PluginYMLGenerator;
import de.craftery.writer.pom.PomGenerator;
import lombok.Getter;

import java.util.Stack;

public class SkriptParser {
    @Getter
    private static SkriptParser instance;
    @Getter
    private final ProjectGenerator projectGenerator;
    @Getter
    private final MainGenerator mainGenerator;
    @Getter
    private final PluginYMLGenerator pluginYMLGenerator;
    @Getter
    private final String outputFolder;
    private final Stack<StructureNode> structureLevel = new Stack<>();

    public SkriptParser(String outputFolder) {
        instance = this;
        this.outputFolder = outputFolder;
        this.projectGenerator = new ProjectGenerator();
        this.mainGenerator = new MainGenerator();
        this.pluginYMLGenerator = new PluginYMLGenerator();
        new PomGenerator();

        this.structureLevel.push(new RootNode());
    }

    public static void entryNode(StructureNode node) {
        SkriptParser.getInstance().structureLevel.push(node);
    }

    public static StructureNode exitNode() {
        SkriptParser.getInstance().structureLevel.pop();
        return SkriptParser.getInstance().structureLevel.peek();
    }

    public void acceptLine(String line, int lineNumber) {
        if (line.trim().equals("")) {
            return;
        }
        Main.log("Parsing Line " + lineNumber + " :'" + line + "'");
        int indentationSpaces = 0;
        while (true) {
            if (line.startsWith(" ")) {
                indentationSpaces++;
                line = line.substring(1);
            } else if(line.startsWith("\t")) {
                indentationSpaces += 4;
                line = line.substring(1);
            } else {
                break;
            }
        }
        if (indentationSpaces % 4 != 0) {
            Main.exit("Indentation error on line " + lineNumber + ": '" + line + "'");
        }
        line = line.split("#")[0];
        structureLevel.peek().acceptLine(new Fragment(line), indentationSpaces / 4);
    }

    public void finish() {
        mainGenerator.build();
        if (FormatterGenerator.isInitialized()) {
            FormatterGenerator.getInstance().build();
        }

        // this calles completing actions on each (maybe) not finished node
        structureLevel.peek().acceptLine(new Fragment(""), 0);
        projectGenerator.generate();
    }
}
