package de.craftery.parser.structure;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommandArgument {
    private boolean optional;
    private Type type;

    public enum Type {
        STRING,
        OFFLINE_PLAYER
    }
}
