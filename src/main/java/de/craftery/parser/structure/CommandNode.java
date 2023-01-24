package de.craftery.parser.structure;

import de.craftery.Fragment;
import de.craftery.Main;
import de.craftery.StringUtils;
import de.craftery.TimeUtils;
import de.craftery.parser.SkriptParser;
import de.craftery.writer.actions.CommandGenerator;

public class CommandNode extends StructureNode {
    private CommandGenerator commandGenerator;
    @Override
    public void acceptLine(Fragment line, int indentation) {
        if (indentation > 1) {
            Main.exit("Command fields must be indented one time!");
        }
        if (indentation == 0) {
            SkriptParser.exitNode().acceptLine(line, indentation);
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
                commandGenerator.setCommandDescription(fieldValue.trim());
                break;
            }
            case "cooldown": {
                commandGenerator.setCommandCooldown(TimeUtils.parseTime(fieldValue.trim()));
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
                commandGenerator.setNode(node);
                SkriptParser.entryNode(node);
                break;
            }
            case "usage": {
                this.commandGenerator.setUsage(fieldValue.trim());
                break;
            }
            default:
                this.reportUnknownToken(line, fieldName, 0);
                System.exit(1);
        }
    }

    @Override
    public CommandNode initialize(Fragment line) {
        String original = line.getContents();
        if (!line.nextToken().matches("/\\S+")) {
            Main.warn("Wrong Syntax!");
            Main.exit("Example: 'command /command:'");
        }
        String next = line.consume();

        String commandName = StringUtils.toTitleCase(next.substring(1));
        this.commandGenerator = new CommandGenerator();
        this.commandGenerator.initialize(commandName);
        this.commandGenerator.setUsage(original.substring(0, original.length()-1));

        while(!line.isEmpty()) {
            if (line.test(":")) {
                break;
            } else if (line.test("<string>")) {
                line.consume();
                this.commandGenerator.addArgument(new CommandArgument(false, CommandArgument.Type.STRING));
            } else if (line.test("[<text>]")) {
                line.consume();
                this.commandGenerator.addArgument(new CommandArgument(true, CommandArgument.Type.STRING));
            } else if (line.test("[<offline player>]")) {
                line.consume();
                this.commandGenerator.addArgument(new CommandArgument(true, CommandArgument.Type.OFFLINE_PLAYER));
            } else {
                Main.exit("Unknown Argument: " + line.nextToken());
            }

        }

        return this;
    }
}
