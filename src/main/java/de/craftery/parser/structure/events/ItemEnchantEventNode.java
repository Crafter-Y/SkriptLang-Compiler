package de.craftery.parser.structure.events;

import de.craftery.parser.structure.ActionNode;
import de.craftery.writer.actions.events.ItemEnchantGenerator;

public class ItemEnchantEventNode extends ActionNode {
    public ItemEnchantEventNode() {
        super(1);
        ItemEnchantGenerator generator = new ItemEnchantGenerator();
        this.setGenerator(generator);
        generator.setNode(this);
    }
}
