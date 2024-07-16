package com.netflix.utils;

/**
 * Utility class for formatting strings and durations.
 */
public class Formatter {

    private static final int TITLE_WIDTH = 39;
    private static final int STATUS_SPACES = 19;

    /**
     * Formats a duration in seconds into a HH:MM:SS string format.
     *
     * @param seconds The duration in seconds to format.
     * @return A formatted string representing the duration in HH:MM:SS format.
     */
    public static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    /**
     * Formats a title for display in a console, truncating if necessary.
     *
     * @param title The title to format.
     * @return A formatted string suitable for console display.
     */
    public static String formatTitle(String title) {
        return title.length() > TITLE_WIDTH ? title.substring(0, TITLE_WIDTH - 3) + "..." :
                String.format("%-" + TITLE_WIDTH + "s", title);
    }

    /**
     * Formats the status line (ASSISTINDO or PAUSADO) for the ASCII art.
     *
     * @param status The status to format (ASSISTINDO or PAUSADO).
     * @return A formatted string representing the status line.
     */
    public static String formatStatusLine(String status) {
        String formattedStatus = " " + status + " ";
        int statusWidth = formattedStatus.length();
        int spaces = STATUS_SPACES - statusWidth;

        return "-".repeat(Math.max(0, spaces)) +
                formattedStatus +
                "-".repeat(Math.max(0, spaces));
    }
}
