package de.craftery.parser.structure;

import de.craftery.Fragment;
import de.craftery.Main;
import lombok.Getter;
import lombok.Setter;

public abstract class StructureNode {
    public abstract void acceptLine(Fragment line, int indentation);
    public abstract StructureNode initialize(Fragment line);

    @Getter
    @Setter
    private int maxIndentation;

    public void reportUnknownToken(Fragment fragment, String token, int index) {
        this.reportUnknownToken(fragment.getContents(), token, index);
    }

    public void reportUnknownToken(String line, String token, int index) {
        Main.warn("Unknown token: '" + token + "'");
        Main.warn(line);
        StringBuilder indicateLine = new StringBuilder();
        String[] splitLine = line.trim().split(" ");
        for (int i = 0; i < splitLine.length; i++) {
            if (i != 0) {
                indicateLine.append(" ");
            }
            if (i == index) {
                indicateLine.append("^".repeat(token.length()));
            } else {
                indicateLine.append(" ".repeat(splitLine[i].length()));
            }
        }
        Main.exit(indicateLine.toString());
    }
}
