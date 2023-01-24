package de.craftery;

public class TimeUtils {
    public static long parseTime(String time) {
        long timeInMs = 0L;

        String[] splitTime = time.split(" ");
        if (splitTime.length != 2) {
            Main.warn("Invalid time format: " + time);
            Main.warn("Expected 2 arguments, got " + splitTime.length);
            return timeInMs;
        }

        int baseNumber;
        try {
            baseNumber = Integer.parseInt(splitTime[0]);
        } catch (NumberFormatException e) {
            Main.warn("Invalid time format: " + time);
            Main.warn("Expected integer, got " + splitTime[0]);
            return timeInMs;
        }

        String timeUnit = splitTime[1];

        switch (timeUnit) {
            case "seconds":
                timeInMs = baseNumber * 1000L;
                break;
            default:
                Main.warn("Invalid time format: " + time);
                Main.warn( "Unknown time unit: " + timeUnit);
                return timeInMs;
        }

        return timeInMs;
    }
}
