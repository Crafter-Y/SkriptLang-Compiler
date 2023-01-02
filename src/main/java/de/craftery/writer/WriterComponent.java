package de.craftery.writer;

import de.craftery.Main;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public abstract class WriterComponent {
    public WriterComponent() {
        Main.getProjectGenerator().addComponent(this);
    }
    @Setter
    private String fileName;

    @Setter
    private String folderPrefix = "src/main/java/";

    @Setter
    private String packagePrefix = null;

    @Setter
    private String packageName = null;

    @Getter
    private final List<String> content = new ArrayList<>();

    public abstract void prepareContent();

    @Getter
    @Setter
    private boolean needed;

    public void write() {
        prepareContent();
        try {
            File file = new File(buildPackagePath());
            Main.log(Level.SEVERE, "WriterComponent", "Generating file: " + buildPackagePath());
            if (!file.exists()) {
                Files.createDirectories(file.getParentFile().toPath());
                boolean succeed = file.createNewFile();
                if (!succeed) {
                    Main.log(Level.WARNING, "WriterComponent", "Could not create this file!");
                    System.exit(1);
                }
            }
            FileWriter myWriter = new FileWriter(buildPackagePath(), StandardCharsets.UTF_8);
            for (String line : content) {
                myWriter.write(line + "\n");
            }
            myWriter.close();
        } catch (IOException e) {
            Main.log(Level.WARNING, "WriterComponent", "Writing failed!");
            e.printStackTrace();
        }
    }

    private String buildPackagePath() {
        StringBuilder pathBuilder = new StringBuilder();

        pathBuilder.append(Main.getOutputFolder());

        if (!pathBuilder.toString().endsWith("/")) {
            pathBuilder.append("/");
        }

        pathBuilder.append(folderPrefix);

        if (packagePrefix != null) {
            String escapedPackagePrefix = (packagePrefix + "").replace(".", "/");
            pathBuilder.append(escapedPackagePrefix);
            pathBuilder.append("/");
        }

        if (packageName != null) {
            String escapedPackageName = (packageName + "").replace(".", "/");
            pathBuilder.append(escapedPackageName);
            pathBuilder.append("/");
        }

        pathBuilder.append(fileName);

        return pathBuilder.toString();
    }
}
