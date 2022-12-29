package de.craftery.parser.helper;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.parser.structure.StructureNode;
import de.craftery.writer.command.CommandGenerator;
import de.craftery.writer.core.FormatterGenerator;
import de.craftery.writer.core.MainGenerator;
import org.bukkit.Material;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            parseSendAction(parent, generator, line);
        } else if (line.test("set")) {
            line.consume();
            parseSetAction(parent, generator, line);
        } else if (line.test("clear")) {
            line.consume();
            parseClearAction(parent, generator, line);
        } else if (line.test("if")) {
            line.consume();
            parseIfCondition(parent, generator, line, false);
        } else if (line.test("else if")) {
            line.consume();
            parseIfCondition(parent, generator, line, true);
        }  else if (line.test("else")) {
            line.consume();
            generateElseCondition(parent, generator, line);
        } else if (line.test("teleport")) {
            line.consume();
            parseTeleportAction(parent, generator, line);
        } else if (line.test("broadcast")) {
            line.consume();
            parseBroadcastAction(parent, generator, line);
        } else {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
    }

    private static void parseBroadcastAction(StructureNode parent, CommandGenerator generator, Fragment line) {
        if (!line.testString()) {
            Main.log(Level.WARNING, "ActionParser", "String expected here!");
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
        String parsedString = replaceKnownInlineStringVariables(generator, line.consumeDelimitedExpression(), false);

        if (!line.isEmpty()) {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }

        generateBroadcastAction(generator, parsedString);
    }

    private static void generateBroadcastAction(CommandGenerator generator, String broadcastMessage) {
        String messageComponent = buildMessageComponent(generator, broadcastMessage);

        generator.requireImport("org.bukkit.Bukkit");
        generator.addBodyLine("Bukkit.broadcast(" + messageComponent + ");");
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
        String parsedKey = replaceKnownInlineStringVariables(generator, variable, false);

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

    private static void parseIfCondition(StructureNode parent, CommandGenerator generator, Fragment line, boolean isElseIf) {
        String condition;
        if (line.testByDelimiters('{', '}')) { // starts with variable
            String variable = line.consumeDelimitedExpression();
            String parsedKey = replaceKnownInlineStringVariables(generator, variable, false);
            String firstPart = getVariable(generator, parsedKey);
            condition = parseVariableFirstConditionalExpression(parent, firstPart, generator, line);
        } else if (line.test("argument")) {
            line.consume();
            if (!line.testInt()) {
                Main.log(Level.WARNING, "ActionParser", "Number of argument expected!");
                parent.reportUnknownToken(line, line.nextToken(), 0);
                System.exit(1);
                return;
            }
            Integer argumentNumber = line.consumeInt();
            String firstPart = "argument" + argumentNumber;
            condition = parseVariableFirstConditionalExpression(parent, firstPart, generator, line);
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

        if (isElseIf) {
            generator.addBodyLine("else if (" + condition + ") {");
        } else {
            generator.addBodyLine("if (" + condition + ") {");
        }

        parent.setMaxIndentation(parent.getMaxIndentation() + 1);
    }

    private static String parseVariableFirstConditionalExpression(StructureNode parent, String firstPartOfEquation, CommandGenerator generator, Fragment line) {
        String result;

        if (line.test("is not set")) {
            line.consume();
            result = firstPartOfEquation + " == null";
        } else if (line.test("is set")) {
            line.consume();
            result = firstPartOfEquation + " != null";
        } else if (line.test("is not")) {
            line.consume();
            result = parseEqualityExpression(parent, generator, firstPartOfEquation, line, false);
        } else if (line.test("is") || line.test("=")) {
            line.consume();
            result = parseEqualityExpression(parent, generator, firstPartOfEquation, line, true);
        } else {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return "";
        }
        return result;
    }

    private static String parseEqualityExpression(StructureNode parent, CommandGenerator generator, String firstVariableToCompare, Fragment followingLine, boolean isEqual) {
        StringBuilder result = new StringBuilder();

        boolean first = true;
        while (!followingLine.isEmpty()) {
            if (followingLine.test(":")) {
                break;
            }
            if (followingLine.test(",") || followingLine.test("or")) {
                followingLine.consume();
                continue;
            }

            if (!first) {
                result.append(" || ");
            } else {
                first = false;
            }

            result.append(firstVariableToCompare);
            if (isEqual) {
                result.append(" ==");
            } else {
                result.append(" !=");
            }


            if (followingLine.testString()) {
                String parsedString = replaceKnownInlineStringVariables(generator, followingLine.consumeDelimitedExpression(), false);
                result.append(" \"").append(parsedString).append("\"");
            } else {
                parent.reportUnknownToken(followingLine, followingLine.nextToken(), 0);
                System.exit(1);
            }

        }
        return result.toString();
    }

    private static void parseSetAction(StructureNode parent, CommandGenerator generator, Fragment line) {
        if (!line.testByDelimiters('{', '}')) {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
        String variable = line.consumeDelimitedExpression();
        String parsedKey = replaceKnownInlineStringVariables(generator, variable, false);

        if (!line.test("to")) {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
        line.consume();

        String value;
        if (line.test("location of player")) {
            generator.setOnlyExecutableByPlayers();
            value = "player.getLocation()";
        } else if (line.testString()) {
            String parsedString = replaceKnownInlineStringVariables(generator, line.consumeDelimitedExpression(), false);
            value = "\" " + parsedString + "\"";
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
        String parsedKey = replaceKnownInlineStringVariables(generator, variable, false);

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

    // https://skripthub.net/docs/?id=1130
    private static void parseSendAction(StructureNode parent, CommandGenerator generator, Fragment line) {
        // (message|send [message[s]]) %objects% [to %commandsenders%] [from %player%]

        String targetVariable;
        String messageComponentVariable;

        if (line.test("message")) {
            line.consume();
        } else if (line.test("messages")) {
            line.consume();
        }

        String message;
        if (line.testString()) {
            message = line.consumeDelimitedExpression();
        } else {
            parent.reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return;
        }
        messageComponentVariable = buildMessageComponent(generator, message);

        if (line.isEmpty()) {
            generator.setOnlyExecutableByPlayers();
            generateSendAction(generator, "player", messageComponentVariable);
            return;
        }

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

    private static String replaceKnownInlineStringVariables(CommandGenerator generator, String original, boolean isOption) {
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

        String testArgs = original.replaceAll("%arg[-\\s](\\d)%", "\" + argument$1 + \"");
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

        String testAndCode = original.replaceAll("&([0-9a-fk-or])", "§$1");
        if (!testAndCode.equals(original)) {
            original = testAndCode;
        }

        if (!isOption) {
            Matcher mat = Pattern.compile("\\{@([a-zA-Z]+)}").matcher(original);
            while (mat.find()) {
                String target = mat.group(0);
                String option = Options.getOption(target.substring(2, target.length() - 1));
                String replacement = replaceKnownInlineStringVariables(generator, option, true);
                original = original.replace(target, replacement);
            }
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

        original = replaceKnownInlineStringVariables(generator, original, false);

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
        } else if (fragment.test("argument")) {
            fragment.consume();
            if (!fragment.testInt()) {
                Main.log(Level.WARNING, "ActionParser", "Number of argument expected!");
                parent.reportUnknownToken(fragment, fragment.nextToken(), 0);
                System.exit(1);
                return "";
            }
            Integer argumentNumber = fragment.consumeInt();
            generator.requireImport("org.bukkit.Bukkit");
            return "Bukkit.getPlayer(argument"+ argumentNumber +")";
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
