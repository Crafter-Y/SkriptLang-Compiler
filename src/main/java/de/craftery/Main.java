package de.craftery;

import de.craftery.parser.SkriptParser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class Main {
    private static int moduleInt = ThreadLocalRandom.current().nextInt(100, 500);

    public static void main(String[] args) {
        if (args.length == 0) {
            Main.exit("Input folder not provided!");
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("status")) {
            status();
            return;
        }

        if (args.length == 1) {
            Main.exit("Output folder not provided!");
        }

        if (args.length == 3) {
            moduleInt = Integer.parseInt(args[2]);
        }

        if (args.length > 3) {
            Main.exit("Too many arguments!");
        }

        List<File> inputFiles = getApplicableFilesOfDirectory(args[0]);

        SkriptParser parser = new SkriptParser(args[1]);
        for (File file : inputFiles) {
            Main.log("Parsing file " + file.getName());
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
    }

    private static void status() {
        System.out.println("This will somehow report the status of the implementation if Skript.");

    }

    private static void log(Level level, String originString, String message) {
        if (level == Level.WARNING) {
            System.err.println("[" + level.getName() + "] (" + originString + ") " + message);
        } else {
            System.out.println("[" + level.getName() + "] (" + originString + ") " + message);
        }
    }

    public static void log(String message) {
        StackTraceElement origin = Thread.currentThread().getStackTrace()[2];
        String originString = origin.getFileName() + ":" + origin.getLineNumber();

        Main.log(Level.SEVERE, originString, message);
    }

    public static void warn(String message) {
        StackTraceElement origin = Thread.currentThread().getStackTrace()[2];
        String originString = origin.getFileName() + ":" + origin.getLineNumber();

        Main.log(Level.WARNING, originString, message);
    }

    public static void exit(String message) {
        StackTraceElement origin = Thread.currentThread().getStackTrace()[2];
        String originString = origin.getFileName() + ":" + origin.getLineNumber();

        Main.log(Level.WARNING, originString, message);
        System.exit(1);
    }

    private static @NotNull List<File> getApplicableFilesOfDirectory(String dir) {
        List<File> files = new ArrayList<>();
        File directory = new File(dir);
        if (!directory.exists()) {
            Main.exit("Directory " + dir + " does not exist!");
        }
        if (!directory.isDirectory()) {
            Main.exit(dir + " is not a directory!");
        }
        File[] directoryFiles = directory.listFiles();
        if (directoryFiles == null) {
            Main.exit("Directory " + dir + " is empty!");
            return new ArrayList<>();
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
            Main.exit("No applicable files found in directory " + dir + "!");
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
            Main.exit("The provided file could not be found!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (lines.size() == 0) {
            Main.exit("The provided file is empty!");
        }
        return lines;
    }

    public static int nextModuleInt() {
        moduleInt++;
        return moduleInt;
    }
}