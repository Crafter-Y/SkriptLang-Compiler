package de.craftery.parser.structure;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.parser.SkriptParser;
import de.craftery.parser.helper.ActionParser;
import de.craftery.writer.actions.CommandGenerator;
import lombok.Setter;

import java.util.logging.Level;

public class CommandTriggerNode extends ActionNode {
    public CommandTriggerNode() {
        super(2);
    }

    @Override
    public CommandTriggerNode initialize(Fragment line) {
        return this;
    }
}
