package de.craftery.parser.helper;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.parser.structure.StructureNode;
import de.craftery.writer.command.CommandGenerator;
import de.craftery.writer.core.FormatterGenerator;
import de.craftery.writer.core.MainGenerator;
import org.bukkit.Material;

import java.util.logging.Level;

public class ActionParser {
    public static void acceptLine(StructureNode parent, CommandGenerator generator, Fragment line) {
        if (line.test("give")) {
            line.consume();
            parseGiveAction(parent, generator, line);
        } else if (line.test("send")) {
            line.consume();
            parseSendAction(parent, generator, line);
        } else if (line.test("message")) {
            line.consume();
            parseMessageAction(parent, generator, line);
        } else if (line.test("set")) {
            line.consume();
            parseSetAction(parent, generator, line);
        } else if (line.test("clear")) {
            line.consume();
            parseClearAction(parent, generator, line);
        } else if (line.test("if")) {
            line.consume();
            parseIfCondition(parent, generator, line);
        } else if (line.test("else")) {
            line.consume();
            generateElseCondition(parent, generator, line);
        } else if (line.test("teleport")) {
            line.consume();
            parseTeleportAction(parent, generator, line);
        } else {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
    }

    private static void parseTeleportAction(StructureNode parent, CommandGenerator generator, Fragment line) {
        String targetVariable = parseTargetVariable(generator, parent, line);

        if (!line.test("to")) {
            Main.log(Level.WARNING, "ActionParser", "Expected 'to' here");
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return;
        }
        line.consume();

        if (!line.testByDelimiters('{', '}')) { // starts with variable
            Main.log(Level.WARNING, "ActionParser", "Expected a variable here");
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return;
        }
        String variable = line.consumeDelimitedExpression();
        String parsedKey = replaceKnownInlineStringVariables(generator, variable);

        String locationVariable = "(Location) " + getVariable(generator, parsedKey);
        generator.requireImport("org.bukkit.Location");

        generateTeleportAction(generator, targetVariable, locationVariable);
    }

    private static void generateTeleportAction(CommandGenerator generator, String targetVariable, String locationVariable) {
        generator.addBodyLine(targetVariable + ".teleport(" + locationVariable + ");");
    }

    private static void generateElseCondition(StructureNode parent, CommandGenerator generator, Fragment line) {
        if (line.test(":")) {
            line.consume();
        }

        if (!line.isEmpty()) {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }

        generator.addBodyLine("else {");
        parent.setMaxIndentation(parent.getMaxIndentation() + 1);
    }

    private static void parseIfCondition(StructureNode parent, CommandGenerator generator, Fragment line) {
        String condition;
        if (line.testByDelimiters('{', '}')) { // starts with variable
            String variable = line.consumeDelimitedExpression();
            String parsedKey = replaceKnownInlineStringVariables(generator, variable);
            condition = parseVariableFirstConditionalExpression(parent, parsedKey,generator, line);
        } else {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return;
        }

        if (line.test(":")) {
            line.consume();
        }

        if (!line.isEmpty()) {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }

        generator.addBodyLine("if (" + condition + ") {");
        parent.setMaxIndentation(parent.getMaxIndentation() + 1);
    }

    private static String parseVariableFirstConditionalExpression(StructureNode parent, String variable, CommandGenerator generator, Fragment line) {
        StringBuilder expressionBuilder = new StringBuilder();

        expressionBuilder.append(getVariable(generator, variable));

        if (line.test("is not set")) {
            line.consume();
            expressionBuilder.append(" == null");
        } else {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
        return expressionBuilder.toString();
    }

    private static void parseSetAction(StructureNode parent, CommandGenerator generator, Fragment line) {
        if (!line.testByDelimiters('{', '}')) {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
        String variable = line.consumeDelimitedExpression();
        String parsedKey = replaceKnownInlineStringVariables(generator, variable);

        if (!line.test("to")) {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
        line.consume();

        String value;
        if (line.test("location of player")) {
            generator.setOnlyExecutableByPlayers();
            value = "player.getLocation()";
        } else {
            Main.log(Level.WARNING, "ActionParser", "Unknown set value: " + line.getContents());
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return;
        }

        assignVariable(generator, parsedKey, value);
    }

    private static void parseClearAction(StructureNode parent, CommandGenerator generator, Fragment line) {
        if (!line.testByDelimiters('{', '}')) {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
        String variable = line.consumeDelimitedExpression();
        String parsedKey = replaceKnownInlineStringVariables(generator, variable);

        clearVariable(generator, parsedKey);
    }

    private static String getVariable(CommandGenerator generator, String key) {
        MainGenerator.getInstance().requireVariableStore();
        generator.requireImport("de.craftery.autogenerated.Main");

        return "Main.getVariable(\"" + key + "\")";
    }

    private static void assignVariable(CommandGenerator generator, String key, String objectVariable) {
        MainGenerator.getInstance().requireVariableStore();
        generator.requireImport("de.craftery.autogenerated.Main");

        generator.addBodyLine("Main.setVariable(\"" + key + "\", " + objectVariable + ");");
    }

    private static void clearVariable(CommandGenerator generator, String key) {
        MainGenerator.getInstance().requireVariableStore();
        generator.requireImport("de.craftery.autogenerated.Main");

        generator.addBodyLine("Main.deleteVariable(\"" + key + "\");");
    }

    private static void parseSendAction(StructureNode parent, CommandGenerator generator, Fragment line) {
        String targetVariable;
        String messageComponentVariable;

        String message;
        if (line.testString()) {
            message = line.consumeDelimitedExpression();
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

        targetVariable = parseTargetVariable(generator, parent, line);

        if (!line.isEmpty()) {
            parent.reportUnknownToken(line.getContents(), line.nextToken(), 0);
            System.exit(1);
        }

        generateSendAction(generator, targetVariable, messageComponentVariable);
    }

    private static void parseMessageAction(StructureNode parent, CommandGenerator generator, Fragment line) {
        String messageComponentVariable;

        String message;
        if (line.testString()) {
            message = line.consumeDelimitedExpression();
        } else {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return;
        }
        messageComponentVariable = buildMessageComponent(generator, message);

        if (!line.isEmpty()) {
            parent.reportUnknownToken(line.getContents(), line.nextToken(), 0);
            System.exit(1);
        }

        generator.setOnlyExecutableByPlayers();
        generateSendAction(generator, "player", messageComponentVariable);
    }

    private static String replaceKnownInlineStringVariables(CommandGenerator generator, String original) {
        String testPlayerNameReplace = original.replaceAll("%player%", "\" + player.getName() + \"");
        if (!testPlayerNameReplace.equals(original)) {
            original = testPlayerNameReplace;
            generator.setOnlyExecutableByPlayers();
        }

        String testPlayerUuid = original.replaceAll("%uuid of player%", "\" + player.getUniqueId().toString() + \"");
        if (!testPlayerUuid.equals(original)) {
            original = testPlayerUuid;
            generator.setOnlyExecutableByPlayers();
        }

        String testArgs = original.replaceAll("%arg-(\\d)%", "\" + args[$1] + \"");
        if (!testArgs.equals(original)) {
            original = testArgs;
        }

        String testGreen = original.replaceAll("<green>", "§a");
        if (!testGreen.equals(original)) {
            original = testGreen;
        }

        String testReset = original.replaceAll("<reset>", "§r");
        if (!testReset.equals(original)) {
            original = testReset;
        }

        String testGrey = original.replaceAll("<grey>", "§7");
        if (!testGrey.equals(original)) {
            original = testGrey;
        }

        String testLocationOfPlayer = original.replaceAll("%location of player%", "\" + Formatter.formatLocation(player.getLocation()) + \"");
        if (!testLocationOfPlayer.equals(original)) {
            original = testLocationOfPlayer;
            generator.setOnlyExecutableByPlayers();
            FormatterGenerator.getInstance().requireLocationFormatter();
            generator.requireImport("de.craftery.autogenerated.Formatter");
        }

        return original;
    }

    private static String buildMessageComponent(CommandGenerator generator, String original) {
        generator.requireImport("net.kyori.adventure.text.Component");

        original = replaceKnownInlineStringVariables(generator, original);

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
