package com.netflix.utils;


import com.netflix.entities.Category;
import org.fusesource.jansi.Ansi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Assistant class to validate user data input through the Scanner.
 */
public final class InputValidator {
    public static Scanner sc = new Scanner(System.in);

    /**
     * Requests and retrieves a positive integer value from the user via the console.
     * Displays a message to prompt the user for input.
     * Returns the positive integer value entered by the user after validation.
     *
     * @param message The message to display prompting the user for input.
     * @return The positive integer value entered by the user.
     */
    public static int getInteger(String message) {
        ConsoleMessage.println(message);

        while (true) {
            try {
                int value = sc.nextInt();

                if (value <= 0) {
                    ConsoleMessage.println("Insira somente valores inteiros positivos:", Ansi.Color.RED);
                    sc.nextLine();
                } else {
                    sc.nextLine();
                    return value;
                }
            } catch (InputMismatchException e) {
                ConsoleMessage.println("Valor inválido! Insira somente valores inteiros positivos:", Ansi.Color.RED);
                sc.nextLine();
            }
        }
    }

    /**
     * Requests and retrieves a positive double value from the user via the console.
     * Displays a message to prompt the user for input.
     * Returns the positive integer value entered by the user after validation.
     *
     * @param message The message to display prompting the user for input.
     * @return The double value entered by the user.
     */
    public static double getDouble(String message) {
        ConsoleMessage.println(message);

        while (true) {
            try {
                double value = sc.nextDouble();

                if (value <= 0.0) {
                    ConsoleMessage.println("Insira somente valores positivos, utilize vírgula:", Ansi.Color.RED);
                    sc.nextLine();
                } else {
                    sc.nextLine();
                    return value;
                }
            } catch (InputMismatchException e) {
                ConsoleMessage.println("Valor inválido! Insira somente valores positivos, utilize vírgula:", Ansi.Color.RED);
                sc.nextLine();
            }
        }
    }

    /**
     * Requests and retrieves a string input from the user via the console.
     * Displays a message to prompt the user for input.
     * Returns the string entered by the user.
     *
     * @param message The message to display prompting the user for input.
     * @return The string entered by the user.
     */
    public static String getString(String message) {
        ConsoleMessage.println(message);
        sc.useDelimiter("\n");

        return sc.nextLine();
    }

    /**
     * Obtains a {@link LocalDate} object from a user input string.
     * This method prompts the user with the provided message and expects a date
     * in the format "dd/MM/yyyy". If the input format is invalid, it will ask the
     * user to try again until a valid date is provided.
     *
     * @param message The message to be displayed to the user when prompting for the date.
     * @return A {@link LocalDate} object representing the date entered by the user.
     */
    public static LocalDate getLocalDate(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (true) {
            try {
                String entry = getString(message);

                return LocalDate.parse(entry, formatter);
            } catch (Exception e) {
                ConsoleMessage.println("Formato inválido. Tente novamente utilizando o formato dd/MM/yyyy.", Ansi.Color.RED);
            }
        }
    }

    /**
     * Prompts the user to choose a category from a list of available categories.
     * Displays the categories with their descriptions.
     *
     * @param message The message to display when asking the user to choose a category.
     * @return The chosen Category.
     */
    public static Category getCategory(String message) {
        ConsoleMessage.println("Categorias: ");

        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            ConsoleMessage.println("[" + (i + 1) + "] " + categories[i].getDescription());
        }

        int choice;
        while (true) {
            choice = InputValidator.getInteger(message);

            if (choice >= 1 && choice <= categories.length) {
                break;
            } else {
                ConsoleMessage.printInvalidOptionMessage();
            }
        }

        return categories[choice - 1];
    }
}