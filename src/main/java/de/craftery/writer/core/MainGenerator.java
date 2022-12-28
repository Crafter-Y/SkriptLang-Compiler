package de.craftery.writer.core;

import de.craftery.writer.javaFile.*;


public class MainGenerator extends JavaFileGenerator {
    private static MainGenerator instance;
    private boolean isCooldownHandlerRequired = false;
    private boolean isVariableStoreRequired = false;

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

    public void build() {
        ClassSection classSection = this.getClasses().get(0);
        if (this.isCooldownHandlerRequired) {
            Variable cooldowns = new Variable();
            cooldowns.setName("cooldowns");
            cooldowns.setAccessLevel(AccessLevel.PRIVATE);
            cooldowns.setStatic(true);
            cooldowns.setFinal(true);
            cooldowns.setType("Map<String, Map<String, Long>>");
            cooldowns.setValue("new HashMap<>()");
            classSection.addVariable(cooldowns);

            RawMethodSection getCooldownMethod = new RawMethodSection(1);
            getCooldownMethod.setRawMethodSignature("public static Long getCooldown(String sender, String command)");
            getCooldownMethod.addLine("if (cooldowns.get(command) == null) {");
            getCooldownMethod.addLine("    return 0L;");
            getCooldownMethod.addLine("} else if (cooldowns.get(command).get(sender) == null) {");
            getCooldownMethod.addLine("    return 0L;");
            getCooldownMethod.addLine("} else {");
            getCooldownMethod.addLine("    return cooldowns.get(command).get(sender);");
            getCooldownMethod.addLine("}");
            classSection.addMethod(getCooldownMethod);

            RawMethodSection setCooldownMethod = new RawMethodSection(1);
            setCooldownMethod.setRawMethodSignature("public static void setCooldown(String sender, String command, Long cooldown)");
            setCooldownMethod.addLine("Map<String, Long> commandMap = cooldowns.get(command);");
            setCooldownMethod.addLine("if (commandMap == null) commandMap = new HashMap<>();");
            setCooldownMethod.addLine("commandMap.put(sender, System.currentTimeMillis() + cooldown);");
            setCooldownMethod.addLine("cooldowns.put(command, commandMap);");
            classSection.addMethod(setCooldownMethod);

            this.requireImport("java.util.Map");
            this.requireImport("java.util.HashMap");

        }
        if (this.isVariableStoreRequired) {
            Variable variableStore = new Variable();
            variableStore.setName("variableStore");
            variableStore.setAccessLevel(AccessLevel.PRIVATE);
            variableStore.setStatic(true);
            variableStore.setFinal(true);
            variableStore.setType("Map<String, Object>");
            variableStore.setValue("new HashMap<>()");
            classSection.addVariable(variableStore);

            RawMethodSection variableSetter = new RawMethodSection(1);
            variableSetter.setRawMethodSignature("public static void setVariable(String key, Object value)");
            variableSetter.addLine("variableStore.put(key, value);");
            classSection.addMethod(variableSetter);

            RawMethodSection variableGetter = new RawMethodSection(1);
            variableGetter.setRawMethodSignature("public static Object getVariable(String key)");
            variableGetter.addLine("return variableStore.get(key);");
            classSection.addMethod(variableGetter);

            RawMethodSection variableRemover = new RawMethodSection(1);
            variableRemover.setRawMethodSignature("public static void deleteVariable(String key)");
            variableRemover.addLine("variableStore.remove(key);");
            classSection.addMethod(variableRemover);

            this.requireImport("java.util.Map");
            this.requireImport("java.util.HashMap");
        }
    }

    public void requireCooldownHandler() {
        this.isCooldownHandlerRequired = true;
    }

    public void requireVariableStore() {
        this.isVariableStoreRequired = true;
    }

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
