package com.netflix.utils;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Utility class for printing messages to the console with optional colors using ANSI escape codes.
 */
public class ConsoleMessage {
    /**
     * Prints a message to the console without changing the line.
     *
     * @param message The message to be printed.
     */
    public static void print(String message) {
        System.out.print(message);
    }

    /**
     * Prints a colored message to the console without changing the line.
     *
     * @param message The message to be printed.
     * @param color   The ANSI color to apply to the message.
     */
    public static void print(String message, Ansi.Color color) {
        System.out.print(ansi().eraseScreen().fg(color).a(message).reset());
    }

    /**
     * Prints a message to the console and then terminates the line.
     *
     * @param message The message to be printed.
     */
    public static void println(String message) {
        System.out.println(message);
    }

    /**
     * Prints a colored message to the console and then terminates the line.
     *
     * @param message The message to be printed.
     * @param color   The ANSI color to apply to the message.
     */
    public static void println(String message, Ansi.Color color) {
        System.out.println(ansi().eraseScreen().fg(color).a(message).reset());
    }

    /**
     * Prints a default invalid option message to the console in red.
     * Typically used when the user selects an invalid option.
     */
    public static void printInvalidOptionMessage() {
        ConsoleMessage.println("Opção inválida! Tente novamente.", Ansi.Color.RED);
    }

    /**
     * Prints the logo app to the console.
     */
    public static void printLogo() {
        ConsoleMessage.println("\n" +
                        "███    ██ ███████ ████████ ███████ ██      ██ ██   ██ \n" +
                        "████   ██ ██         ██    ██      ██      ██  ██ ██  \n" +
                        "██ ██  ██ █████      ██    █████   ██      ██   ███   \n" +
                        "██  ██ ██ ██         ██    ██      ██      ██  ██ ██  \n" +
                        "██   ████ ███████    ██    ██      ███████ ██ ██   ██ \n",
                Ansi.Color.RED
        );
    }

    /**
     * Prints the movie catalog title to the console.
     */
    public static void printMovieCatalogTitle() {
        ConsoleMessage.println("\n" +
                "   _____      _        _                         _         __ _ _                     \n" +
                "  / ____|    | |      | |                       | |       / _(_) |                    \n" +
                " | |     __ _| |_ __ _| | ___   __ _  ___     __| | ___  | |_ _| |_ __ ___   ___  ___ \n" +
                " | |    / _` | __/ _` | |/ _ \\ / _` |/ _ \\   / _` |/ _ \\ |  _| | | '_ ` _ \\ / _ \\/ __|\n" +
                " | |___| (_| | || (_| | | (_) | (_| | (_) | | (_| |  __/ | | | | | | | | | |  __/\\__ \\\n" +
                "  \\_____\\__,_|\\__\\__,_|_|\\___/ \\__, |\\___/   \\__,_|\\___| |_| |_|_|_| |_| |_|\\___||___/\n" +
                "                                __/ |                                                 \n" +
                "                               |___/                                                  \n"
        );
    }

    /**
     * Prints the TV show catalog title to the console.
     */
    public static void printTvShowCatalogTitle() {
        ConsoleMessage.println("\n" +
                "   _____      _        _                         _                      _           \n" +
                "  / ____|    | |      | |                       | |                    (_)          \n" +
                " | |     __ _| |_ __ _| | ___   __ _  ___     __| | ___   ___  ___ _ __ _  ___  ___ \n" +
                " | |    / _` | __/ _` | |/ _ \\ / _` |/ _ \\   / _` |/ _ \\ / __|/ _ \\ '__| |/ _ \\/ __|\n" +
                " | |___| (_| | || (_| | | (_) | (_| | (_) | | (_| |  __/ \\__ \\  __/ |  | |  __/\\__ \\\n" +
                "  \\_____\\__,_|\\__\\__,_|_|\\___/ \\__, |\\___/   \\__,_|\\___| |___/\\___|_|  |_|\\___||___/\n" +
                "                                __/ |                                               \n" +
                "                               |___/                                                \n");
    }

    /**
     * Prints a TV or movie message to the console with dynamic status (ASSISTINDO or PAUSADO).
     *
     * @param title         The title of the episode or movie.
     * @param isPaused      Whether the media is paused or not.
     * @param elapsedTime   The elapsed time in seconds.
     * @param totalTime     The total time of the media in seconds.
     */
    public static void printTv(String title, boolean isPaused, int elapsedTime, int totalTime) {
        String status = isPaused ? "PAUSED" : "PLAYING";
        String elapsedTimeFormatted = Formatter.formatTime(elapsedTime);
        String totalTimeFormatted = Formatter.formatTime(totalTime);

        String formattedStatusLine = Formatter.formatStatusLine(status);
        String formattedTitle = Formatter.formatTitle(title);

        System.out.println("┌──────────────────────────────────────────────────┐");
        System.out.println("│                o                                 │");
        System.out.println("│           o    |                                 │");
        System.out.println("│            \\   |                                 │");
        System.out.println("│             \\  |                                 │");
        System.out.println("│              \\.|-                                │");
        System.out.println("│              (\\|  )                              │");
        System.out.println("│    .=========================================.   │");
        System.out.println("│    | .-------------------------------------. |   │");
        System.out.println("│    | |--.__.--.__.-------------------------| |   │");
        System.out.println("│    | |--.__.--.__.-------------------------| |   │");
        System.out.println("│    | |--.-" + formattedStatusLine + "---- --| |   │");
        System.out.println("│    | |--.__.--.__.-------------------------| |   │");
        System.out.println("│    | |--.__.--.__.-------------------------| |   │");
        System.out.println("│    | '-------------------------------------'o|   │");
        System.out.println("│    | " + formattedTitle + " │   │");
        System.out.println("│    | " + elapsedTimeFormatted + " - " + totalTimeFormatted + "                     │   │");
        System.out.println("│    |                                       │o|   │");
        System.out.println("│    '========================================='   │");
        System.out.println("│                                                  │");
        System.out.println("└──────────────────────────────────────────────────┘");
    }
}
