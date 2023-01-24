package de.craftery.writer.core;

import de.craftery.parser.SkriptParser;
import de.craftery.writer.StructuredFile;
import lombok.Getter;

public class PluginYMLGenerator extends StructuredFile {
    @Getter
    private final PluginYMLHeader fileHeader = new PluginYMLHeader();
    @Getter
    private final PluginYMLCommandSection commands = new PluginYMLCommandSection();

    public PluginYMLGenerator() {
        this.setNeeded(true);
        this.setFileName("plugin.yml");
        this.setFolderPrefix("src/main/resources/");
    }

    @Override
    public void buildSections() {
        this.addSection(fileHeader);
        this.addSection(commands);
    }

    public static void registerCommand(String commandName) {
        getInstance().getCommands().registerCommand(commandName.toLowerCase(), new PluginYMLCommand());
    }

    public static PluginYMLGenerator getInstance() {
        return SkriptParser.getInstance().getPluginYMLGenerator();
    }
}
