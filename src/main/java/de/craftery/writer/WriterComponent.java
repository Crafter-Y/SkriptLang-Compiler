package de.craftery.writer;

import de.craftery.Main;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public abstract class WriterComponent {
    public WriterComponent() {
        Main.getProjectGenerator().addComponent(this);
    }
    @Setter
    private String fileName;

    @Setter
    private String folderPrefix = "out/";

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
            System.out.println(file.getAbsolutePath());
            if (!file.exists()) {
                Files.createDirectories(file.getParentFile().toPath());
                System.out.println("Generating file: " + file.getAbsolutePath());
                boolean succeed = file.createNewFile();
                if (!succeed) {
                    System.err.println("Could not create file!");
                    System.exit(1);
                }
            }
            FileWriter myWriter = new FileWriter(buildPackagePath());
            for (String line : content) {
                myWriter.write(line + "\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("Writing to the file failed.");
            e.printStackTrace();
        }
    }

    private String buildPackagePath() {
        StringBuilder pathBuilder = new StringBuilder();

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
