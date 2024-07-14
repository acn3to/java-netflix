import java.util.InputMismatchException;
import java.util.Scanner;

import entities.User;
import services.LoginService;
import services.MediaService;
import services.UserService;


public class NetflixApp {

    private final UserService userService;
    private final MediaService mediaService;
    private final LoginService authenticateUser;
    private User loggedInUser;

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
                System.out.println("2. Exit Netflix");
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
                            clearConsole();
                            System.out.println("Exiting NetflixApp CLI. Goodbye!");
                            return;
                        default:
                            clearConsole();
                            System.out.println("Invalid option. Please choose again.");
                            break;
                    }

                } catch (InputMismatchException e) {
                    clearConsole();
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine();
                }
            }
        }
    }


    private void performLogin(Scanner scanner) {
        clearConsole();
        // Prompt for username
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        // Prompt for password (for simplicity, handle securely in production)
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Perform login using LoginService and call searchMenu method
        boolean loggedIn = authenticateUser.authenticate(username, password);
        if (loggedIn) {
            loggedInUser = authenticateUser.getLoggedInUser();
            System.out.println("Login successful! Welcome, " + loggedInUser.getName());
            searchMenu(scanner);

        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }


    private void searchMenu(Scanner scanner) {
        clearConsole();
        while (true) {
            System.out.println("====== Welcome " + loggedInUser.getName() + " ======");
            System.out.println("1. Search for a movie");
            System.out.println("2. Search for a TV show");
            System.out.println("3. Logoff");
            System.out.println("4. Exit Netflix");
            System.out.print("Choose an option: ");

            try {

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        // Search for a movie
                        searchMovie();

                    case 2:
                        // Search for a TV show


                    case 3:
                        // Logoff
                        run();
                    case 4:
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
    public void searchMovie() {
        clearConsole();
        System.out.println("====== Movie by Netflix ======");
        System.out.println("1. Search by category");
        System.out.println("2. Search by name");
        System.out.println("3. Logoff");
        System.out.println("4. Exit Netflix");
        System.out.print("Choose an option: ");
        Scanner scanner = new Scanner(System.in);
        try {

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            while (true) {

                switch (choice) {
                    case 1:
                        //To be implemented

                    case 2:
                        //To be implemented

                    case 3:
                        // Logoff
                        run();
                    case 4:
                        // Exit the application
                        System.out.println("Exiting NetflixApp CLI. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option. Please choose again.");
                        break;
                }
            }
        }
             catch(InputMismatchException e){
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }


        }
    public void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
    }
