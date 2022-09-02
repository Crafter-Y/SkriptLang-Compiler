package de.craftery.writer.core;

import de.craftery.writer.WritingSection;

import java.util.HashMap;
import java.util.Map;

public class PluginYMLCommandSection extends WritingSection {
    public PluginYMLCommandSection() {
        super(0);
    }

    private final Map<String, PluginYMLCommand> commands = new HashMap<>();

    public void registerCommand(String command, PluginYMLCommand properties) {
        this.commands.put(command, properties);
    }

    public PluginYMLCommand getCommand(String command) {
        return this.commands.get(command);
    }

    @Override
    public void buildLines() {
        if (commands.size() != 0) {
            this.getLines().add("commands:");
            for (Map.Entry<String, PluginYMLCommand> entry : commands.entrySet()) {
                this.getLines().add("  " + entry.getKey() + ":");
                if (entry.getValue().getDescription() != null) {
                    this.getLines().add("    description: " + entry.getValue().getDescription());
                }
                if (entry.getValue().getAliases().size() != 0) {
                    StringBuilder aliasBuilder = new StringBuilder();
                    aliasBuilder.append("    aliases: [");
                    for (int i = 0; i < entry.getValue().getAliases().size(); i++) {
                        if (i != 0) {
                            aliasBuilder.append(", ");
                        }
                        aliasBuilder.append(entry.getValue().getAliases().get(i));
                    }
                    aliasBuilder.append("]");
                    this.getLines().add(aliasBuilder.toString());
                }
                if (entry.getValue().getPermission() != null) {
                    this.getLines().add("    permission: " + entry.getValue().getPermission());
                }
                if (entry.getValue().getPermissionMessage() != null) {
                    this.getLines().add("    permission-message: " + entry.getValue().getPermissionMessage());
                }
                if (entry.getValue().getUsage() != null) {
                    this.getLines().add("    usage: " + entry.getValue().getUsage());
                }
            }
        }
        this.getLines().add("");
    }
}
