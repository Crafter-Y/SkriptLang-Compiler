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

    private String commandName;

    public void registerAlias(String alias) {
        PluginYMLGenerator.getInstance().getCommands().getCommand(this.commandName.toLowerCase()).getAliases().add(alias);
    }

    public void prependReturnStatement() {
        RawMethodSection commandSection = this.getClasses().get(0).getMethods().get(0);
        commandSection.addLine("return true;");
    }

    public void setOnlyExecutableByPlayers() {
        RawMethodSection commandSection = this.getClasses().get(0).getMethods().get(0);
        commandSection.addLine("if (!(sender instanceof Player)) {");
        commandSection.addLine("    sender.sendMessage(\"§cOnly Players can execute this command!\");");
        commandSection.addLine("    return true;");
        commandSection.addLine("}");
        this.requireImport("org.bukkit.entity.Player");
    }

    public void setPermission(String permission) {
        PluginYMLGenerator.getInstance().getCommands().getCommand(this.commandName.toLowerCase()).setPermission(permission);
    }

    public void setPermissionMessage(String permissionMessage) {
        PluginYMLGenerator.getInstance().getCommands().getCommand(this.commandName.toLowerCase()).setPermissionMessage(permissionMessage);
    }

    public void setDescription(String description) {
        PluginYMLGenerator.getInstance().getCommands().getCommand(this.commandName.toLowerCase()).setDescription(description);
    }

    public void initialize(String commandName) {
        this.commandName = commandName;
        this.setFileName(commandName + "Command");
        ClassSection classSection = new ClassSection(0);
        classSection.setClassName(commandName + "Command");
        classSection.addImplementationClass("CommandExecutor");
        RawMethodSection methodSection = new RawMethodSection(1);
        methodSection.setRawMethodSignature("public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)");
        methodSection.addAnnotation("@Override");
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
