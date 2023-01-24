package de.craftery;

import de.craftery.parser.helper.Options;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AllTests {
    @Test
    public void test() {
        File testDir = new File("tests/testProjects");
        File outputDir = new File("tests/realOutputs/");
        File[] testProjects = testDir.listFiles();
        assertNotNull(testProjects, "Test projects not found");

        deleteDir(outputDir);

        for (File testProject : testProjects) {
            if (testProject.isDirectory()) {
                Options.setOptions(new HashMap<>());
                Main.main(new String[]{testProject.getAbsolutePath(), "tests/realOutputs/" + testProject.getName(), "1"});
            }
        }
        System.out.println("Checking if all files are the same...");
        File expectedDir = new File("tests/outputs/");
        assertTrue(expectedDir.exists(), "Expected outputs not found");

        File[] expectedProjects = expectedDir.listFiles();
        assertNotNull(expectedProjects, "Expected projects not found");

        for (File expectedProject : expectedProjects) {
            if (expectedProject.isDirectory()) {
                compareFiles(expectedProject.getName());
            }
        }
    }

    private void compareFiles(String dir) {
        if (dir.endsWith("target")) {
            return;
        }
        File expectedDir = new File("tests/outputs/" + dir);
        File[] expectedFiles = expectedDir.listFiles();
        assertNotNull(expectedFiles, "Expected files not found");
        for (File expectedFile : expectedFiles) {
            if (expectedFile.isDirectory()) {
                compareFiles(dir + "/" + expectedFile.getName());
                continue;
            }
            File realFile = new File("tests/realOutputs/" + dir + "/" + expectedFile.getName());
            assertTrue(realFile.exists(), "File not found: " + realFile.getAbsolutePath());
            compareTwoFiles(expectedFile, realFile);
        }
    }

    private void compareTwoFiles(File expectedFile, File realFile) {
        // compare line by line
        try {
            List<String> expectedLines = Files.readAllLines(expectedFile.toPath());
            List<String> realLines = Files.readAllLines(realFile.toPath());
            assertEquals(expectedLines.size(), realLines.size(), "File size not equal: " + realFile.getAbsolutePath());
            for (int i = 0; i < expectedLines.size(); i++) {
                assertEquals(expectedLines.get(i), realLines.get(i), "File content not equal: " + realFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error while comparing files: " + realFile.getAbsolutePath());
        }
    }

    private static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }
}
