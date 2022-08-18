package de.craftery;

import de.craftery.writer.WriterComponent;

import java.util.ArrayList;
import java.util.List;

public class ProjectGenerator {
    private final List<WriterComponent> components = new ArrayList<>();

    public void addComponent(WriterComponent component) {
        components.add(component);
    }

    public void generate() {
        System.out.println("Starting project generation...");
        for (WriterComponent component : components) {
            if (component.isNeeded()) {
                component.write();
            }
        }
    }
}
