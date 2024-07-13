import java.util.InputMismatchException;
import java.util.Scanner;

import services.LoginService;
import services.MediaService;
import services.UserService;


public class NetflixApp {

    private final UserService userService;
    private final MediaService mediaService;
    private final LoginService authenticateUser;

    public NetflixApp(UserService userService, MediaService mediaService, LoginService authenticateUser) {
        this.userService = userService;
        this.mediaService = mediaService;
        this.authenticateUser = authenticateUser;
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {

            // Display welcome message or initial prompt
            System.out.println("Welcome to NetflixApp CLI");

            // Simulate user interaction
            while (true) {
                // Display menu options
                System.out.println("1. Login");
                System.out.println("2. Exit");
                System.out.print("Choose an option: ");

                // Read user input
                try {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (choice) {
                        case 1:
                            // Perform login
                            performLogin(scanner);
                            break;
                        case 2:
                            // Exit the application
                            System.out.println("Exiting NetflixApp CLI. Goodbye!");
                            return;
                        default:
                            System.out.println("Invalid option. Please choose again.");
                            break;
                    }

                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine();
                }
            }
        }
    }


    private void performLogin(Scanner scanner) {
        // Prompt for username

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        // Prompt for password (for simplicity, handle securely in production)
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Perform login using LoginService
        boolean loggedIn = authenticateUser.authenticate(username, password);
        if (loggedIn) {
            System.out.println("Login successful!");

        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }
}