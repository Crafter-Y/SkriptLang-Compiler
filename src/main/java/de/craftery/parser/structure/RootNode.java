package de.craftery.parser.structure;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.parser.SkriptParser;
import de.craftery.writer.core.FormatterGenerator;
import de.craftery.writer.core.MainGenerator;
import de.craftery.writer.core.PluginYMLGenerator;
import de.craftery.writer.pom.PomGenerator;

import java.util.logging.Level;

public class RootNode extends StructureNode {
    public RootNode() {
        PomGenerator.initialize();
        PluginYMLGenerator.initialize();
        MainGenerator.initialize();
    }

    @Override
    public void acceptLine(Fragment line, int indentation) {
        if (indentation != 0) {
            Main.log(Level.WARNING, "RootNode" , "There should not be an indentation here! " + line);
            System.exit(1);
        }
        String[] tokens = line.getContents().trim().split(" ");
        switch (tokens[0]) {
            case "command":
                CommandNode node = new CommandNode().initialize(line);
                SkriptParser.entryNode(node);
                break;
            case "": // empty case will happen at the end of the file
                MainGenerator.getInstance().build();
                FormatterGenerator.getInstance().build();
                break;
            default:
                reportUnknownToken(line, tokens[0], 0);
                break;
        }
    }

    @Override
    public RootNode initialize(Fragment ignored) {
        return this;
    }
}
