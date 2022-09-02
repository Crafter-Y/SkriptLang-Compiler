package de.craftery.writer.javaFile;

import de.craftery.writer.WritingSection;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ClassSection extends WritingSection {
    public ClassSection(int indentationLevel) {
        super(indentationLevel);
    }

    @Setter
    private AccessLevel accessLevel = AccessLevel.PUBLIC;

    @Setter
    private String className = "";

    @Setter
    private boolean finalModifier = false;

    @Setter
    private boolean abstractModifier = false;

    @Setter
    private String extendsClass = "";

    private final List<String> implementsInterfaces = new ArrayList<>();

    @Getter
    private final List<RawMethodSection> methods = new ArrayList<>();

    @Getter
    private final List<Variable> variables = new ArrayList<>();

    public void addMethod(RawMethodSection method) {
        this.methods.add(method);
    }

    public void addVariable(Variable variable) {
        this.variables.add(variable);
    }

    public void addImplementationClass(String className) {
        this.implementsInterfaces.add(className);
    }

    private String getImplementingInterfaces() {
        if (implementsInterfaces.size() == 0) {
            return "";
        }
        StringBuilder implementStringBuilder = new StringBuilder();
        implementStringBuilder.append("implements ");

        for (int i = 0; i < implementsInterfaces.size(); i++) {
            if (i != 0) {
                implementStringBuilder.append(", ");
            }
            implementStringBuilder.append(implementsInterfaces.get(i));
        }
        implementStringBuilder.append(" ");
        return implementStringBuilder.toString();
    }

    @Override
    public void buildLines() {
        this.getLines().add(
                accessLevel.getTranslation() + " " +
                        (finalModifier ? "final " : "") +
                        (abstractModifier ? "abstract " : "") +
                        "class " + className + " " +
                        (extendsClass.isEmpty() ? "" : "extends " + extendsClass + " ") +
                        getImplementingInterfaces() +
                        "{");
        for (Variable variable : variables) {
            String variableBuilder = "    ".repeat(Math.max(0, this.getIndentationLevel() + 1)) +
                    variable.build();
            this.getLines().add(variableBuilder);
        }
        if (variables.size() != 0) {
            this.getLines().add("");
        }
        for (RawMethodSection method : methods) {
            this.getLines().addAll(method.getIndentedLines());
        }
        this.getLines().add("}");
        this.getLines().add("");
    }
}
