package de.craftery.parser.structure.events;

import de.craftery.Fragment;
import de.craftery.parser.structure.ActionNode;
import de.craftery.writer.actions.events.OnClickGenerator;
import de.craftery.writer.actions.events.PlayerInteractEntityEventGenerator;

public class OnClickEventNode extends ActionNode {
    public OnClickEventNode() {
        super(1);
    }

    // https://skripthub.net/docs/?id=1094

    // What gets here:
    // [(right|left)] [on %entitydata/itemtype%] [(with|using|holding) %itemtype%]
    // [(right|left)] (with|using|holding) %itemtype% on %entitydata/itemtype%
    @Override
    public OnClickEventNode initialize(Fragment line) {
        boolean rightClickRequired = false;
        boolean leftClickRequired = false;

        if (line.test("right")) {
            line.consume();
            rightClickRequired = true;
        } else if (line.test("left")) {
            line.consume();
            leftClickRequired = true;
        }

        if (line.test("on entity")) {
            line.consume();
            this.setGenerator(new PlayerInteractEntityEventGenerator());
        } else {
            this.setGenerator(new OnClickGenerator());
        }
        this.getGenerator().setNode(this);

        if (rightClickRequired) {
            this.getGenerator().requireRightClick();
        }
        if (leftClickRequired) {
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
