package com.netflix;

import com.netflix.entities.Media;
import com.netflix.entities.Movie;
import com.netflix.entities.TvShow;
import com.netflix.entities.User;
import com.netflix.services.LoginService;
import com.netflix.services.MediaService;
import com.netflix.services.UserService;
import com.netflix.utils.ConsoleMessage;
import com.netflix.utils.InputValidator;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.List;

public class NexflixApp {
    private final LoginService loginService;
    private final UserService userService;
    private final MediaService mediaService;

    public NexflixApp(LoginService loginService, UserService userService, MediaService mediaService) {
        this.loginService = loginService;
        this.userService = userService;
        this.mediaService = mediaService;
    }

    /**
     * Runs the main application loop.<p>
     * Displays the login menu options and performs the selected action.
     * If no user is logged in, it prints the welcome message and logo.
     * The loop continues until the user chooses to exit.
     */
    public void run() {
        while (true) {
            if (loginService.getLoggedInUser() == null) {
                ConsoleMessage.printLogo();
                ConsoleMessage.print("Bem-vindo(a) à Netflix! ");
            }

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

    /**
     * Handles the login process.<p>
     * If successful, it displays the logged-in user information and menu options.
     * If an error occurs, it prints the error message.
     */
    private void performLogin() {
        ConsoleMessage.println("--------------------\nLogin\n--------------------");

        try {
            String email = InputValidator.getString("Digite seu e-mail:");
            String password = InputValidator.getString("Digite sua senha:");

            loginService.login(email, password);

            showLoggedUserInformation();
            displayMenuOptions();
        } catch (Exception e) {
            ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
        }
    }

    /**
     * Handles the user registration process.<p>
     * If successful, it prints a success message and proceeds to log the user in.
     * If an error occurs, it prints the error message.
     */
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

    /**
     * Displays the main menu options for the logged-in user.<p>
     * If the user is an admin, additional admin options are shown.
     * The loop continues until the user logs out or selects an invalid option.
     */
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

                    loginService.logout();
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
                        loginService.logout();
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

    /**
     * Displays the list of movies and provides options to the user.<p>
     * Options include selecting a movie to view its details or exiting the list.
     * If no movies are available, a message is displayed and the method returns.
     * The loop continues until the user chooses to exit.
     */
    private void displayMovieListOptions() {
        List<Media> movies = mediaService.getAllMovies();

        if (movies.isEmpty()) {
            ConsoleMessage.println("\nNenhum registro encontrado.\n", Ansi.Color.RED);
            return;
        }

        while (true) {
            ConsoleMessage.printMovieCatalogTitle();
            showMediaList(movies);

            switch (InputValidator.getInteger(getMediaListOptions())) {
                case 1:
                    Media movie = mediaService.getMediaById((long) InputValidator.getInteger("Escolha um filme (número)"));

                    if (movie == null) {
                        ConsoleMessage.printInvalidOptionMessage();
                        break;
                    }

                    displayMediaOptions(movie);
                    break;
                case 2:
                    return;
                default:
                    ConsoleMessage.printInvalidOptionMessage();
                    break;
            }
        }
    }

    /**
     * Displays the list of tv shows and provides options to the user.<p>
     * Options include selecting a tv show to view its details or exiting the list.
     * If no tv shows are available, a message is displayed and the method returns.
     * The loop continues until the user chooses to exit.
     */
    private void displayTvShowListOptions() {
        List<Media> tvShows = mediaService.getAllTvShows();

        if (tvShows.isEmpty()) {
            ConsoleMessage.println("\nNenhum registro encontrado.\n", Ansi.Color.RED);
            return;
        }

        while (true) {
            ConsoleMessage.printTvShowCatalogTitle();
            showMediaList(tvShows);

            switch (InputValidator.getInteger(getMediaListOptions())) {
                case 1:
                    Media tvShow = mediaService.getMediaById((long) InputValidator.getInteger("Escolha uma série (número)"));

                    if (tvShow == null) {
                        ConsoleMessage.printInvalidOptionMessage();
                        break;
                    }

                    displayMediaOptions(tvShow);
                    break;
                case 2:
                    return;
                default:
                    ConsoleMessage.printInvalidOptionMessage();
                    break;
            }
        }
    }

    /**
     * Displays the details of a selected media item and provides further options.
     * The media information is displayed, and the user is prompted to choose an option.
     * <p>
     * Options include:<p>
     * 1. Displaying watching options for a movie or displaying season options for a TV show.<p>
     * 2. Returning to the previous menu.
     * <p>
     * The loop continues until the user selects a valid option.
     * <p>
     *
     * @param media The selected media item (Movie or TvShow).
     */
    private void displayMediaOptions(Media media) {
        ConsoleMessage.println(media.getInformation());

        while (true) {
            switch (InputValidator.getInteger(getMediaOptions())) {
                case 1:
                    if (media instanceof Movie) displayWatchingOptions();
                    else displayTvShowSeasonsOptions((TvShow) media);

                    return;
                case 2:
                    return;
                default:
                    ConsoleMessage.printInvalidOptionMessage();
                    break;
            }
        }
    }

    /**
     * Displays the options for selecting a season and episode of a TV show. <p>
     * Allows the user to choose a season from the available seasons of the TV show.
     * Once a season is chosen, displays the episodes of that season and prompts the user to choose an episode.
     * <p>
     * After selecting an episode, displays the watching options.
     * <p>
     * The method loops until the user selects valid options for both season and episode.
     *
     * @param tvShow The TV show for which to display seasons and episodes.
     */
    private void displayTvShowSeasonsOptions(TvShow tvShow) {
        List<String> episodeList = new ArrayList<>();
        boolean selectingSeason = true;

        while (selectingSeason) {
            ConsoleMessage.println("Escolha uma temporada:");
            tvShow.getSeasons().forEach((season, episodes) -> {
                ConsoleMessage.println("[" + season + "] Temporada " + season);
            });

            episodeList = tvShow.getSeasons().get(InputValidator.getInteger(""));

            if (episodeList == null) {
                ConsoleMessage.printInvalidOptionMessage();
                break;
            } else {
                selectingSeason = false;
            }
        }

        while (true) {
            ConsoleMessage.println("Escolha um episódio:");
            for (int i = 0; i < episodeList.size(); i++) {
                ConsoleMessage.println("[" + (i + 1) + "] " + episodeList.get(i));
            }

            if (episodeList.get(InputValidator.getInteger("") - 1).isEmpty()) {
                ConsoleMessage.printInvalidOptionMessage();
                break;
            } else {
                displayWatchingOptions();
                return;
            }
        }
    }

    /**
     * Displays options for watching a media item, such as a movie or TV show.
     * Allows the user to toggle between playing and pausing the media.
     * <p>
     * The method loops indefinitely until the user chooses to exit.
     * <p>
     * Options include: <p>
     * 1. Toggle between play and pause.<p>
     * 2. Return to the previous menu.
     */
    private void displayWatchingOptions() {
        boolean isPaused = false;

        while (true) {
            if (isPaused) ConsoleMessage.printTvPaused();
            else ConsoleMessage.printTvRunning();

            switch (InputValidator.getInteger(getMediaWatchingOptions(isPaused))) {
                case 1:
                    isPaused = !isPaused;
                    break;
                case 2:
                    return;
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

    /**
     * Displays a list of media items in a formatted ASCII table.
     * The list is divided into rows of up to three media items per row.
     * Each row displays the ID and title of the media items.
     *
     * @param mediaList The list of media items to display.
     */
    private void showMediaList(List<Media> mediaList) {
        AsciiTable table = new AsciiTable();
        table.addRule();

        for (int i = 0; i < mediaList.size(); i += 3) {
            String media1 = "[" + mediaList.get(i).getId() + "] " + mediaList.get(i).getTitle();
            String media2 = (i + 1 < mediaList.size()) ? "[" + mediaList.get(i + 1).getId() + "] " + mediaList.get(i + 1).getTitle() : "";
            String media3 = (i + 2 < mediaList.size()) ? "[" + mediaList.get(i + 2).getId() + "] " + mediaList.get(i + 2).getTitle() : "";

            table.addRow(media1, media2, media3);
            table.addRule();
        }

        table.setTextAlignment(TextAlignment.LEFT);
        ConsoleMessage.println(table.render());
    }

    /**
     * Displays the information of the currently logged-in user in an ASCII table format.
     */
    private void showLoggedUserInformation() {
        AsciiTable asciiTable = new AsciiTable();

        asciiTable.addRule();
        asciiTable.addRow("Nome", "E-mail");
        asciiTable.addRule();
        asciiTable.addRow(loginService.getLoggedInUser().getName(), loginService.getLoggedInUser().getEmail());
        asciiTable.addRule();

        asciiTable.setTextAlignment(TextAlignment.CENTER);

        ConsoleMessage.println("Usuário logado:");
        ConsoleMessage.println(asciiTable.render(), Ansi.Color.GREEN);
    }

    /**
     * @return A formatted string of login menu options.
     */
    private String getLoginMenuOptions() {
        return "[1] Login\n" +
                "[2] Cadastrar novo usuário\n" +
                "[3] Sair";
    }

    /**
     * @return A formatted string of main menu options based on the user's role
     */
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

    /**
     * @return A formatted string of options for interacting with a media item.
     */
    private String getMediaOptions() {
        return "\n[1] Assistir\n[2] Voltar";
    }

    /**
     * @return A formatted string of options for selecting an item from a media list.
     */
    private String getMediaListOptions() {
        return "[1] Escolher\n[2] Voltar";
    }

    /**
     * Returns a formatted string of options for watching a media item.
     * Includes options to pause or resume watching and to exit.
     *
     * @param isPaused Indicates if the media is currently paused.
     * @return Formatted string of media watching options.
     */
    private String getMediaWatchingOptions(boolean isPaused) {
        return "[1] " + (isPaused ? "Despausar" : "Pausar") + "\n[2] Sair";
    }
}
