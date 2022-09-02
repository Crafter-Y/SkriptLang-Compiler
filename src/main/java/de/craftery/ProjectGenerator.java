package de.craftery;

import de.craftery.writer.WriterComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ProjectGenerator {
    private final List<WriterComponent> components = new ArrayList<>();

    public void addComponent(WriterComponent component) {
        components.add(component);
    }

    public void generate() {
        Main.log(Level.SEVERE, "ProjectGenerator", "Starting project generation...");
        for (WriterComponent component : components) {
            if (component.isNeeded()) {
                component.write();
            }
        }
    }
}
