package de.craftery.parser.structure;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.parser.SkriptParser;
import de.craftery.writer.command.CommandGenerator;
import lombok.Setter;
import org.bukkit.Material;

import java.util.logging.Level;

public class CommandTriggerNode extends StructureNode {
    @Setter
    private CommandGenerator generator;

    @Override
    public void acceptLine(Fragment line, int indentation) {
        if (indentation > 2) {
            Main.log(Level.WARNING, "CommandTriggerNode", "Command Trigger Fields must be indented two times!");
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

        if (line.test("give")) {
            line.consume();
            this.give(line);
        }

    }

    private void give(Fragment fragment) {
        fragment.trim();

        String playerVariableName;
        String itemStackVariableName;
        int amount;

        if (fragment.test("player")) {
            fragment.consume();
            playerVariableName = "player";
            fragment.trim();
        } else {
            this.reportUnknownToken(fragment.getContents(), fragment.nextToken(), 0);
            System.exit(1);
            return;
        }

        if (fragment.testInt()) {
            amount = fragment.consumeInt();

            fragment.trim();
        } else {
            this.reportUnknownToken(fragment.getContents(), fragment.nextToken(), 0);
            System.exit(1);
            return;
        }

        Material material = fragment.parseItem();
        if (material == null) {
            this.reportUnknownToken(fragment.getContents(), fragment.nextToken(), 0);
            System.exit(1);
        }
        if (!fragment.isEmpty()) {
            this.reportUnknownToken(fragment.getContents(), fragment.nextToken(), 0);
            System.exit(1);
        }

        itemStackVariableName = "new ItemStack(Material." + material.name() + ", " + amount + ")";

        generator.requireImport("org.bukkit.inventory.ItemStack");
        generator.requireImport("org.bukkit.Material");

        generator.addBodyLine(playerVariableName + ".getInventory().addItem(" + itemStackVariableName + ");");
    }

    @Override
    public CommandTriggerNode initialize(Fragment line) {
        return this;
    }
}
