package de.craftery.writer.actions;

import de.craftery.parser.structure.CommandArgument;
import de.craftery.writer.core.PluginYMLGenerator;
import de.craftery.writer.javaFile.ClassSection;
import de.craftery.writer.javaFile.RawMethodSection;
import de.craftery.writer.core.MainGenerator;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class CommandGenerator extends ActionGenerator {
    private String commandName;
    private boolean isPlayerOnly;
    private String usageMessage = "§cWrong command syntax!";
    @Setter
    private String cooldownMessage = "§cYou need to slow down!";
    private boolean hasCooldownBypassPermission = false;
    private String cooldownBypassPermission = "";
    private RawMethodSection commandSection;
    private long commandCooldown = 0;
    private boolean hasCooldown = false;

    private final List<CommandArgument> arguments = new ArrayList<>();

    public CommandGenerator() {
        this.setPackage("command");
        this.setNeeded(true);
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

        commandSection = this.getClasses().get(0).getMethods().get(0);
    }

    public void addArgument(CommandArgument argument) {
        this.arguments.add(argument);
    }

    @Override
    public void build() {
        RawMethodSection commandSection = this.commandSection;

        // player only check
        if (isPlayerOnly) {
            commandSection.addLine("if (!(sender instanceof Player)) {");
            commandSection.addLine("    sender.sendMessage(\"§cThis command is only for players!\");");
            commandSection.addLine("    return true;");
            commandSection.addLine("}");
            commandSection.addLine("Player player = (Player) sender;");
            commandSection.addLine("");
            this.requireImport("org.bukkit.entity.Player");
        }

        // min args
        if (this.getRequiredArgumentCount() > 0) {
            commandSection.addLine("if (args.length < "+ this.getRequiredArgumentCount() +") {");
            commandSection.addLine("    sender.sendMessage(\""+ usageMessage +"\");");
            commandSection.addLine("    return true;");
            commandSection.addLine("}");
            commandSection.addLine("");
        }

        // max args
        if (this.getOptionalMaxArgumentCont() >= 0) {
            commandSection.addLine("if (args.length > "+ this.getOptionalMaxArgumentCont() +") {");
            commandSection.addLine("    sender.sendMessage(\""+ usageMessage +"\");");
            commandSection.addLine("    return true;");
            commandSection.addLine("}");
            commandSection.addLine("");
        }

        // integrate every defined argument as variable ignoring the type
        for (int i = 0; i < arguments.size(); i++) {
            if (i == arguments.size() - 1) {
                commandSection.addLine("StringBuilder argumentBuilder = new StringBuilder();");
                commandSection.addLine("for (int i = "+ i +"; i < args.length; i++) {");
                commandSection.addLine("    argumentBuilder.append(args[i]);");
                commandSection.addLine("    if (i != args.length) argumentBuilder.append(\" \");");
                commandSection.addLine("}");
                commandSection.addLine("String argument" + (i + 1) + " = argumentBuilder.toString();");
            } else {
                commandSection.addLine("String argument" + (i + 1) + " = args[" + i + "];");
            }
        }

        if (arguments.size() != 0) {
            commandSection.addLine("");
        }

        // command cooldown
        if (hasCooldown) {
            if (hasCooldownBypassPermission) {
                commandSection.addLine("if (!sender.hasPermission(\""+ cooldownBypassPermission +"\") && Main.getCooldown(sender.getName(), \""+ commandName +"Command\") > System.currentTimeMillis()) {");
            } else {
                commandSection.addLine("if (Main.getCooldown(sender.getName(), \""+ commandName +"Command\") > System.currentTimeMillis()) {");
            }
            commandSection.addLine("    sender.sendMessage(\""+ cooldownMessage +"\");");
            commandSection.addLine("    return true;");
            commandSection.addLine("} else {");
            commandSection.addLine("    Main.setCooldown(sender.getName(), \""+ commandName +"Command\", "+ commandCooldown +"L);");
            commandSection.addLine("}");
            commandSection.addLine("");
            this.requireImport("de.craftery.autogenerated.Main");
        }

        super.build();

        // return statement
        commandSection.addLine("return true;");
    }

    private int getRequiredArgumentCount() {
        int count = 0;
        for (CommandArgument argument : this.arguments) {
            if (!argument.isOptional()) count++;
        }
        return count;
    }

    private int getOptionalMaxArgumentCont() {
        for (CommandArgument argument : this.arguments) {
            if (argument.isOptional()) return -1;
        }
        return arguments.size();
    }

    public void setCooldownBypassPermission(String permission) {
        this.hasCooldownBypassPermission = true;
        this.cooldownBypassPermission = permission;
    }

    public void setUsage(String usage) {
        this.usageMessage = "Correct usage: " + usage;
    }

    public void registerAlias(String alias) {
        PluginYMLGenerator.getInstance().getCommands().getCommand(this.commandName.toLowerCase()).getAliases().add(alias);
    }

    @Override
    public void setOnlyExecutableByPlayers() {
        this.isPlayerOnly = true;
    }

    public void setPermission(String permission) {
        PluginYMLGenerator.getInstance().getCommands().getCommand(this.commandName.toLowerCase()).setPermission(permission);
    }

    public void setPermissionMessage(String permissionMessage) {
        PluginYMLGenerator.getInstance().getCommands().getCommand(this.commandName.toLowerCase()).setPermissionMessage(permissionMessage);
    }

    public void setCommandDescription(String description) {
        PluginYMLGenerator.getInstance().getCommands().getCommand(this.commandName.toLowerCase()).setDescription(description);
    }

    @Override
    public void setCommandCooldown(long cooldown) {
        MainGenerator.getInstance().requireCooldownHandler();
        this.hasCooldown = true;
        this.commandCooldown = cooldown;
    }

    @Override
    public Type getType() {
        return Type.COMMAND;
    }

    @Override
    protected RawMethodSection getBodySection() {
        return this.commandSection;
    }
}
