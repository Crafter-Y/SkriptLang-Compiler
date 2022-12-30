package de.craftery.parser.structure;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.parser.SkriptParser;
import de.craftery.parser.structure.events.ItemEnchantEventNode;
import de.craftery.writer.actions.events.ItemEnchantGenerator;
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
        } else if (line.test("enchant") ||
                line.test("on enchant") ||
                line.test("item enchant") ||
                line.test("on item enchant")
        ) {
            // https://skripthub.net/docs/?id=4261
            //Syntax: [on] [item] enchant
            line.consume();
            ItemEnchantGenerator generator = new ItemEnchantGenerator();
            ItemEnchantEventNode node = new ItemEnchantEventNode();
            node.setGenerator(generator);
            generator.setNode(node);
            SkriptParser.entryNode(node);
        } else if (line.test("options")) {
            line.consume();
            OptionsNode node = new OptionsNode().initialize(line);
            SkriptParser.entryNode(node);
        } else {
            reportUnknownToken(line, line.nextToken(), 0);
            System.exit(1);
        }
    }

    @Override
    public RootNode initialize(Fragment ignored) {
        return this;
    }
}
