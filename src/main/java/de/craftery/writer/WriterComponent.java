package de.craftery.writer;

import de.craftery.Main;
import de.craftery.parser.SkriptParser;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public abstract class WriterComponent {
    public WriterComponent() {
        SkriptParser.getInstance().getProjectGenerator().addComponent(this);
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
    @Getter
    @Setter
    private boolean needed;

    public abstract void prepareContent();

    public void write() {
        prepareContent();
        try {
            File file = new File(buildPackagePath());
            Main.log("Generating file: " + buildPackagePath());
            if (!file.exists()) {
                Files.createDirectories(file.getParentFile().toPath());
                boolean succeed = file.createNewFile();
                if (!succeed) {
                    Main.exit("Could not create this file!");
                }
            }
            FileWriter myWriter = new FileWriter(buildPackagePath(), StandardCharsets.UTF_8);
            for (String line : content) {
                myWriter.write(line + "\n");
            }
            myWriter.close();
        } catch (IOException e) {
            Main.warn("Writing failed!");
            e.printStackTrace();
        }
    }

    private String buildPackagePath() {
        StringBuilder pathBuilder = new StringBuilder();

        pathBuilder.append(SkriptParser.getInstance().getOutputFolder());

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
