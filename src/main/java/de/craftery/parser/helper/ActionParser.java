package de.craftery.parser.helper;

import de.craftery.Fragment;
import de.craftery.parser.structure.StructureNode;
import de.craftery.writer.command.CommandGenerator;
import org.bukkit.Material;

public class ActionParser {
    public static void acceptLine(StructureNode parent, CommandGenerator generator, Fragment line) {
        if (line.test("give")) {
            line.consume();
            line.trim();
            parseGiveAction(parent, generator, line);
        } else if (line.test("send")) {
            line.consume();
            line.trim();
            parseSendAction(parent, generator, line);
        } else {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
    }

    private static void parseSendAction(StructureNode parent, CommandGenerator generator, Fragment line) {
        String targetVariable;
        String messageComponentVariable;

        String message;
        if (line.testString()) {
            message = line.consumeString();
            line.trim();
        } else {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return;
        }
        messageComponentVariable = buildMessageComponent(generator, message);

        if (!line.test("to")) {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return;
        }
        line.consume();
        line.trim();

        targetVariable = parseTargetVariable(generator, parent, line);

        if (!line.isEmpty()) {
            parent.reportUnknownToken(line.getContents(), line.nextToken(), 0);
            System.exit(1);
        }

        generateSendAction(generator, targetVariable, messageComponentVariable);
    }

    private static String buildMessageComponent(CommandGenerator generator, String original) {
        generator.requireImport("net.kyori.adventure.text.Component");

        String testPlayerNameReplace = original.replaceAll("%player%", "\" + player.getName() + \"");
        if (!testPlayerNameReplace.equals(original)) {
            original = testPlayerNameReplace;
            generator.setOnlyExecutableByPlayers();
        }

        return "Component.text(\"" + original + "\")";
    }

    private static void generateSendAction(CommandGenerator generator, String targetVariable, String messageComponentVariable) {
        generator.addBodyLine(targetVariable + ".sendMessage(" + messageComponentVariable + ");");
    }

    private static void parseGiveAction(StructureNode parent, CommandGenerator generator, Fragment fragment) {
        String playerVariableName;
        String itemStackVariableName;
        int amount;

        playerVariableName = parseTargetVariable(generator, parent, fragment);

        if (fragment.testInt()) {
            amount = fragment.consumeInt();

            fragment.trim();
        } else {
            parent.reportUnknownToken(fragment.getContents(), fragment.nextToken(), 0);
            System.exit(1);
            return;
        }

        Material material = fragment.parseItem();
        if (material == null) {
            parent.reportUnknownToken(fragment.getContents(), fragment.nextToken(), 0);
            System.exit(1);
        }
        if (!fragment.isEmpty()) {
            parent.reportUnknownToken(fragment.getContents(), fragment.nextToken(), 0);
            System.exit(1);
        }

        itemStackVariableName = createItemStack(material, amount);

        generateGiveAction(generator, playerVariableName, itemStackVariableName);
    }

    private static String parseTargetVariable(CommandGenerator generator, StructureNode parent, Fragment fragment) {
        if (fragment.test("player")) {
            generator.setOnlyExecutableByPlayers();
            fragment.consume();
            fragment.trim();
            return "player";
        } else {
            parent.reportUnknownToken(fragment.getContents(), fragment.nextToken(), 0);
            System.exit(1);
            return null;
        }
    }

    private static String createItemStack(Material material, int amount) {
        return "new ItemStack(Material." + material.name() + ", " + amount + ")";
    }

    private static void generateGiveAction(CommandGenerator generator, String playerVariableName, String itemStackVariableName) {
        generator.requireImport("org.bukkit.inventory.ItemStack");
        generator.requireImport("org.bukkit.Material");

        generator.addBodyLine(playerVariableName + ".getInventory().addItem(" + itemStackVariableName + ");");
    }
}
