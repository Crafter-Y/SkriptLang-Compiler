package de.craftery.writer.core;

import de.craftery.writer.javaFile.ClassSection;
import de.craftery.writer.javaFile.JavaFileGenerator;
import de.craftery.writer.javaFile.RawMethodSection;

public class MainGenerator extends JavaFileGenerator {
    public MainGenerator() {
        this.setNeeded(true);
        this.setFileName("Main");
        ClassSection classSection = new ClassSection(0);
        classSection.setClassName("Main");
        classSection.setFinalModifier(true);
        classSection.setExtendsClass("JavaPlugin");

        RawMethodSection onEnableSection = new RawMethodSection(1);
        onEnableSection.setRawMethodSignature("public void onEnable()");
        onEnableSection.addAnnotation("@Override");
        classSection.addMethod(onEnableSection);

        RawMethodSection onDisableSection = new RawMethodSection(1);
        onDisableSection.setRawMethodSignature("public void onDisable()");
        onDisableSection.addAnnotation("@Override");
        classSection.addMethod(onDisableSection);

        this.addClass(classSection);
        this.requireImport("org.bukkit.plugin.java.JavaPlugin");
    }
    private static MainGenerator instance;
    public static MainGenerator getInstance() {
        if (instance == null) {
            instance = new MainGenerator();
        }
        return instance;
    }

    public static void initialize() {
        getInstance().setNeeded(true);
    }

    public static void registerCommand(String commandName) {
        ClassSection classSection = getInstance().getClasses().get(0);
        RawMethodSection onEnableSection = classSection.getMethods().get(0);
        String commandSelector = "PluginCommand " + commandName.toLowerCase() + "Command = Bukkit.getPluginCommand(\"" + commandName.toLowerCase() + "\");";
        onEnableSection.addLine(commandSelector);
        onEnableSection.addLine("if ("+ commandName.toLowerCase() +"Command != null) {");
        onEnableSection.addLine("    "+ commandName.toLowerCase() +"Command.setExecutor(new "+ commandName +"Command());");
        onEnableSection.addLine("}");

        RawMethodSection onDisableSection = classSection.getMethods().get(1);
        onDisableSection.addLine(commandSelector);
        onDisableSection.addLine("if ("+ commandName.toLowerCase() +"Command != null) {");
        onDisableSection.addLine("    "+ commandName.toLowerCase() +"Command.setExecutor(null);");
        onDisableSection.addLine("}");

        getInstance().requireImport("de.craftery.autogenerated.command."+ commandName + "Command");
        getInstance().requireImport("org.bukkit.Bukkit");
        getInstance().requireImport("org.bukkit.command.PluginCommand");
    }
}
