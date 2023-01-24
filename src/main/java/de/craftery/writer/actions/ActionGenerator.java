package de.craftery.writer.actions;

import de.craftery.Main;
import de.craftery.parser.structure.ActionNode;
import de.craftery.writer.javaFile.JavaFileGenerator;
import de.craftery.writer.javaFile.RawMethodSection;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public abstract class ActionGenerator extends JavaFileGenerator {
    @Getter
    @Setter
    private ActionNode node;
    private final List<String> bodyLines = new ArrayList<>();

    public void build() {
        for (String line : bodyLines) {
            this.getBodySection().addLine(line);
        }
    }

    public void addBodyLine(String line) {
        int indent = node.getMaxIndentation() - this.node.getBaseIndentation();
        String lineBuilder = "    ".repeat(Math.max(0, indent)) + line;
        bodyLines.add(lineBuilder);
    }

    public void setCommandCooldown(long cooldown) {
        Main.exit("Command cooldown is only available for commands!");
    }

    public void setOnlyExecutableByPlayers() {
        Main.exit("Only executable by players is only available for commands!");
    }

    public void requireRightClick() {
        Main.exit("Right click is only available for click event!");
    }

    public void requireLeftClick() {
        Main.exit("Left click is only available for click event on blocks!");
    }

    public abstract Type getType();
    protected abstract RawMethodSection getBodySection();
    public enum Type {
        COMMAND,
        ENCHANT_EVENT,
        PREPARE_ENCHANT_EVENT,
        CHAT_EVENT
    }
}
