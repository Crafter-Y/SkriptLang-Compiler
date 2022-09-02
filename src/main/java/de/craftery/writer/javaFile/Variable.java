package de.craftery.writer.javaFile;

import lombok.Data;

@Data
public class Variable {
    private String name;
    private AccessLevel accessLevel = null;
    private boolean isStatic = false;
    private boolean isFinal = false;
    private String type;
    private String value = null;

    public String build() {
        StringBuilder builder = new StringBuilder();
        if (accessLevel != null) {
            builder.append(accessLevel.getTranslation());
            builder.append(" ");
        }

        if (isStatic) {
            builder.append("static ");
        }
        if (isFinal) {
            builder.append("final ");
        }
        builder.append(type);
        builder.append(" ");
        builder.append(name);
        if (value != null) {
            builder.append(" = ");
            builder.append(value);
        }
        builder.append(";");
        return builder.toString();
    }

}
