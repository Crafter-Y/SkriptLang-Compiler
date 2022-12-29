package de.craftery.parser.structure;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.parser.SkriptParser;
import de.craftery.parser.helper.Options;

import java.util.logging.Level;

public class OptionsNode extends StructureNode {
    @Override
    public void acceptLine(Fragment line, int indentation) {
        if (indentation > 1) {
            Main.log(Level.WARNING, "OptionsNode", "Options must be indented one time!");
            System.exit(1);
        }
        if (indentation == 0) {
            SkriptParser.exitNode().acceptLine(line, indentation);
            return;
        }

        String[] parts = line.getContents().split(":");
        if (parts.length != 2) {
            Main.log(Level.WARNING, "OptionsNode", "Wrong option Syntax");
            System.exit(1);
            return;
        }
        String key = parts[0].trim();
        String value = parts[1].trim();

        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }

        Options.registerOption(key, value);
    }

    @Override
    public OptionsNode initialize(Fragment line) {
        return this;
    }
}
