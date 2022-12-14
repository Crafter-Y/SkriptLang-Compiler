package de.craftery.autogenerated;

import org.bukkit.Location;

public final class Formatter {
    public static String formatLocation(Location location) {
        return "x=" + location.getX() + ", y=" + location.getY() + ", z=" + location.getZ()+ ", yaw=" + location.getYaw()+ ", pitch=" + location.getPitch()+ ", world=" + location.getWorld().getName();
    }
    
    public static String formatUnknown(Object object) {
        if (object instanceof Location) {
            return formatLocation((Location) object);
        }
        return object.toString();
    }
    
}

