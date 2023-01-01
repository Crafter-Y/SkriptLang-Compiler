package de.craftery.parser.structure.events;

import de.craftery.Fragment;
import de.craftery.parser.structure.ActionNode;
import de.craftery.writer.actions.events.OnClickGenerator;

public class OnClickEventNode extends ActionNode {
    public OnClickEventNode() {
        super(1);
        OnClickGenerator generator = new OnClickGenerator();
        this.setGenerator(generator);
        generator.setNode(this);
    }

    // https://skripthub.net/docs/?id=1094

    // What gets here:
    // [(right|left)] [on %entitydata/itemtype%] [(with|using|holding) %itemtype%]
    // [(right|left)] (with|using|holding) %itemtype% on %entitydata/itemtype%
    @Override
    public OnClickEventNode initialize(Fragment line) {
        if (line.test("right")) {
            line.consume();
            this.getGenerator().requireRightClick();
        } else if (line.test("left")) {
            line.consume();
            this.getGenerator().requireLeftClick();
        }

        if (line.test(":")) {
            line.consume();
        }

        if (!line.isEmpty()) {
            this.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }

        return this;
    }
}
