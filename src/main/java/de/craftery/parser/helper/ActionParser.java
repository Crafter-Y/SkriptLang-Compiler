package de.craftery.parser.helper;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.writer.actions.ActionGenerator;
import de.craftery.writer.core.FormatterGenerator;
import de.craftery.writer.core.MainGenerator;
import org.bukkit.Material;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionParser {
    private final ActionGenerator generator;
    public ActionParser(ActionGenerator generator) {
        this.generator = generator;
    }
    public void acceptLine(Fragment line) {
        if (line.test("give")) {
            line.consume();
            parseGiveAction(line);
        } else if (line.test("send")) {
            line.consume();
            parseSendAction(line);
        } else if (line.test("message")) {
            line.consume();
            parseSendAction(line);
        } else if (line.test("set")) {
            line.consume();
            parseSetAction(line);
        } else if (line.test("clear")) {
            line.consume();
            parseClearAction(line);
        } else if (line.test("if")) {
            line.consume();
            parseIfCondition(line, false);
        } else if (line.test("else if")) {
            line.consume();
            parseIfCondition(line, true);
        }  else if (line.test("else")) {
            line.consume();
            generateElseCondition(line);
        } else if (line.test("teleport")) {
            line.consume();
            parseTeleportAction(line);
        } else if (line.test("broadcast")) {
            line.consume();
            parseBroadcastAction(line);
        } else {
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
    }

    private void parseBroadcastAction(Fragment line) {
        if (!line.testString()) {
            Main.log(Level.WARNING, "ActionParser", "String expected here!");
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
        String parsedString = replaceKnownInlineStringVariables(line.consumeDelimitedExpression(), false);

        if (!line.isEmpty()) {
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }

        generateBroadcastAction(parsedString);
    }

    private void generateBroadcastAction(String broadcastMessage) {
        String messageComponent = buildMessageComponent(broadcastMessage);

        this.generator.requireImport("org.bukkit.Bukkit");
        this.generator.addBodyLine("Bukkit.broadcast(" + messageComponent + ");");
    }

    private void parseTeleportAction(Fragment line) {
        String targetVariable = parseTargetVariable(line);

        if (!line.test("to")) {
            Main.log(Level.WARNING, "ActionParser", "Expected 'to' here");
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return;
        }
        line.consume();

        if (!line.testByDelimiters('{', '}')) { // starts with variable
            Main.log(Level.WARNING, "ActionParser", "Expected a variable here");
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return;
        }
        String variable = line.consumeDelimitedExpression();
        String parsedKey = replaceKnownInlineStringVariables(variable, false);

        String locationVariable = "(Location) " + getVariable(parsedKey);
        this.generator.requireImport("org.bukkit.Location");

        generateTeleportAction(targetVariable, locationVariable);
    }

    private void generateTeleportAction(String targetVariable, String locationVariable) {
        this.generator.addBodyLine(targetVariable + ".teleport(" + locationVariable + ");");
    }

    private void generateElseCondition(Fragment line) {
        if (line.test(":")) {
            line.consume();
        }

        if (!line.isEmpty()) {
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }

        this.generator.addBodyLine("else {");
        this.generator.getNode().setMaxIndentation(this.generator.getNode().getMaxIndentation() + 1);
    }

    private void parseIfCondition(Fragment line, boolean isElseIf) {
        String condition;
        if (line.testByDelimiters('{', '}')) { // starts with variable
            String variable = line.consumeDelimitedExpression();
            String parsedKey = replaceKnownInlineStringVariables(variable, false);
            String firstPart = getVariable(parsedKey);
            condition = parseVariableFirstConditionalExpression(firstPart, line);
        } else if (line.test("argument")) {
            line.consume();
            if (!line.testInt()) {
                Main.log(Level.WARNING, "ActionParser", "Number of argument expected!");
                this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
                System.exit(1);
                return;
            }
            Integer argumentNumber = line.consumeInt();
            String firstPart = "argument" + argumentNumber;
            condition = parseVariableFirstConditionalExpression(firstPart, line);
        } else {
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return;
        }

        if (line.test(":")) {
            line.consume();
        }

        if (!line.isEmpty()) {
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }

        if (isElseIf) {
            this.generator.addBodyLine("else if (" + condition + ") {");
        } else {
            this.generator.addBodyLine("if (" + condition + ") {");
        }

        this.generator.getNode().setMaxIndentation(this.generator.getNode().getMaxIndentation() + 1);
    }

    private String parseVariableFirstConditionalExpression(String firstPartOfEquation, Fragment line) {
        String result;

        if (line.test("is not set")) {
            line.consume();
            result = firstPartOfEquation + " == null";
        } else if (line.test("is set")) {
            line.consume();
            result = firstPartOfEquation + " != null";
        } else if (line.test("is not")) {
            line.consume();
            result = parseEqualityExpression(firstPartOfEquation, line, false);
        } else if (line.test("is") || line.test("=")) {
            line.consume();
            result = parseEqualityExpression(firstPartOfEquation, line, true);
        } else {
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return "";
        }
        return result;
    }

    private String parseEqualityExpression(String firstVariableToCompare, Fragment followingLine, boolean isEqual) {
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
                String parsedString = replaceKnownInlineStringVariables(followingLine.consumeDelimitedExpression(), false);
                result.append(" \"").append(parsedString).append("\"");
            } else {
                this.generator.getNode().reportUnknownToken(followingLine, followingLine.nextToken(), 0);
                System.exit(1);
            }

        }
        return result.toString();
    }

    private void parseSetAction(Fragment line) {
        if (!line.testByDelimiters('{', '}')) {
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
        String variable = line.consumeDelimitedExpression();
        String parsedKey = replaceKnownInlineStringVariables(variable, false);

        if (!line.test("to")) {
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
        line.consume();

        String value;
        if (line.test("location of player")) {
            this.generator.setOnlyExecutableByPlayers();
            value = "player.getLocation()";
        } else if (line.test("location of block at location of player")) {
            this.generator.setOnlyExecutableByPlayers();
            value = "player.getLocation().getBlock().getLocation()";
        } else if (line.testString()) {
            String parsedString = replaceKnownInlineStringVariables(line.consumeDelimitedExpression(), false);
            value = "\" " + parsedString + "\"";
        } else {
            Main.log(Level.WARNING, "ActionParser", "Unknown set value: " + line.getContents());
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return;
        }

        assignVariable(parsedKey, value);
    }

    private void parseClearAction(Fragment line) {
        if (!line.testByDelimiters('{', '}')) {
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
        String variable = line.consumeDelimitedExpression();
        String parsedKey = replaceKnownInlineStringVariables(variable, false);

        clearVariable(parsedKey);
    }

    private String getVariable(String key) {
        MainGenerator.getInstance().requireVariableStore();
        this.generator.requireImport("de.craftery.autogenerated.Main");

        return "Main.getVariable(\"" + key + "\")";
    }

    private void assignVariable(String key, String objectVariable) {
        MainGenerator.getInstance().requireVariableStore();
        this.generator.requireImport("de.craftery.autogenerated.Main");

        this.generator.addBodyLine("Main.setVariable(\"" + key + "\", " + objectVariable + ");");
    }

    private void clearVariable(String key) {
        MainGenerator.getInstance().requireVariableStore();
        this.generator.requireImport("de.craftery.autogenerated.Main");

        this.generator.addBodyLine("Main.deleteVariable(\"" + key + "\");");
    }

    // https://skripthub.net/docs/?id=1130
    private void parseSendAction(Fragment line) {
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
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return;
        }
        messageComponentVariable = buildMessageComponent(message);

        if (line.isEmpty()) {
            this.generator.setOnlyExecutableByPlayers();
            generateSendAction("player", messageComponentVariable);
            return;
        }

        if (!line.test("to")) {
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return;
        }
        line.consume();

        targetVariable = parseTargetVariable(line);

        if (!line.isEmpty()) {
            this.generator.getNode().reportUnknownToken(line.getContents(), line.nextToken(), 0);
            System.exit(1);
        }

        generateSendAction(targetVariable, messageComponentVariable);
    }

    private String replaceKnownInlineStringVariables(String original, boolean isOption) {
        String testPlayerNameReplace = original.replaceAll("%player%", "\" + player.getName() + \"");
        if (!testPlayerNameReplace.equals(original)) {
            original = testPlayerNameReplace;
            this.generator.setOnlyExecutableByPlayers();
        }

        String testPlayerUuid = original.replaceAll("%uuid of player%", "\" + player.getUniqueId().toString() + \"");
        if (!testPlayerUuid.equals(original)) {
            original = testPlayerUuid;
            this.generator.setOnlyExecutableByPlayers();
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
                String replacement = replaceKnownInlineStringVariables(option, true);
                original = original.replace(target, replacement);
            }
        }

        String testVariableInject = original.replaceAll("%\\{([a-zA-Z]+)}%", "\" + Formatter.formatUnknown(Main.getVariable(\"$1\")) + \"");
        if (!testVariableInject.equals(original)) {
            MainGenerator.getInstance().requireVariableStore();
            FormatterGenerator.getInstance().requireUnknownFormatter();
            this.generator.requireImport("de.craftery.autogenerated.Formatter");
            original = testVariableInject;
        }

        String testLocationOfPlayer = original.replaceAll("%location of player%", "\" + Formatter.formatLocation(player.getLocation()) + \"");
        if (!testLocationOfPlayer.equals(original)) {
            original = testLocationOfPlayer;
            this.generator.setOnlyExecutableByPlayers();
            FormatterGenerator.getInstance().requireLocationFormatter();
            this.generator.requireImport("de.craftery.autogenerated.Formatter");
        }

        return original;
    }

    private String buildMessageComponent(String original) {
        this.generator.requireImport("net.kyori.adventure.text.Component");

        original = replaceKnownInlineStringVariables(original, false);

        return "Component.text(\"" + original + "\")";
    }

    private void generateSendAction(String targetVariable, String messageComponentVariable) {
        this.generator.addBodyLine(targetVariable + ".sendMessage(" + messageComponentVariable + ");");
    }

    private void parseGiveAction(Fragment fragment) {
        String playerVariableName;
        String itemStackVariableName;
        int amount;

        playerVariableName = parseTargetVariable(fragment);

        if (fragment.testInt()) {
            amount = fragment.consumeInt();
        } else {
            this.generator.getNode().reportUnknownToken(fragment.getContents(), fragment.nextToken(), 0);
            System.exit(1);
            return;
        }

        Material material = fragment.parseItem();
        if (material == null) {
            this.generator.getNode().reportUnknownToken(fragment.getContents(), fragment.nextToken(), 0);
            System.exit(1);
        }
        if (!fragment.isEmpty()) {
            this.generator.getNode().reportUnknownToken(fragment.getContents(), fragment.nextToken(), 0);
            System.exit(1);
        }

        itemStackVariableName = createItemStack(material, amount);

        generateGiveAction(playerVariableName, itemStackVariableName);
    }

    private String parseTargetVariable(Fragment fragment) {
        if (fragment.test("player")) {
            this.generator.setOnlyExecutableByPlayers();
            fragment.consume();
            return "player";
        } else if (fragment.test("argument")) {
            fragment.consume();
            if (!fragment.testInt()) {
                Main.log(Level.WARNING, "ActionParser", "Number of argument expected!");
                this.generator.getNode().reportUnknownToken(fragment, fragment.nextToken(), 0);
                System.exit(1);
                return "";
            }
            Integer argumentNumber = fragment.consumeInt();
            this.generator.requireImport("org.bukkit.Bukkit");
            return "Bukkit.getPlayer(argument"+ argumentNumber +")";
        } else {
            this.generator.getNode().reportUnknownToken(fragment.getContents(), fragment.nextToken(), 0);
            System.exit(1);
            return null;
        }
    }

    private String createItemStack(Material material, int amount) {
        return "new ItemStack(Material." + material.name() + ", " + amount + ")";
    }

    private void generateGiveAction(String playerVariableName, String itemStackVariableName) {
        this.generator.requireImport("org.bukkit.inventory.ItemStack");
        this.generator.requireImport("org.bukkit.Material");

        this.generator.addBodyLine(playerVariableName + ".getInventory().addItem(" + itemStackVariableName + ");");
    }
}
