package de.craftery;

import java.util.logging.Level;

public class TimeUtils {
    public static long parseTime(String time) {
        long timeInMs = 0L;

        String[] splitTime = time.split(" ");
        if (splitTime.length != 2) {
            Main.log(Level.WARNING, "TimeUtils", "Invalid time format: " + time);
            Main.log(Level.WARNING, "TimeUtils", "Expected 2 arguments, got " + splitTime.length);
            return timeInMs;
        }

        int baseNumber;
        try {
            baseNumber = Integer.parseInt(splitTime[0]);
        } catch (NumberFormatException e) {
            Main.log(Level.WARNING, "TimeUtils", "Invalid time format: " + time);
            Main.log(Level.WARNING, "TimeUtils", "Expected integer, got " + splitTime[0]);
            return timeInMs;
        }

        String timeUnit = splitTime[1];

        switch (timeUnit) {
            case "seconds":
                timeInMs = baseNumber * 1000L;
                break;
            default:
                Main.log(Level.WARNING, "TimeUtils", "Invalid time format: " + time);
                Main.log(Level.WARNING, "TimeUtils", "Unknown time unit: " + timeUnit);
                return timeInMs;
        }

        return timeInMs;
    }
}
