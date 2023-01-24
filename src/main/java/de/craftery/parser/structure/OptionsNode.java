package de.craftery.parser.structure;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.parser.SkriptParser;
import de.craftery.parser.helper.Options;

public class OptionsNode extends StructureNode {
    @Override
    public void acceptLine(Fragment line, int indentation) {
        if (indentation > 1) {
            Main.exit("Options must be indented one time!");
        }
        if (indentation == 0) {
            SkriptParser.exitNode().acceptLine(line, indentation);
            return;
        }

        String[] parts = line.getContents().split(":");
        if (parts.length != 2) {
            Main.exit("Wrong option Syntax");
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
