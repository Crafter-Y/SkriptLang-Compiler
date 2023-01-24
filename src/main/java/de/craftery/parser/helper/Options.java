package de.craftery.parser.helper;

import de.craftery.Main;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class Options {
    @Setter
    private static Map<String, String> options = new HashMap<>();

    public static void registerOption(String key, String value) {
        if (options.containsKey(key)) {
            Main.exit("Option for key is already existing: " + key);
            return;
        }
        options.put(key, value);
    }

    public static String getOption(String key) {
        String value = options.get(key);
        if (value == null) {
            Main.exit("This option is not registered (yet)!: " + key);
        }
        return value;
    }
}
