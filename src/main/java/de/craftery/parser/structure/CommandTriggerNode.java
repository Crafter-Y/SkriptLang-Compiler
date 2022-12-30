package de.craftery.parser.structure;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.parser.SkriptParser;
import de.craftery.parser.helper.ActionParser;
import de.craftery.writer.actions.CommandGenerator;
import lombok.Setter;

import java.util.logging.Level;

public class CommandTriggerNode extends StructureNode {
    public CommandTriggerNode() {
        this.setMaxIndentation(2);
    }

    @Setter
    private CommandGenerator generator;

    @Override
    public void acceptLine(Fragment line, int indentation) {
        if (indentation < this.getMaxIndentation()) {
            while (this.getMaxIndentation() > indentation && this.getMaxIndentation() > 2) {
                this.setMaxIndentation(this.getMaxIndentation() - 1);
                generator.addBodyLine("}");
            }
        }

        if (indentation > this.getMaxIndentation()) {
            Main.log(Level.WARNING, "CommandTriggerNode", "Indentation error!");
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

        new ActionParser(generator).acceptLine(line);
    }

    @Override
    public CommandTriggerNode initialize(Fragment line) {
        return this;
    }
}
