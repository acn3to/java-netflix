package com.netflix;

import com.netflix.entities.User;
import com.netflix.services.LoginService;
import com.netflix.services.MediaService;
import com.netflix.services.UserService;
import com.netflix.utils.ConsoleMessage;
import com.netflix.utils.InputValidator;
import org.fusesource.jansi.Ansi;

public class NexflixApp {
    private LoginService loginService;
    private UserService userService;
    private MediaService mediaService;

    public NexflixApp(LoginService loginService, UserService userService, MediaService mediaService) {
        this.loginService = loginService;
        this.userService = userService;
        this.mediaService = mediaService;
    }

    public void run() {
        ConsoleMessage.printLogo();
        ConsoleMessage.print("Bem-vindo(a) à Netflix! ");

        while (true) {
            ConsoleMessage.println("Acesse uma opção abaixo:\n");

            switch (InputValidator.getInteger(getLoginMenuOptions())) {
                case 1:
                    performLogin();
                    break;
                case 2:
                    performUserRegistration();
                    break;
                case 3:
                    return;
                default:
                    ConsoleMessage.printInvalidOptionMessage();
                    break;
            }
        }
    }

    private void performLogin() {
        ConsoleMessage.println("--------------------\nLogin\n--------------------");

        try {
            String email = InputValidator.getString("Digite seu e-mail:");
            String password = InputValidator.getString("Digite sua senha:");

            loginService.login(email, password);
            displayMenuOptions();
        } catch (Exception e) {
            ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
        }
    }

    private void performUserRegistration() {
        ConsoleMessage.println("------------------------\nCadastrar novo usuário\n------------------------");

        try {
            String name = InputValidator.getString("Digite seu nome:");
            String email = InputValidator.getString("Digite seu e-mail:");
            String password = InputValidator.getString("Digite sua senha:");

            userService.addUser(new User(name, email, password));
            ConsoleMessage.println("Usuário cadastrado com sucesso!", Ansi.Color.GREEN);

            performLogin();
        } catch (Exception e) {
            ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
        }
    }

    private void displayMenuOptions() {
        boolean showAdminOptions = loginService.getLoggedInUser().isAdmin();

        while (true) {
            ConsoleMessage.println("Navegue pelas opções abaixo:");

            switch (InputValidator.getInteger(getMenuOptions())) {
                case 1:
                    displayMovieListOptions();
                    break;
                case 2:
                    displayTvShowListOptions();
                    break;
                case 3:
                    if (showAdminOptions) {
                        displayMovieCrudOptions();
                        break;
                    }

                    return;
                case 4:
                    if (showAdminOptions) {
                        displayTvShowCrudOptions();
                        break;
                    }

                    ConsoleMessage.printInvalidOptionMessage();
                    break;
                case 5:
                    if (showAdminOptions) {
                        return;
                    }

                    ConsoleMessage.printInvalidOptionMessage();
                    break;
                default:
                    ConsoleMessage.printInvalidOptionMessage();
                    break;
            }
        }
    }

    private void displayTvShowCrudOptions() {
    }

    private void displayMovieCrudOptions() {
    }

    private void displayMovieListOptions() {
    }

    private void displayTvShowListOptions() {
    }

    private String getLoginMenuOptions() {
        return "[1] Login\n" +
                "[2] Cadastrar novo usuário\n" +
                "[0] Sair";
    }

    private String getMenuOptions() {
        var defaultOptions = "[1] Visualizar catálogo de filmes\n" +
                "[2] Visualizar catálogo de séries\n";

        if (loginService.getLoggedInUser().isAdmin()) {
            defaultOptions = defaultOptions +
                    "[3] Gerenciar filmes\n" +
                    "[4] Gerenciar séries\n";
        }

        return defaultOptions + (loginService.getLoggedInUser().isAdmin() ? "[5] Logout" : "[3] Logout");
    }
}
