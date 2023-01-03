package de.craftery.parser.helper;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.writer.actions.ActionGenerator;
import de.craftery.writer.core.FormatterGenerator;
import de.craftery.writer.core.MainGenerator;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

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
        } else if (line.test("send") || line.test("message")) {
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
        } else if (line.test("stop")) {
            line.consume();
            parseStopAction(line);
        } else if (line.test("cancel event")) {
            line.consume();
            parseCancelEventAction(line);
        } else if (line.test("execute")) {
            line.consume();
            parseExecuteAction(line);
        } else if (line.test("remove")) {
            line.consume();
            parseRemoveAction(line);
        } else {
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
    }

    // https://skripthub.net/docs/?id=1134
    private void parseRemoveAction(Fragment line) {
        // remove (all|every) %objects% from %objects%
        // (remove|subtract) %objects% from %objects%
        int moduleInt = Main.nextModuleInt();
        String conditionIfItemStack = parseComparisonBetweenVariableAndItemProperties("item" + moduleInt, line);

        if (!line.test("from")) {
            Main.log(Level.WARNING, "ActionParser", "Expected 'from' after 'remove' action");
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
        line.consume();

        String targetPlayer = parseTargetVariable(line);

        this.generator.requireImport("org.bukkit.inventory.ItemStack");

        this.generator.addBodyLine("for (ItemStack item"+ moduleInt +" : "+ targetPlayer +".getInventory().getContents()) {");
        this.generator.addBodyLine("    if ("+ conditionIfItemStack +") {");
        this.generator.addBodyLine("        "+ targetPlayer +".getInventory().removeItem(item"+ moduleInt +");");
        this.generator.addBodyLine("    }");
        this.generator.addBodyLine("}");

        if (!line.isEmpty()) {
            Main.log(Level.WARNING, "ActionParser", "Expected end of line after 'remove' action");
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
    }

    // https://skripthub.net/docs/?id=1129
    private void parseExecuteAction(Fragment line) {
        // [execute] [the] command %strings% [by %commandsenders%]
        // [execute] [the] %commandsenders% command %strings%
        // (let|make) %commandsenders% execute [[the] command] %strings%

        boolean isConsoleSender = false;

        if (line.test("the")) {
            line.consume();
        }

        if (line.test("console")) {
            line.consume();
            isConsoleSender = true;
        }

        if (!line.test("command")) {
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
        line.consume();

        if (!line.testString()) {
            Main.log(Level.WARNING,"ActionParser", "Expected string after command");
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
        String command = line.consumeDelimitedExpression();
        String parsed = replaceKnownInlineStringVariables(command, false);

        this.generator.requireImport("org.bukkit.Bukkit");
        if (isConsoleSender) {
            this.generator.addBodyLine("Bukkit.dispatchCommand(Bukkit.getConsoleSender(), \"" + parsed + "\");");
        } else {
            generator.setOnlyExecutableByPlayers();
            this.generator.addBodyLine("Bukkit.dispatchCommand(player, \"" + parsed + "\");");
        }

    }

    private void parseCancelEventAction(Fragment line) {
        if (!line.isEmpty()) {
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }

        this.generator.addBodyLine("event.setCancelled(true);");
    }

    private void parseStopAction(Fragment line) {
        if (!line.isEmpty()) {
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }

        this.generator.addBodyLine("return;");
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
        String messageComponent = buildMessageComponentWithString(broadcastMessage);

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
        } else if (line.test("player is op")) {
            line.consume();
            this.generator.setOnlyExecutableByPlayers();
            condition = "player.isOp()";
        } else if (line.test("player is not op")) {
            line.consume();
            this.generator.setOnlyExecutableByPlayers();
            condition = "!player.isOp()";
        } else if (line.test("player is holding")) {
            line.consume();
            this.generator.setOnlyExecutableByPlayers();
            condition = parsePlayerIsHoldingCondition(line);
        } else if (line.test("entity's display name")) {
            line.consume();
            condition = parseVariableFirstConditionalExpression("entity.getName()", line);
        } else if (line.test("entity is")) {
            line.consume();
            condition = parseEntityTypeCheckCondition("entity", line);
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

    private String parseEntityTypeCheckCondition(String entityVariable, Fragment line) {
        EntityType entityType = line.parseEntity();
        this.generator.requireImport("org.bukkit.entity.EntityType");
        return entityVariable + ".getType() == EntityType." + entityType.name();
    }

    private String parsePlayerIsHoldingCondition(Fragment line) {
        return parseComparisonBetweenVariableAndItemProperties("player.getInventory().getItemInMainHand()", line);
    }

    private String parseComparisonBetweenVariableAndItemProperties(String itemStackVariableName, Fragment line) {
        StringBuilder condition = new StringBuilder();

        // parse optional amount
        if (line.testInt()) {
            Integer amount = line.consumeInt();

            if (!line.test("of")) {
                Main.log(Level.WARNING, "ActionParser", "Expected 'of' here");
                this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
                System.exit(1);
                return null;
            }
            line.consume();

            condition.append(itemStackVariableName).append(".getAmount() == ").append(amount).append(" && ");
        }

        // parse the item type
        Material mat = parseMaterial(line);
        this.generator.requireImport("org.bukkit.Material");
        condition.append(itemStackVariableName).append(".getType() == Material.").append(mat.name());

        while (!line.isEmpty()) {
            if (line.testExact(":")) {
                break;
            }

            condition.append(" && ");

            if (line.test("named")) {
                line.consume();
                if (!line.testByDelimiters('{', '}')) {
                    Main.log(Level.WARNING, "ActionParser", "Expected a constant here");
                    this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
                    System.exit(1);
                    return null;
                }
                String variable = line.consume();
                String parsedStringContent = replaceKnownInlineStringVariables(variable, false);
                this.generator.requireImport("net.kyori.adventure.text.TextComponent");
                condition.append("((TextComponent) ")
                        .append(itemStackVariableName)
                        .append(".getItemMeta().displayName()).content() == \"")
                        .append(parsedStringContent)
                        .append("\"");
            } else if (line.test("with lore") || line.test("with the lore")) {
                line.consume();
                if (!line.testByDelimiters('{', '}')) {
                    this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
                    System.exit(1);
                    return null;
                }
                String variable = line.consume();
                String parsedKey = replaceKnownInlineStringVariables(variable, false);
                this.generator.requireImport("net.kyori.adventure.text.TextComponent");
                int moduleInt = Main.nextModuleInt();
                condition.append(itemStackVariableName)
                        .append(".getItemMeta().lore().stream().anyMatch(el")
                        .append(moduleInt)
                        .append(" -> ((TextComponent) el")
                        .append(moduleInt)
                        .append(").content() == \"")
                        .append(parsedKey)
                        .append("\")");
            } else {
                condition.delete(condition.length() - 4, condition.length());
                break;
            }
        }

        return condition.toString();
    }

    private String parseItem(Fragment line) {
        int amount;

        if (line.testInt()) {
            amount = line.consumeInt();
        } else {
            this.generator.getNode().reportUnknownToken(line.getContents(), line.nextToken(), 0);
            System.exit(1);
            return null;
        }

        Material mat = parseMaterial(line);

        int moduleInt = Main.nextModuleInt();

        this.generator.requireImport("org.bukkit.inventory.ItemStack");
        this.generator.requireImport("org.bukkit.Material");
        String itemStackVariableName = "itemStack" + moduleInt;
        this.generator.addBodyLine("ItemStack "+ itemStackVariableName +" = new ItemStack(Material." + mat.name() + ", " + amount + ");");

        while (!line.isEmpty()) {
            if (line.testExact(":")) {
                break;
            }

            if (line.test("named")) {
                line.consume();
                if (!line.testByDelimiters('{', '}')) {
                    Main.log(Level.WARNING, "ActionParser", "Expected a constant here");
                    this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
                    System.exit(1);
                    return null;
                }
                String variable = line.consume();
                String parsedStringContent = replaceKnownInlineStringVariables(variable, false);
                this.generator.requireImport("net.kyori.adventure.text.Component");
                this.generator.addBodyLine(itemStackVariableName + ".getItemMeta().displayName(Component.text(\"" + parsedStringContent + "\"));");
            } else if (line.test("with lore") || line.test("with the lore")) {
                line.consume();
                if (!line.testByDelimiters('{', '}')) {
                    this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
                    System.exit(1);
                    return null;
                }
                String variable = line.consume();
                String parsedKey = replaceKnownInlineStringVariables(variable, false);
                this.generator.requireImport("net.kyori.adventure.text.Component");
                this.generator.requireImport("java.util.List");
                this.generator.addBodyLine(itemStackVariableName + ".getItemMeta().lore(List.of(Component.text(\"" + parsedKey + "\")));");
            } else {
                break;
            }
        }

        return itemStackVariableName;
    }

    private Material parseMaterial(Fragment line) {
        Material mat;
        String errTok;
        if (line.testByDelimiters('{', '}')) { // starts with variable
            String variable = line.consume();
            String parsedKey = replaceKnownInlineStringVariables(variable, false);
            errTok = parsedKey;
            mat = Fragment.parseItem(parsedKey);
        } else {
            errTok = line.nextToken();
            mat = line.parseItem();
        }
        if (mat == null) {
            Main.log(Level.WARNING, "ActionParser", "Unknown item type: " + errTok);
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return null;
        }
        return mat;
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
            messageComponentVariable = buildMessageComponentWithString(message);
        } else if (line.testByDelimiters('{', '}')) {
            String variable = line.consumeDelimitedExpression();
            String parsedKey = replaceKnownInlineStringVariables(variable, false);
            messageComponentVariable = buildMessageComponent(getVariable(parsedKey));
        } else {
            this.generator.getNode().reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
            return;
        }


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
            Matcher mat = Pattern.compile("\\{@([a-zA-Z0-9]+)}").matcher(original);
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

    private String buildMessageComponentWithString(String original) {
        this.generator.requireImport("net.kyori.adventure.text.Component");

        original = replaceKnownInlineStringVariables(original, false);

        return "Component.text(\"" + original + "\")";
    }

    private String buildMessageComponent(String variableName) {
        this.generator.requireImport("net.kyori.adventure.text.Component");

        return "Component.text((String) " + variableName + ")";
    }

    private void generateSendAction(String targetVariable, String messageComponentVariable) {
        this.generator.addBodyLine(targetVariable + ".sendMessage(" + messageComponentVariable + ");");
    }

    private void parseGiveAction(Fragment fragment) {
        String playerVariableName;
        playerVariableName = parseTargetVariable(fragment);

        String itemStackVariableName = parseItem(fragment);

        if (!fragment.isEmpty()) {
            this.generator.getNode().reportUnknownToken(fragment.getContents(), fragment.nextToken(), 0);
            System.exit(1);
        }

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

    private void generateGiveAction(String playerVariableName, String itemStackVariableName) {
        this.generator.addBodyLine(playerVariableName + ".getInventory().addItem(" + itemStackVariableName + ");");
    }
}
