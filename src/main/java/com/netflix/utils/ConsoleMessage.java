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
}
