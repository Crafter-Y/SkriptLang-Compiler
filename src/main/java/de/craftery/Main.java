package de.craftery;

import de.craftery.parser.SkriptParser;
import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class Main {
    @Getter
    private static ProjectGenerator projectGenerator;
    private static int moduleInt = ThreadLocalRandom.current().nextInt(100, 500);
    @Getter
    private static String outputFolder;
    public static void main(String[] args) {
        projectGenerator = new ProjectGenerator();
        if (args.length == 0) {
            log(Level.WARNING, "Main", "Input folder not provided!");
            System.exit(1);
        }

        if (args.length == 1) {
            log(Level.WARNING, "Main", "Output folder not provided!");
            System.exit(1);
        }

        if (args.length == 3) {
            moduleInt = Integer.parseInt(args[2]);
        }

        if (args.length > 3) {
            log(Level.WARNING, "Main", "Too many arguments!");
            System.exit(1);
        }

        List<File> inputFiles = getApplicableFilesOfDirectory(args[0]);

        outputFolder = args[1];

        SkriptParser parser = new SkriptParser();
        for (File file : inputFiles) {
            Main.log(Level.INFO, "Main", "Parsing file " + file.getName());
            List<String> inputFile = readFile(file);

            for (int i = 0; i < inputFile.size(); i++) {
                String line = inputFile.get(i);
                if (line.trim().startsWith("#")) {
                    continue;
                }
                parser.acceptLine(line, i + 1);
            }
        }
        parser.finish();

        projectGenerator.generate();
    }

    public static void log(Level level, String origin, String message) {
        String color = "";
        /*if (Level.SEVERE.equals(level)) {
            color = WinConsoleColor.ANSI_WHITE;
        } else if (Level.WARNING.equals(level)) {
            color = WinConsoleColor.ANSI_RED;
        } else if (Level.INFO.equals(level)) {
            color = WinConsoleColor.ANSI_YELLOW;
        } else if (Level.CONFIG.equals(level)) {
            color = WinConsoleColor.ANSI_BLUE;
        } else if (Level.FINE.equals(level)) {
            color = WinConsoleColor.ANSI_GREEN;
        } else if (Level.FINER.equals(level)) {
            color = WinConsoleColor.ANSI_GREEN;
        } else {
            color = WinConsoleColor.ANSI_WHITE;
        }*/
        if (level == Level.WARNING) {
            System.err.println(color + "[" + level.getName() + "] (" + origin + ") " + message);
        } else {
            System.out.println(color + "[" + level.getName() + "] (" + origin + ") " + message);
        }
    }

    private static List<File> getApplicableFilesOfDirectory(String dir) {
        List<File> files = new ArrayList<>();
        File directory = new File(dir);
        if (!directory.exists()) {
            log(Level.SEVERE, "Main", "Directory " + dir + " does not exist!");
            System.exit(1);
        }
        if (!directory.isDirectory()) {
            log(Level.SEVERE, "Main", dir + " is not a directory!");
            System.exit(1);
        }
        File[] directoryFiles = directory.listFiles();
        if (directoryFiles == null) {
            log(Level.SEVERE, "Main", "Directory " + dir + " is empty!");
            System.exit(1);
        }

        for (File file : directoryFiles) {
            if (file.getName().endsWith(".sk") &&
                    !file.getName().startsWith("-") &&
                    !file.isDirectory() &&
                    file.canRead()
            ) {
                files.add(file);
            }
        }
        if (files.size() == 0) {
            log(Level.SEVERE, "Main", "No applicable files found in directory " + dir + "!");
            System.exit(1);
        }

        Collections.sort(files);

        return files;
    }

    private static List<String> readFile(File file) {
        List<String> lines = new ArrayList<>();
        try {
            Scanner fileReader = new Scanner(file, StandardCharsets.UTF_8);
            while (fileReader.hasNextLine()) {
                lines.add(fileReader.nextLine());
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            log(Level.WARNING, "Main", "The provided file could not be found!");
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (lines.size() == 0) {
            log(Level.WARNING, "Main", "The provided file is empty!");
            System.exit(1);
        }
        return lines;
    }

    public static int nextModuleInt() {
        moduleInt++;
        return moduleInt;
    }
}