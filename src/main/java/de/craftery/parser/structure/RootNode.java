package de.craftery.parser.structure;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.parser.SkriptParser;
import de.craftery.parser.structure.events.ItemEnchantEventNode;
import de.craftery.parser.structure.events.OnClickEventNode;
import de.craftery.parser.structure.events.PrepareItemEnchantEventNode;
import de.craftery.writer.core.MainGenerator;
import de.craftery.writer.core.PluginYMLGenerator;
import de.craftery.writer.pom.PomGenerator;

import java.util.logging.Level;

public class RootNode extends StructureNode {
    public RootNode() {
        PomGenerator.initialize();
        PluginYMLGenerator.initialize();
        MainGenerator.initialize();
    }

    @Override
    public void acceptLine(Fragment line, int indentation) {
        if (indentation != 0) {
            Main.log(Level.WARNING, "RootNode" , "There should not be an indentation here! " + line);
            System.exit(1);
        }
        if (line.isEmpty()) {
            return;
        }

        if (line.test("command")) {
            line.consume();
            CommandNode node = new CommandNode().initialize(line);
            SkriptParser.entryNode(node);
        } else if (testPrepareEnchant(line)) {
            PrepareItemEnchantEventNode node = new PrepareItemEnchantEventNode();
            SkriptParser.entryNode(node);
        } else if (testEnchant(line)) {
            ItemEnchantEventNode node = new ItemEnchantEventNode();
            SkriptParser.entryNode(node);
        } else if (line.test("options")) {
            line.consume();
            OptionsNode node = new OptionsNode().initialize(line);
            SkriptParser.entryNode(node);
        } else if (testOnClick(line)) {
            OnClickEventNode node = new OnClickEventNode().initialize(line);
            SkriptParser.entryNode(node);
        } else {
            reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
    }

    // https://skripthub.net/docs/?id=4262
    private boolean testPrepareEnchant(Fragment line) {
        //Syntax: [on] [item] enchant prepare
        if (line.test("enchant prepare") ||
                line.test("on enchant prepare") ||
                line.test("item enchant prepare") ||
                line.test("on item enchant prepare")) {
            line.consume();
            return true;
        }
        return false;
    }

    // https://skripthub.net/docs/?id=4261
    private boolean testEnchant(Fragment line) {
        //Syntax: [on] [item] enchant
        if (line.test("enchant") ||
                line.test("on enchant") ||
                line.test("item enchant") ||
                line.test("on item enchant")) {
            line.consume();
            return true;
        }
        return false;
    }

    // https://skripthub.net/docs/?id=1094
    private boolean testOnClick(Fragment line) {
        // Syntax: [on] [(right|left)(| |-)][mouse(| |-)]click[ing] [on %entitydata/itemtype%] [(with|using|holding) %itemtype%]
        // Syntax: [on] [(right|left)(| |-)][mouse(| |-)]click[ing] (with|using|holding) %itemtype% on %entitydata/itemtype%
        String original = line.getContents();

        if (line.test("on")) {
            line.consume();
        }

        String type = null;
        if (line.testExact("right") || line.testExact("left")) {
            type = line.consumeExact();

            if (line.testExact(" ") || line.testExact("-")) {
                line.consumeExact();
            }
        }

        if (line.testExact("mouse")) {
            line.consumeExact();

            if (line.testExact(" ") || line.testExact("-")) {
                line.consumeExact();
            }
        }

        if (line.test("click") || line.test("clicking")) {
            line.consume();

            if (type != null) {
                line.setContents(type + " " + line.getContents());
            }

            return true;
        } else {
            line.setContents(original);
            return false;
        }
    }

    @Override
    public RootNode initialize(Fragment ignored) {
        return this;
    }
}
