package de.craftery.parser.helper;

import de.craftery.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Options {
    private static final Map<String, String> options = new HashMap<>();

    public static void registerOption(String key, String value) {
        if (options.containsKey(key)) {
            Main.log(Level.WARNING, "Options", "Option for key is already existing: " + key);
            System.exit(1);
            return;
        }
        options.put(key, value);
    }

    public static String getOption(String key) {
        String value = options.get(key);
        if (value == null) {
            Main.log(Level.WARNING, "Options", "This option is not registered (yet)!: " + key);
            System.exit(1);
        }
        return value;
    }
}
