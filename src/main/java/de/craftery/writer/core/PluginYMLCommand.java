package de.craftery.writer.core;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PluginYMLCommand {
    private String description;
    private final List<String> aliases = new ArrayList<>();
    private String permission;
    private String permissionMessage;
    private String usage;
}
