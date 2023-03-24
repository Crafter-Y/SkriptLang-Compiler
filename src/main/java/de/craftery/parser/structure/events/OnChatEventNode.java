package de.craftery.parser.structure.events;

import de.craftery.parser.structure.ActionNode;
import de.craftery.writer.actions.events.OnChatGenerator;

public class OnChatEventNode extends ActionNode {
    public OnChatEventNode() {
        super(1);
        OnChatGenerator generator = new OnChatGenerator();
        this.setGenerator(generator);
        generator.setNode(this);
    }
}
