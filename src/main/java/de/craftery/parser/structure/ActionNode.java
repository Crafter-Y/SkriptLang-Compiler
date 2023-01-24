package de.craftery.parser.structure;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.parser.SkriptParser;
import de.craftery.parser.helper.ActionParser;
import de.craftery.writer.actions.ActionGenerator;
import lombok.Getter;
import lombok.Setter;

public class ActionNode extends StructureNode {
    @Getter
    @Setter
    private int baseIndentation;
    @Getter
    @Setter
    private ActionGenerator generator;
    public ActionNode(int baseIndentation) {
        this.baseIndentation = baseIndentation;
        this.setMaxIndentation(baseIndentation);
    }

    @Override
    public void acceptLine(Fragment line, int indentation) {
        if (indentation < this.getMaxIndentation()) {
            while (this.getMaxIndentation() > indentation && this.getMaxIndentation() > this.baseIndentation) {
                this.setMaxIndentation(this.getMaxIndentation() - 1);
                this.generator.addBodyLine("}");
            }
        }

        if (indentation == 0) {
            SkriptParser.exitNode().acceptLine(line, indentation);
            this.generator.build();
            return;
        }

        if (indentation > this.getMaxIndentation()) {
            Main.warn("Indentation error!");
            Main.warn("Expected maximum indentation: " + this.getMaxIndentation());
            Main.warn("Got: " + indentation);
            System.exit(1);
        }
        if (generator == null) {
            Main.exit("There is no generator assigned to this node!");
        }
        if (indentation < this.baseIndentation) {
            SkriptParser.exitNode().acceptLine(line, indentation);
            return;
        }

        new ActionParser(generator).acceptLine(line);
    }

    @Override
    public ActionNode initialize(Fragment line) {
        return this;
    }
}
