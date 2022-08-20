package de.craftery;

import de.craftery.parser.SkriptParser;
import de.craftery.writer.pom.PomGenerator;
import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    @Getter
    private static final ProjectGenerator projectGenerator = new ProjectGenerator();
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Input file not provided!");
            System.exit(1);
        }

        List<String> inputFile = readFile(args[0]);

        SkriptParser parser = new SkriptParser();
        for (String line : inputFile) {
            parser.acceptLine(line);
        }

        projectGenerator.generate();
    }

    private static List<String> readFile(String path) {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(path);
            Scanner fileReader = new Scanner(file);
            while (fileReader.hasNextLine()) {
                lines.add(fileReader.nextLine());
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("The provided file could not be found!");
            System.exit(1);
        }
        return lines;
    }
}