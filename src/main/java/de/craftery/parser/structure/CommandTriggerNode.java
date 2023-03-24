package de.craftery.parser.structure;

import de.craftery.Fragment;

public class CommandTriggerNode extends ActionNode {
    public CommandTriggerNode() {
        super(2);
    }

    @Override
    public CommandTriggerNode initialize(Fragment line) {
        return this;
    }
}
