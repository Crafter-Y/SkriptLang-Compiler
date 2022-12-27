package de.craftery.parser.structure;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.parser.SkriptParser;
import de.craftery.parser.helper.ActionParser;
import de.craftery.writer.command.CommandGenerator;
import lombok.Setter;

import java.util.logging.Level;

public class CommandTriggerNode extends StructureNode {
    @Setter
    private CommandGenerator generator;

    @Override
    public void acceptLine(Fragment line, int indentation) {
        if (indentation > 2) {
            Main.log(Level.WARNING, "CommandTriggerNode", "Command Trigger Fields must be indented two times!");
            System.exit(1);
        }
        if (generator == null) {
            Main.log(Level.WARNING, "CommandTriggerNode", "There is no generator assigned to this node!");
            System.exit(1);
        }
        if (indentation < 2) {
            SkriptParser.exitNode().acceptLine(line, indentation);
            return;
        }

        ActionParser.acceptLine(this, generator, line);
    }

    @Override
    public CommandTriggerNode initialize(Fragment line) {
        return this;
    }
}
