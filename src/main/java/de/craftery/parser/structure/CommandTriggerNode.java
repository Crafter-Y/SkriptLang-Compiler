package de.craftery.parser.structure;

import de.craftery.Main;
import de.craftery.parser.SkriptParser;

import java.util.logging.Level;

public class CommandTriggerNode extends StructureNode {
    @Override
    public void acceptLine(String line, int indentation) {
        if (indentation > 2) {
            Main.log(Level.WARNING, "CommandTriggerNode", "Command Trigger Fields must be indented two times!");
            System.exit(1);
        }
        if (indentation < 2) {
            Main.log(Level.INFO, "CommandTriggerNode", "Redirected from CommandTriggerNode");
            SkriptParser.exitNode().acceptLine(line, indentation);
            return;
        }
        Main.log(Level.WARNING, "CommandTriggerNode", "Command Trigger Actions are not yet implemented!");
    }

    @Override
    public CommandTriggerNode initialize(String line) {
        return this;
    }
}
