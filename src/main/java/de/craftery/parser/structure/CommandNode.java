package de.craftery.parser.structure;

import de.craftery.StringUtils;
import de.craftery.writer.command.CommandGenerator;

public class CommandNode extends StructureNode {
    @Override
    public void acceptLine(String line, int indentation) {
        /*if (indentation != 1) {
            System.err.println("Commands Fields must be indented one time!");
            System.exit(1);
        }*/
    }

    private CommandGenerator commandGenerator;

    @Override
    public CommandNode initialize(String line) {
        String[] tokens = line.trim().split(" ");
        if (tokens.length < 2) {
            System.err.println("A Command must have a name!");
            System.err.println("Example: 'command /command:'");
            System.exit(1);
        }

        if (!tokens[1].matches("^/\\S+:$")) {
            System.err.println("Wrong Syntax!");
            System.err.println("Example: 'command /command:'");
            System.exit(1);
        }

        String commandName = StringUtils.toTitleCase(tokens[1].substring(1, tokens[1].length() - 1));

        this.commandGenerator = new CommandGenerator();
        this.commandGenerator.initialize(commandName);

        return this;
    }
}
