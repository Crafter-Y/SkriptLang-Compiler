package de.craftery.writer.core;

import de.craftery.writer.javaFile.ClassSection;
import de.craftery.writer.javaFile.JavaFileGenerator;
import de.craftery.writer.javaFile.RawMethodSection;

public class FormatterGenerator extends JavaFileGenerator {
    private static FormatterGenerator instance;

    private boolean isLocationFormatterRequired = false;

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
            formatLocation.addLine("return location.toString();");
            classSection.addMethod(formatLocation);

            this.requireImport("org.bukkit.Location");
        }
    }

    public void requireLocationFormatter() {
        this.isLocationFormatterRequired = true;
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
