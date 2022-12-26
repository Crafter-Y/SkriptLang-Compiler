package de.craftery.parser.structure;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.StringUtils;
import de.craftery.TimeUtils;
import de.craftery.parser.SkriptParser;
import de.craftery.writer.command.CommandGenerator;

import java.util.logging.Level;

public class CommandNode extends StructureNode {
    private CommandGenerator commandGenerator;
    @Override
    public void acceptLine(Fragment line, int indentation) {
        if (indentation > 1) {
            Main.log(Level.WARNING, "CommandNode", "Command fields must be indented one time!");
            System.exit(1);
        }
        if (indentation == 0) {
            SkriptParser.exitNode().acceptLine(line, indentation);
            commandGenerator.build();
            return;
        }
        String fieldName = line.getContents().split(":")[0];
        String fieldValue = line.getContents().substring(fieldName.length() + 1);
        switch (fieldName) {
            case "aliases": {
                String[] aliases = fieldValue.split(",");
                for (String alias : aliases) {
                    commandGenerator.registerAlias(alias.trim().replace("/", ""));
                }
                break;
            }
            case "executable by": {
                switch (fieldValue.trim()) {
                    case "players": {
                        commandGenerator.setOnlyExecutableByPlayers();
                        break;
                    }
                    default:
                        this.reportUnknownToken(line, fieldValue.trim(), 2);
                        System.exit(1);
                }

                break;
            }
            case "permission": {
                commandGenerator.setPermission(fieldValue.trim());
                break;
            }
            case "permission message": {
                commandGenerator.setPermissionMessage(fieldValue.trim());
                break;
            }
            case "description": {
                commandGenerator.setDescription(fieldValue.trim());
                break;
            }
            case "cooldown": {
                commandGenerator.setCooldown(TimeUtils.parseTime(fieldValue.trim()));
                break;
            }
            case "cooldown message": {
                commandGenerator.setCooldownMessage(fieldValue.trim());
                break;
            }
            case "cooldown bypass": {
                commandGenerator.setCooldownBypassPermission(fieldValue.trim());
                break;
            }
            case "trigger": {
                CommandTriggerNode node = new CommandTriggerNode().initialize(line);
                node.setGenerator(commandGenerator);
                SkriptParser.entryNode(node);
                break;
            }
            default:
                this.reportUnknownToken(line, fieldName, 0);
                System.exit(1);
        }
    }

    @Override
    public CommandNode initialize(Fragment line) {
        String[] tokens = line.getContents().trim().split(" ");
        if (tokens.length < 2) {
            Main.log(Level.WARNING, "CommandNode", "A Command must have a name!");
            Main.log(Level.WARNING, "CommandNode", "Example: 'command /command:'");
            System.exit(1);
        }

        if (!tokens[1].matches("^/\\S+:$")) {
            Main.log(Level.WARNING, "CommandNode", "Wrong Syntax!");
            Main.log(Level.WARNING, "CommandNode", "Example: 'command /command:'");
            System.exit(1);
        }

        String commandName = StringUtils.toTitleCase(tokens[1].substring(1, tokens[1].length() - 1));

        this.commandGenerator = new CommandGenerator();
        this.commandGenerator.initialize(commandName);

        return this;
    }
}
