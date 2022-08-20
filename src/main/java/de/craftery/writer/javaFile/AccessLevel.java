package de.craftery.writer.javaFile;

import lombok.Getter;

public enum AccessLevel {
    PUBLIC("public"),
    PROTECTED("protected"),
    PRIVATE("private");

    @Getter
    private final String translation;

    AccessLevel(String translation) {
        this.translation = translation;
    }
}
