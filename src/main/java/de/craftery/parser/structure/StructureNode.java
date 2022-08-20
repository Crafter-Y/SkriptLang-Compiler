package de.craftery.parser.structure;

public abstract class StructureNode {
    public abstract void acceptLine(String line, int indentation);
    public abstract StructureNode initialize(String line);

    public void reportUnknownToken(String line, String token, int index) {
        System.err.println("Unknown token: '" + token + "'");
        System.err.println(line);
        StringBuilder indicateLine = new StringBuilder();
        String[] splitLine = line.split(" ");
        for (int i = 0; i < splitLine.length; i++) {
            if (i != 0) {
                indicateLine.append(" ");
            }
            if (i == index) {
                indicateLine.append("^".repeat(token.length()));
            } else {
                indicateLine.append(" ".repeat(token.length()));
            }
        }
        System.err.println(indicateLine.toString());
        System.exit(1);
    }
}
