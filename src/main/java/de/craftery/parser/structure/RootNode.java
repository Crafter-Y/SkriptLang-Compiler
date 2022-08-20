package de.craftery.parser.structure;

import de.craftery.parser.SkriptParser;
import de.craftery.writer.core.MainGenerator;
import de.craftery.writer.core.PluginYMLGenerator;
import de.craftery.writer.pom.PomGenerator;

public class RootNode extends StructureNode {
    public RootNode() {
        PomGenerator.initialize();
        PluginYMLGenerator.initialize();
        MainGenerator.initialize();
    }

    @Override
    public void acceptLine(String line, int indentation) {
        if (indentation != 0) {
            System.err.println("There should not be an indentation here!");
            System.exit(1);
        }
        String[] tokens = line.trim().split(" ");
        switch (tokens[0]) {
            case "command":
                CommandNode node = new CommandNode().initialize(line);
                SkriptParser.entryNode(node);
                break;
            default:
                reportUnknownToken(line, tokens[0], 0);
                break;
        }
    }

    @Override
    public RootNode initialize(String ignored) {
        return this;
    }
}
