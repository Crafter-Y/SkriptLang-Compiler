package de.craftery.writer.command;

import de.craftery.writer.core.PluginYMLGenerator;
import de.craftery.writer.javaFile.ClassSection;
import de.craftery.writer.javaFile.JavaFileGenerator;
import de.craftery.writer.javaFile.RawMethodSection;
import de.craftery.writer.core.MainGenerator;

public class CommandGenerator extends JavaFileGenerator {
    public CommandGenerator() {
        this.setPackage("command");
        this.setNeeded(true);
    }

    public void initialize(String commandName) {
        this.setFileName(commandName + "Command");
        ClassSection classSection = new ClassSection(0);
        classSection.setClassName(commandName + "Command");
        classSection.addImplementationClass("CommandExecutor");
        RawMethodSection methodSection = new RawMethodSection(1);
        methodSection.setRawMethodSignature("public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)");
        methodSection.addAnnotation("@Override");
        methodSection.addLine("return true;");
        classSection.addMethod(methodSection);
        this.addClass(classSection);
        this.requireImport("org.bukkit.command.Command");
        this.requireImport("org.bukkit.command.CommandExecutor");
        this.requireImport("org.bukkit.command.CommandSender");
        this.requireImport("org.jetbrains.annotations.NotNull");
        MainGenerator.registerCommand(commandName);
        PluginYMLGenerator.registerCommand(commandName);
    }
}
