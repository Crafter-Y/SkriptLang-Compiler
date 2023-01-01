package de.craftery.parser.structure.events;

import de.craftery.parser.structure.ActionNode;
import de.craftery.writer.actions.events.PrepareItemEnchantGenerator;

public class PrepareItemEnchantEventNode extends ActionNode {
    public PrepareItemEnchantEventNode() {
        super(1);
        PrepareItemEnchantGenerator generator = new PrepareItemEnchantGenerator();
        this.setGenerator(generator);
        generator.setNode(this);
    }
}
