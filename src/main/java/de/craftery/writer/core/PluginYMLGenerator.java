package de.craftery.writer.core;

import de.craftery.writer.StructuredFile;
import lombok.Getter;
import lombok.Setter;

public class PluginYMLGenerator extends StructuredFile {
    public PluginYMLGenerator() {
        this.setFileName("plugin.yml");
        this.setFolderPrefix("src/main/resources/");
    }

    @Override
    public void buildSections() {
        this.addSection(fileHeader);
        this.addSection(commands);
    }

    public static void initialize() {
        getInstance().setNeeded(true);
    }

    @Getter
    private final PluginYMLHeader fileHeader = new PluginYMLHeader();

    @Getter
    private final PluginYMLCommandSection commands = new PluginYMLCommandSection();

    public static void registerCommand(String commandName) {
        getInstance().getCommands().registerCommand(commandName.toLowerCase(), new PluginYMLCommand());
    }

    @Setter
    private static PluginYMLGenerator instance;
    public static PluginYMLGenerator getInstance() {
        if (instance == null) {
            instance = new PluginYMLGenerator();
        }
        return instance;
    }
}
