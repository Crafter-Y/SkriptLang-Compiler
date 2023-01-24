package de.craftery.writer.core;

import de.craftery.writer.javaFile.ClassSection;
import de.craftery.writer.javaFile.JavaFileGenerator;
import de.craftery.writer.javaFile.RawMethodSection;

public class FormatterGenerator extends JavaFileGenerator {
    private static FormatterGenerator instance;
    private boolean isLocationFormatterRequired = false;
    private boolean isUnknownFormatterRequired = false;

    public FormatterGenerator() {
        this.setNeeded(true);
        this.setFileName("Formatter");

        ClassSection classSection = new ClassSection(0);
        classSection.setClassName("Formatter");
        classSection.setFinalModifier(true);

        this.addClass(classSection);
    }

    public void build() {
        ClassSection classSection = this.getClasses().get(0);
        if (this.isLocationFormatterRequired) {
            RawMethodSection formatLocation = new RawMethodSection(1);
            formatLocation.setRawMethodSignature("public static String formatLocation(Location location)");
            formatLocation.addLine("return \"x=\" + location.getX() + \", y=\" + location.getY() + \", z=\" + location.getZ()+ \", yaw=\" + location.getYaw()+ \", pitch=\" + location.getPitch()+ \", world=\" + location.getWorld().getName();");
            classSection.addMethod(formatLocation);

            this.requireImport("org.bukkit.Location");
        }

        if (this.isUnknownFormatterRequired) {
            RawMethodSection formatLocation = new RawMethodSection(1);
            formatLocation.setRawMethodSignature("public static String formatUnknown(Object object)");
            formatLocation.addLine("if (object instanceof Location) {");
            formatLocation.addLine("    return formatLocation((Location) object);");
            formatLocation.addLine("}");
            formatLocation.addLine("return object.toString();");
            classSection.addMethod(formatLocation);

            this.requireImport("org.bukkit.Location");
        }
    }

    public void requireLocationFormatter() {
        this.isLocationFormatterRequired = true;
    }

    public void requireUnknownFormatter() {
        this.isLocationFormatterRequired = true;
        this.isUnknownFormatterRequired = true;
    }

    public static FormatterGenerator getInstance() {
        if (instance == null) {
            instance = new FormatterGenerator();
        }
        return instance;
    }

    public static boolean isInitialized() {
        return instance != null;
    }
}
