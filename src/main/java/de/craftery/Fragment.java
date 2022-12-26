package de.craftery;

import lombok.Getter;
import org.bukkit.Material;

public class Fragment {
    @Getter
    private String contents;

    private int testLength = 0;

    public Fragment(String contents) {
        this.contents = contents;
    }

    public boolean test(String tester) {
        if (this.contents.startsWith(tester + " ") || this.contents.equals(tester)) {
            this.testLength = tester.length();
            return true;
        }
        return false;
    }

    public boolean testInt() {
        StringBuilder tester = new StringBuilder();
        for (int i = 0; i < this.contents.length(); i++) {
            char c = this.contents.charAt(i);
            if (c == ' ') break;
            tester.append(c);
        }

        try {
            Integer.parseInt(tester.toString());
            this.testLength = tester.length();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String consume() {
        String removed = this.contents.substring(0, this.testLength);
        this.contents = this.contents.substring(this.testLength);
        return removed;
    }

    public Integer consumeInt() {
        String removed = this.contents.substring(0, this.testLength);
        this.contents = this.contents.substring(this.testLength);
        return Integer.parseInt(removed);
    }

    public String nextToken() {
        return this.contents.split(" ")[0];
    }

    public boolean isEmpty() {
        return this.contents.trim().isEmpty();
    }

    public Material parseItem() {
        if (this.isEmpty()) return null;
        for (Material material : Material.values()) {
            if (this.test(material.name().replace("_", " ").toLowerCase())) {
                this.consume();
                return material;
            }
            if (this.test(material.name().replace("_", " ").toLowerCase() + "s")) {
                this.consume();
                return material;
            }
        }
        return null;
    }

    public void trim() {
        this.contents = this.contents.trim();
    }
}