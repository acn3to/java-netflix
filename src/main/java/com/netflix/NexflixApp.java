package com.netflix;

import com.netflix.entities.*;
import com.netflix.services.LoginService;
import com.netflix.services.MediaService;
import com.netflix.services.UserService;
import com.netflix.utils.ConsoleMessage;
import com.netflix.utils.Formatter;
import com.netflix.utils.InputValidator;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

public class NexflixApp {
    private final LoginService loginService;
    private final UserService userService;
    private final MediaService mediaService;
    private boolean hasFilters;
    private boolean continueFilter;

    public NexflixApp(LoginService loginService, UserService userService, MediaService mediaService) {
        this.loginService = loginService;
        this.userService = userService;
        this.mediaService = mediaService;
        this.hasFilters = false;
        this.continueFilter = false;
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
                clearConsole();
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
        clearConsole();
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

    /**
     * Handles the user registration process.<p>
     * If successful, it prints a success message and proceeds to log the user in.
     * If an error occurs, it prints the error message.
     */
    private void performUserRegistration() {
        clearConsole();
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
            clearConsole();
            showLoggedUserInformation();
            ConsoleMessage.println("Escolha uma opção:");

            switch (InputValidator.getInteger(getMenuOptions())) {
                case 1:
                    if (showAdminOptions) {
                        displayMediaListOptions(mediaService.getAllMedia());
                        break;
                    }
                    selectProfile();
                    break;
                case 2:
                    if (showAdminOptions) {
                        displayMediaListOptions(mediaService.getAllTvShows());
                        break;
                    }
                    createProfile();
                    break;
                case 3:
                    if (showAdminOptions) {
                        displayMovieCrudOptions();
                        break;
                    }
                    removeProfile();
                    return;
                case 4:
                    if (showAdminOptions) {
                        displayTvShowCrudOptions();
                        break;
                    }
                    return;
                case 5:
                    if (showAdminOptions) {
                        loginService.logout();
                        return;
                    }
                    ConsoleMessage.printInvalidOptionMessage();
                    break;
                case 6:
                    if (loginService.getLoggedInUser() != null && !(showAdminOptions)) {
                        clearConsole();
                        displayProfileOptions();
                        break;
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
     * Displays options for interacting with a list of media items.
     * Allows selection of media, filtering, resetting filters, or exiting.
     *
     * @param mediaList The list of media items to display and interact with.
     */
    private void displayMediaListOptions(List<Media> mediaList) {
        if (mediaList.isEmpty()) {
            ConsoleMessage.println("\nNenhum registro encontrado.\n", Ansi.Color.RED);
            return;
        }

        boolean isListOfMovies = mediaList.stream().anyMatch(media -> media instanceof Movie);
        this.continueFilter = true;

        while (this.continueFilter) {
            displayCatalogTitle(isListOfMovies);
            showMediaList(mediaList);

            int option = InputValidator.getInteger(getMediaListOptions());
            handleMediaListOption(option, mediaList, isListOfMovies);
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
        clearConsole();
        ConsoleMessage.println(media.getInformation());

        while (true) {
            int choice = InputValidator.getInteger(getMediaOptions());
            switch (choice) {
                case 1:
                    if (media instanceof Movie) {
                        displayWatchingOptions(media, null);
                    } else if (media instanceof TvShow) {
                        displayTvShowSeasonsOptions((TvShow) media);
                    }
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
        List<Episode> episodeList;

        while (true) {
            ConsoleMessage.println("Escolha uma temporada:");
            tvShow.getSeasons().forEach((season, _) -> {
                ConsoleMessage.println("[" + season + "] Temporada " + season);
            });

            int seasonNumber = InputValidator.getInteger("Escolha o número da temporada:");

            episodeList = tvShow.getSeasons().get(seasonNumber);

            if (episodeList == null) {
                ConsoleMessage.printInvalidOptionMessage();
            } else {
                while (true) {
                    ConsoleMessage.println("Escolha um episódio:");
                    for (int i = 0; i < episodeList.size(); i++) {
                        ConsoleMessage.println("[" + (i + 1) + "] " + episodeList.get(i).getTitle());
                    }

                    int episodeNumber = InputValidator.getInteger("Escolha o número do episódio:") - 1;

                    if (episodeNumber < 0 || episodeNumber >= episodeList.size()) {
                        ConsoleMessage.printInvalidOptionMessage();
                    } else {
                        displayWatchingOptions(tvShow, episodeList.get(episodeNumber));
                        return;
                    }
                }
            }
        }
    }

    /**
     * Displays options for watching a media item (movie or episode), including pause/resume and exit.
     * Logs "Fim do episódio." or "Fim do filme." when the media reaches its end.
     *
     * @param media   The media item being watched (either Movie or Episode).
     * @param episode The episode being watched, if applicable.
     */
    private void displayWatchingOptions(Media media, Episode episode) {
        boolean isPaused = false;
        int startWatchingTime = (int) (System.currentTimeMillis() / 1000);
        int pauseStartTime = 0;
        int elapsedTime;
        int totalTime = (episode != null) ? episode.getDuration() * 60 : ((media instanceof Movie) ? ((Movie) media).getDurationInMinutes() * 60 : 0);

        while (true) {
            elapsedTime = isPaused ? pauseStartTime - startWatchingTime : (int) (System.currentTimeMillis() / 1000) - startWatchingTime;

            String endMessage = (episode != null) ? "Fim do episódio." : "Fim do filme.";

            if (!isPaused && episode != null) {
                if (elapsedTime >= totalTime) {
                    ConsoleMessage.println(endMessage);
                    return;
                }
                ConsoleMessage.printTv(Formatter.formatTitle(episode.getTitle()), isPaused, elapsedTime, totalTime);
            } else if (!isPaused && media instanceof Movie) {
                if (elapsedTime >= totalTime) {
                    ConsoleMessage.println(endMessage);
                    return;
                }
                ConsoleMessage.printTv(Formatter.formatTitle(media.getTitle()), isPaused, elapsedTime, totalTime);
            } else {
                String title = (episode != null) ? episode.getTitle() : media.getTitle();
                ConsoleMessage.printTv(Formatter.formatTitle(title), isPaused, elapsedTime, totalTime);
            }

            int choice = InputValidator.getInteger(getMediaWatchingOptions(isPaused));
            switch (choice) {
                case 1:
                    if (isPaused) {
                        int pauseEndTime = (int) (System.currentTimeMillis() / 1000);
                        int pauseDuration = pauseEndTime - pauseStartTime;
                        startWatchingTime += pauseDuration;
                        pauseStartTime = 0;
                    } else {
                        pauseStartTime = (int) (System.currentTimeMillis() / 1000);
                    }
                    isPaused = !isPaused;
                    break;
                case 2:
                    return;
                default:
                    ConsoleMessage.printInvalidOptionMessage();
                    break;
            }

            elapsedTime = isPaused ? pauseStartTime - startWatchingTime : (int) (System.currentTimeMillis() / 1000) - startWatchingTime;
            if (!isPaused && episode != null && elapsedTime >= totalTime) {
                ConsoleMessage.println(endMessage);
                return;
            } else if (!isPaused && media instanceof Movie && elapsedTime >= totalTime) {
                ConsoleMessage.println(endMessage);
                return;
            }
        }
    }

    /**
     * Displays the catalog title based on whether the current list of media items is movies or TV shows.
     *
     * @param isListOfMovies Indicates whether the current list of media items is movies.
     */
    private void displayCatalogTitle(boolean isListOfMovies) {
        if (isListOfMovies) {
            ConsoleMessage.printMovieCatalogTitle();
        } else {
            ConsoleMessage.printTvShowCatalogTitle();
        }
    }

    /**
     * Handles user selected options for interacting with a list of media items.
     * Allows selection of a media item for detailed display,
     * filtering the list, resetting filters, or exiting the menu.
     *
     * @param option         The selected option to handle.
     * @param mediaList      The current list of media items being interacted with.
     * @param isListOfMovies Indicates if the current media list contains movies or tv shows.
     */
    private void handleMediaListOption(int option, List<Media> mediaList, boolean isListOfMovies) {
        switch (option) {
            case 1 -> handleSelectMedia();
            case 2 -> displayFilterOptions(mediaList);
            case 3 -> resetFilters(isListOfMovies);
            case 4 -> exitMediaList();
            default -> ConsoleMessage.printInvalidOptionMessage();
        }
    }

    /**
     * Handles the selection of a media item by prompting the user to choose a media ID.
     * If no media item is found, displays an invalid option message.
     * Otherwise, displays options (watching) for the selected media item.
     */
    private void handleSelectMedia() {
        Media media = mediaService.getMediaById(InputValidator.getInteger("Escolha uma mídia (número)"));
        if (media == null) {
            ConsoleMessage.printInvalidOptionMessage();
        } else {
            displayMediaOptions(media);
        }
    }

    /**
     * Resets any applied filters and displays the full list of media items.
     * Sets the {@code hasFilters} flag to false after resetting.
     *
     * @param isListOfMovies Indicates whether the current list of media items is movies or TV shows.
     *                       Determines which set of media items to display after resetting filters.
     */
    private void resetFilters(boolean isListOfMovies) {
        this.hasFilters = false;

        if (isListOfMovies) {
            displayMediaListOptions(mediaService.getAllMovies());
        } else {
            displayMediaListOptions(mediaService.getAllTvShows());
        }
    }

    /**
     * Exits the media list interaction loop by setting {@code continueFilter} to false.
     * Also resets the {@code hasFilters} flag to false.
     */
    private void exitMediaList() {
        this.continueFilter = false;
        this.hasFilters = false;
    }

    /**
     * Displays options for filtering a list of media items.
     *
     * @param mediaList The list of media items to apply filters to.
     */
    private void displayFilterOptions(List<Media> mediaList) {
        while (continueFilter) {
            ConsoleMessage.println("Escolha uma opção de filtro:");

            int option = InputValidator.getInteger(getFilterOptions());
            handleFilterOption(option, mediaList);
        }
    }

    /**
     * Handles the selected filter options.
     *
     * @param option    The selected filter option to handle.
     * @param mediaList The list of media items to apply filters to.
     */
    private void handleFilterOption(int option, List<Media> mediaList) {
        switch (option) {
            case 1 -> applyFilterAndDisplay(() -> mediaService.filterReleaseDateInDescendingOrder(mediaList));
            case 2 -> applyFilterAndDisplay(() -> mediaService.filterReleaseDateInAscendingOrder(mediaList));
            case 3 -> applyDateRangeFilter(mediaList);
            case 4 -> applyYearAndRatingFilter(mediaList);
            case 5 -> applyCategoryFilter(mediaList);
            case 6 -> applyTitleFilter(mediaList);
            case 7 -> applyRatingFilter(mediaList);
            case 8 -> applyDirectorFilter(mediaList);
            case 9 -> displayMenuOptions();
            default -> ConsoleMessage.printInvalidOptionMessage();
        }
    }

    /**
     * Applies a filter function to the list of media items and displays the filtered results.
     * Sets the flag {@code hasFilters} to true.
     *
     * @param filterFunction A supplier that provides a filtered list of media items.
     */
    private void applyFilterAndDisplay(Supplier<List<Media>> filterFunction) {
        this.hasFilters = true;
        displayMediaListOptions(filterFunction.get());
    }

    /**
     * Prompts the user to input a date range and filters the list of media items by release date within that range.
     * Displays an error message if the initial date is after the final date.
     * Sets the flag {@code hasFilters} to true.
     *
     * @param mediaList The list of media items to filter by release date.
     */
    private void applyDateRangeFilter(List<Media> mediaList) {
        this.hasFilters = true;
        boolean keepAsking = true;

        while (keepAsking) {
            LocalDate initialDate = InputValidator.getLocalDate("Digite a data inicial:");
            LocalDate finalDate = InputValidator.getLocalDate("Digite a data final:");

            if (finalDate.isBefore(initialDate) || initialDate.isAfter(finalDate)) {
                ConsoleMessage.println("Data inicial deve ser menor que a data final!", Ansi.Color.RED);
            } else {
                keepAsking = false;
                displayMediaListOptions(mediaService.filterByReleaseDate(mediaList, initialDate, finalDate));
            }
        }
    }

    /**
     * Prompts the user to input a year and minimum rating, filters the list of media items by these criteria,
     * and displays the filtered results.
     * Sets the flag {@code hasFilters} to true.
     *
     * @param mediaList The list of media items to filter by year and minimum rating.
     */
    private void applyYearAndRatingFilter(List<Media> mediaList) {
        this.hasFilters = true;
        int year = InputValidator.getInteger("Digite o ano de lançamento:");
        double minRating = InputValidator.getDouble("Digite a nota mínima de avaliação:");

        displayMediaListOptions(mediaService.filterByYearAndRating(mediaList, year, minRating));
    }

    /**
     * Prompts the user to enter a category and filters the media item list by that category.
     * Displays all available categories with indices for user selection.
     * Sets the {@code hasFilters} flag to true.
     *
     * @param mediaList The list of media items to filter by category.
     */
    private void applyCategoryFilter(List<Media> mediaList) {
        this.hasFilters = true;

        ConsoleMessage.println("[1] Aventura");
        ConsoleMessage.println("[2] Comédia");
        ConsoleMessage.println("[3] Fantasia");
        ConsoleMessage.println("[4] Terror");
        ConsoleMessage.println("[5] Animação");
        ConsoleMessage.println("[6] Ficção científica");
        ConsoleMessage.println("[7] Drama");
        ConsoleMessage.println("[8] Romance");

        boolean validCategory = false;
        Category selectedCategory = null;

        while (!validCategory) {
            try {
                String categoryIndexString = InputValidator.getString("Selecione a categoria pelo número:");
                int categoryIndex = Integer.parseInt(categoryIndexString);

                if (categoryIndex >= 1 && categoryIndex <= 8) {
                    selectedCategory = Category.values()[categoryIndex - 1];
                    validCategory = true;
                } else {
                    ConsoleMessage.println("Número de categoria inválido! Por favor, selecione um número válido.", Ansi.Color.RED);
                }
            } catch (NumberFormatException e) {
                ConsoleMessage.println("Entrada inválida! Por favor, digite um número válido.", Ansi.Color.RED);
            }
        }

        displayMediaListOptions(mediaService.filterByCategory(mediaList, selectedCategory));
    }

    /**
     * Prompts the user to enter a title and filters the media item list by that title.
     * Sets the {@code hasFilters} flag to true.
     *
     * @param mediaList The list of media items to filter by title.
     */
    private void applyTitleFilter(List<Media> mediaList) {
        this.hasFilters = true;

        ConsoleMessage.println("Digite o título para filtrar:");

        String title = InputValidator.getString("Título: ");
        List<Media> filteredMedia = mediaService.filterByTitle(mediaList, title);

        if (filteredMedia.isEmpty()) {
            ConsoleMessage.println("Nenhum item encontrado com o título especificado.", Ansi.Color.RED);
        } else {
            displayMediaListOptions(filteredMedia);
        }
    }

    /**
     * Prompts the user to input a minimum rating and filters the list of media items by that rating.
     * Sets the flag {@code hasFilters} to true.
     *
     * @param mediaList The list of media items to filter by rating.
     */
    private void applyRatingFilter(List<Media> mediaList) {
        this.hasFilters = true;
        double minRating = InputValidator.getDouble("Digite a nota mínima de avaliação (0.0-5.0):");

        displayMediaListOptions(mediaService.filterByRating(mediaList, minRating));
    }

    /**
     * Prompts the user to select a director from the displayed list and filters the media item list by that director.
     * Sets the {@code hasFilters} flag to true.
     *
     * @param mediaList The list of media items to filter by director.
     */
    private void applyDirectorFilter(List<Media> mediaList) {
        this.hasFilters = true;

        ConsoleMessage.println("Digite o nome do diretor para filtrar:");

        String director = InputValidator.getString("Diretor: ");
        List<Media> filteredMedia = mediaService.filterByDirector(mediaList, director);

        if (filteredMedia.isEmpty()) {
            ConsoleMessage.println("Nenhum item encontrado com o diretor especificado.", Ansi.Color.RED);
        } else {
            displayMediaListOptions(filteredMedia);
        }
    }

    /**
     * Displays CRUD options for TV shows and continues prompting until the user chooses to exit.
     */
    private void displayTvShowCrudOptions() {
        while (true) {
            clearConsole();
            ConsoleMessage.println("Escolha uma opção:");
            switch (InputValidator.getInteger(getCrudOptions())) {
                case 1 -> handleCreateTvShow();
                case 2 -> handleEditTvShow();
                case 3 -> handleDeleteTvShow();
                case 4 -> {
                    return;
                }
                default -> ConsoleMessage.printInvalidOptionMessage();
            }
        }
    }

    private void handleCreateTvShow() {
        TvShow tvShow = new TvShow();

        tvShow.setTitle(InputValidator.getString("Insira o título da série:"));
        tvShow.setDescription(InputValidator.getString("Insira a descrição da série:"));
        tvShow.setDirector(InputValidator.getString("Insira o diretor da série:"));
        tvShow.setReleaseDate(InputValidator.getLocalDate("Insira a data de lançamento da série:"));
        tvShow.setCategory(InputValidator.getCategory("Insira a categoria da série:"));
        tvShow.setRating(InputValidator.getDouble("Insira a nota de avaliação da série:"));

        Map<Integer, List<Episode>> seasons = new HashMap<>();

        int numberOfSeasons = InputValidator.getInteger("Insira o número de temporadas:");
        for (int i = 1; i <= numberOfSeasons; i++) {
            List<Episode> episodes = new ArrayList<>();
            int numberOfEpisodes = InputValidator.getInteger("Insira o número de episódios para a temporada " + i + ":");

            for (int j = 1; j <= numberOfEpisodes; j++) {
                String episodeTitle = InputValidator.getString("Insira o título do episódio " + j + " da temporada " + i + ":");
                int duration = InputValidator.getInteger("Insira a duração do episódio " + j + " da temporada " + i + " em minutos:");
                episodes.add(new Episode(episodeTitle, duration));
            }

            seasons.put(i, episodes);
        }

        tvShow.setSeasons(seasons);
        mediaService.addMedia(tvShow);

        ConsoleMessage.println("Série cadastrada com sucesso!", Ansi.Color.GREEN);
    }

    /**
     * Handles the editing of an existing TV show.
     * Prompts the user to select a TV show from the list and update its details.
     * Saves the updated TV show to the media service
     */
    private void handleEditTvShow() {
        List<Media> tvShows = mediaService.getAllTvShows();

        if (tvShows.isEmpty()) {
            clearConsole();
            ConsoleMessage.println("\nNenhuma série encontrada.\n", Ansi.Color.RED);
            return;
        }

        while (true) {
            clearConsole();
            ConsoleMessage.println("Séries cadastradas:");
            showMediaList(tvShows);

            TvShow tvShow = (TvShow) mediaService.getMediaById(InputValidator.getInteger("Escolha a série que deseja editar:"));

            if (tvShow == null) {
                clearConsole();
                ConsoleMessage.printInvalidOptionMessage();
                break;
            }
            clearConsole();
            tvShow.setTitle(InputValidator.getString("Insira o novo título da série:"));
            tvShow.setDescription(InputValidator.getString("Insira a nova descrição da série:"));
            tvShow.setDirector(InputValidator.getString("Insira o novo diretor da série:"));
            tvShow.setReleaseDate(InputValidator.getLocalDate("Insira a nova data de lançamento da série:"));
            tvShow.setCategory(InputValidator.getCategory("Insira a nova categoria da série:"));
            tvShow.setRating(InputValidator.getDouble("Insira a nova nota de avaliação da série:"));

            // TODO: edit seasons

            try {
                mediaService.updateMedia(tvShow);
                ConsoleMessage.println("Série editada com sucesso!", Ansi.Color.GREEN);
                return;
            } catch (Exception e) {
                ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
            }
        }
    }

    /**
     * Handles the deletion of an existing TV show.
     * Prompts the user to select a TV show from the list and removes it from the media service.
     */
    private void handleDeleteTvShow() {
        List<Media> tvShows = mediaService.getAllTvShows();

        if (tvShows.isEmpty()) {
            clearConsole();
            ConsoleMessage.println("\nNenhuma série encontrada.\n", Ansi.Color.RED);
            return;
        }

        while (true) {
            clearConsole();
            ConsoleMessage.println("Séries cadastradas:");
            showMediaList(tvShows);

            try {
                mediaService.deleteMedia(InputValidator.getInteger("Escolha a série que deseja remover:"));

                ConsoleMessage.println("Série removida com sucesso!", Ansi.Color.GREEN);
                return;
            } catch (Exception e) {
                ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
            }
        }
    }

    /**
     * Displays options for managing movies (CRUD operations).
     */
    private void displayMovieCrudOptions() {
        while (true) {
            clearConsole();
            ConsoleMessage.println("Escolha uma opção:");
            switch (InputValidator.getInteger(getCrudOptions())) {
                case 1 -> handleCreateMovie();
                case 2 -> handleEditMovie();
                case 3 -> handleDeleteMovie();
                case 4 -> {
                    return;
                }
                default -> ConsoleMessage.printInvalidOptionMessage();
            }
        }
    }

    /**
     * Handles the creation of a new movie.
     * Prompts the user to input movie details and saves the new movie to the media service.
     */
    private void handleCreateMovie() {
        Movie movie = new Movie();
        clearConsole();
        movie.setTitle(InputValidator.getString("Insira o título do filme:"));
        movie.setDescription(InputValidator.getString("Insira a descrição do filme:"));
        movie.setDirector(InputValidator.getString("Insira o diretor do filme:"));
        movie.setReleaseDate(InputValidator.getLocalDate("Insira a data de lançamento do filme:"));
        movie.setCategory(InputValidator.getCategory("Insira a categoria do filme:"));
        movie.setRating(InputValidator.getDouble("Insira a nota de avaliação do filme:"));
        movie.setDurationInMinutes(InputValidator.getInteger("Insira a duração do filme em minutos:"));

        mediaService.addMedia(movie);

        ConsoleMessage.println("Filme cadastrado com sucesso!", Ansi.Color.GREEN);
    }

    /**
     * Handles the editing of an existing movie.
     * Prompts the user to select a movie from the list and update its details.
     * Saves the updated movie to the media service.
     */
    private void handleEditMovie() {
        List<Media> movies = mediaService.getAllMovies();

        if (movies.isEmpty()) {
            clearConsole();
            ConsoleMessage.println("\nNenhum filme encontrado.\n", Ansi.Color.RED);
            return;
        }

        while (true) {
            clearConsole();
            ConsoleMessage.println("Filmes cadastrados:");
            showMediaList(movies);

            int movieId = InputValidator.getInteger("Escolha o filme que deseja editar:");

            Movie movie = (Movie) mediaService.getMediaById(movieId);

            if (movie == null) {
                ConsoleMessage.printInvalidOptionMessage();
                break;
            }

            movie.setTitle(InputValidator.getString("Insira o novo título do filme:"));
            movie.setDescription(InputValidator.getString("Insira a nova descrição do filme:"));
            movie.setDirector(InputValidator.getString("Insira o novo diretor do filme:"));
            movie.setReleaseDate(InputValidator.getLocalDate("Insira a nova data de lançamento do filme:"));
            movie.setCategory(InputValidator.getCategory("Insira a nova categoria do filme:"));
            movie.setRating(InputValidator.getDouble("Insira a nova nota de avaliação do filme:"));
            movie.setDurationInMinutes(InputValidator.getInteger("Insira a nova duração do filme em minutos:"));

            try {
                mediaService.updateMedia(movie);
                ConsoleMessage.println("Filme editado com sucesso!", Ansi.Color.GREEN);
                return;
            } catch (Exception e) {
                ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
            }
        }
    }

    /**
     * Handles the deletion of an existing movie.
     * Prompts the user to select a movie from the list and removes it from the media service.
     */
    private void handleDeleteMovie() {
        List<Media> movies = mediaService.getAllMovies();

        if (movies.isEmpty()) {
            clearConsole();
            ConsoleMessage.println("\nNenhum filme encontrado.\n", Ansi.Color.RED);
            return;
        }

        while (true) {
            clearConsole();
            ConsoleMessage.println("Filmes cadastrados:");
            showMediaList(movies);

            try {
                mediaService.deleteMedia(InputValidator.getInteger("Escolha o filme que deseja remover:"));

                ConsoleMessage.println("Filme removido com sucesso!", Ansi.Color.GREEN);
                return;
            } catch (Exception e) {
                ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
            }
        }
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
        clearConsole();
        for (int i = 0; i < mediaList.size(); i += 3) {
            String media1 = "[" + mediaList.get(i).getId() + "] " + mediaList.get(i).getTitle();
            String media2 = (i + 1 < mediaList.size()) ? "[" + mediaList.get(i + 1).getId() + "] " + mediaList.get(i + 1).getTitle() : "";
            String media3 = (i + 2 < mediaList.size()) ? "[" + mediaList.get(i + 2).getId() + "] " + mediaList.get(i + 2).getTitle() : "";

            table.addRow(media1, media2, media3);
            table.addRule();
        }

        table.setTextAlignment(TextAlignment.LEFT);
        ConsoleMessage.println(table.render());

        if (hasFilters) {
            ConsoleMessage.println("* Filtros aplicados\n", Ansi.Color.GREEN);
        }
    }

    /**
     * Displays the information of the currently logged-in user in an ASCII table format.
     */
    private void showLoggedUserInformation() {
        AsciiTable asciiTable = new AsciiTable();
        clearConsole();
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

    private String getMenuOptions() {
        var defaultOptions = "";

        if (loginService.getLoggedInUser().isAdmin()) {
            defaultOptions = "[1] Visualizar catálogo de filmes\n" +
                    "[2] Visualizar catálogo de séries\n" +
                    "[3] Gerenciar filmes\n" +
                    "[4] Gerenciar séries\n" +
                    "[5] Logout";
        } else {
            defaultOptions = "[1] Selecionar perfil\n" +
                    "[2] Criar perfil\n" +
                    "[3] Excluir perfil\n" +
                    "[4] Logout";
        }

        return defaultOptions;
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
        return "[1] Escolher\n[2] Filtrar\n[3] Limpar filtros\n[4] Voltar";
    }

    /**
     * Returns a formatted string of options for watching a media item.
     * Includes options to pause or resume watching and to exit.
     *
     * @param isPaused Indicates if the media is currently paused.
     * @return Formatted string of media watching options.
     */
    private String getMediaWatchingOptions(boolean isPaused) {
        return "[1] " + (isPaused ? "Play" : "Pause") + "\n[2] Exit";
    }

    /**
     * @return A formatter string of options for CRUD operations
     */
    private String getCrudOptions() {
        return "[1] Cadastrar\n" +
                "[2] Editar\n" +
                "[3] Excluir\n" +
                "[4] Voltar";
    }

    /**
     * Clears the console by printing 50 empty lines.
     */
    public void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    /**
     * Returns filter options for media items with each option on a new line.
     *
     * @return Filter options string
     */
    private String getFilterOptions() {
        return "[1] Ordenar por data de lançamento (decrescente)\n" +
                "[2] Ordenar por data de lançamento (crescente)\n" +
                "[3] Filtrar por intervalo de datas\n" +
                "[4] Filtrar por ano e avaliação mínima\n" +
                "[5] Filtrar por Categoria\n" +
                "[6] Filtrar por Título\n" +
                "[7] Filtrar por Avaliação\n" +
                "[8] Filtrar por Diretor\n" +
                "[9] Voltar";
    }

    /**
     * Displays options related to profiles and continues prompting until the user chooses to exit.
     */
    private void displayProfileOptions() {
        while (true) {
            ConsoleMessage.println("Escolha uma opção:");
            switch (InputValidator.getInteger(getProfileOptions())) {
                case 1 -> selectProfile();
                case 2 -> createProfile();
                case 3 -> {
                    return;
                }
                default -> ConsoleMessage.printInvalidOptionMessage();
            }
        }
    }

    /**
     * Creates a new profile for the logged-in user.
     * Prompts the user to enter a profile name and handles profile creation.
     * Displays success message upon successful creation or error message if an exception occurs.
     */
    private void createProfile() {
        clearConsole();
        ConsoleMessage.println("------------------------\nCriar novo perfil\n------------------------");

        try {
            if (loginService.getLoggedInUser() == null) {
                ConsoleMessage.printInvalidOptionMessage();
                return;
            }

            String name = InputValidator.getString("Digite o nome do perfil:");
            User user = loginService.getLoggedInUser();

            int profileId = userService.getNextProfileId(user.getId());

            Profile profile = new Profile(profileId, name, user);
            userService.addProfileToUser(user.getId(), profile);

            ConsoleMessage.println("Perfil criado com sucesso!", Ansi.Color.GREEN);
        } catch (Exception e) {
            ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
        }
    }

    /**
     * Allows the logged-in user to select a profile from their available profiles.
     * Displays a list of profiles associated with the user and prompts for profile selection by ID.
     * Upon selecting a profile, displays a success message and proceeds to display profile-specific options.
     * Handles errors by displaying error messages.
     */
    private void selectProfile() {
        Profile profile = retrieveSelectedProfile();
        if (profile != null) {
            clearConsole();
            ConsoleMessage.println("Perfil selecionado: " + profile.getName());
            displayProfileSpecificOptions(profile);
        }
    }

    /**
     * Retrieves a profile selected by the logged-in user.
     * Displays a list of profiles associated with the user and prompts for profile selection by ID.
     * Handles errors by displaying error messages.
     *
     * @return The selected profile, or null if no valid profile is selected.
     */
    private Profile retrieveSelectedProfile() {
        try {
            User user = loginService.getLoggedInUser();
            if (user == null) {
                ConsoleMessage.printInvalidOptionMessage();
                return null;
            }

            List<Profile> profiles = userService.getProfilesByUserId(user.getId());

            if (profiles.isEmpty()) {
                ConsoleMessage.println("Nenhum perfil encontrado.", Ansi.Color.RED);
                return null;
            }

            ConsoleMessage.println("Escolha um perfil:");
            profiles.forEach(profile -> ConsoleMessage.println("[" + profile.getId() + "] " + profile.getName()));

            int profileId = InputValidator.getInteger("Digite o ID do perfil:");
            Profile profile = userService.getProfileById(user.getId(), profileId);

            if (profile == null) {
                ConsoleMessage.printInvalidOptionMessage();
                return null;
            }

            return profile;

        } catch (Exception e) {
            ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
            return null;
        }
    }

    /**
     * Returns a formatted string with profile options.
     * Options include selecting a profile, creating a new profile, and returning to the previous menu.
     * @return The formatted string with profile options.
     */
    private String getProfileOptions() {
        return """
            [1] Selecionar perfil\n
            [2] Criar perfil\n
            [3] Voltar\n
            """;
    }

    /**
     * Adds a selected media to the personal list of a given profile.
     * Displays a list of available media for selection and prompts the user to enter the ID of the media to add.
     * If successful, updates the user's profile with the selected media.
     *
     * @param profile The profile to which the media will be added.
     */
    private void addMediaToMyList(Profile profile) {
        try {
            List<Media> mediaList = mediaService.getAllMedia();
            ConsoleMessage.println("Escolha uma mídia para adicionar à lista:");

            for (Media media : mediaList) {
                ConsoleMessage.println("[" + media.getId() + "] " + media.getTitle());
            }

            int mediaId = InputValidator.getInteger("Digite o ID da mídia:");
            Media media = mediaService.getMediaById(mediaId);

            if (media == null) {
                ConsoleMessage.printInvalidOptionMessage();
                return;
            }

            userService.addToProfileMyList(loginService.getLoggedInUser().getId(), profile.getId(), media);
            ConsoleMessage.println("Mídia adicionada à lista com sucesso!", Ansi.Color.GREEN);
        } catch (Exception e) {
            ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
        }
    }


    /**
     * Removes a selected media from the personal list of a given profile.
     * Displays the current list of media in the profile's personal list for selection,
     * prompts the user to enter the ID of the media to remove.
     * If successful, updates the user's profile by removing the selected media.
     *
     * @param profile The profile from which the media will be removed.
     */
    private void removeMediaFromMyList(Profile profile) {
        try {
            List<Media> myList = userService.getProfileMyList(loginService.getLoggedInUser().getId(), profile.getId());

            if (myList.isEmpty()) {
                ConsoleMessage.println("A lista está vazia.", Ansi.Color.RED);
                return;
            }

            ConsoleMessage.println("Escolha uma mídia para remover da lista:");
            for (Media media : myList) {
                ConsoleMessage.println("[" + media.getId() + "] " + media.getTitle());
            }

            int mediaId = InputValidator.getInteger("Digite o ID da mídia:");
            Media media = mediaService.getMediaById(mediaId);

            if (media == null || !myList.contains(media)) {
                ConsoleMessage.printInvalidOptionMessage();
                return;
            }

            userService.removeFromProfileMyList(loginService.getLoggedInUser().getId(), profile.getId(), media);
            ConsoleMessage.println("Mídia removida da lista com sucesso!", Ansi.Color.GREEN);
        } catch (Exception e) {
            ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
        }
    }


    /**
     * Displays the list of media items in the personal list of a given profile.
     * Retrieves and displays the current list of media in the profile's personal list.
     * If the list is empty, informs the user that the list is empty.
     *
     * @param profile The profile whose media list will be viewed.
     */
    private void viewMyList(Profile profile) {
        try {
            List<Media> myList = userService.getProfileMyList(loginService.getLoggedInUser().getId(), profile.getId());

            if (myList.isEmpty()) {
                ConsoleMessage.println("A lista está vazia.", Ansi.Color.RED);
                return;
            }

            ConsoleMessage.println("Sua lista de mídias:");
            showMediaList(myList);
        } catch (Exception e) {
            ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
        }
    }

    /**
     * Displays profile-specific options for a given profile.
     * Options include viewing catalogs, adding and removing media from lists,
     * and viewing the user's personal media list.
     *
     * @param profile The profile for which options are displayed.
     */
    private void displayProfileSpecificOptions(Profile profile) {
        while (true) {
            ConsoleMessage.println("Escolha uma opção:");
            ConsoleMessage.println("[1] Ver catálogo de filmes");
            ConsoleMessage.println("[2] Ver catálogo de séries");
            ConsoleMessage.println("[3] Adicionar mídia à lista");
            ConsoleMessage.println("[4] Remover mídia da lista");
            ConsoleMessage.println("[5] Ver minha lista");
            ConsoleMessage.println("[6] Voltar ao menu principal");

            int option = InputValidator.getInteger("Digite a opção desejada:");
            switch (option) {
                case 1 -> displayMediaListOptions(mediaService.getAllMovies());
                case 2 -> displayMediaListOptions(mediaService.getAllTvShows());
                case 3 -> addMediaToMyList(profile);
                case 4 -> removeMediaFromMyList(profile);
                case 5 -> viewMyList(profile);
                case 6 -> {
                    return;
                }
                default -> ConsoleMessage.printInvalidOptionMessage();
            }
        }
    }

    /**
     * Removes a profile selected by the logged-in user.
     * Displays a list of profiles associated with the user and prompts for profile selection by ID.
     * Upon successful removal, displays a success message. Handles errors by displaying error messages.
     */
    private void removeProfile() {
        Profile profile = retrieveSelectedProfile();
        if (profile != null) {
            try {
                userService.removeProfile(loginService.getLoggedInUser().getId(), profile.getId());
                ConsoleMessage.println("Perfil removido com sucesso.", Ansi.Color.GREEN);
            } catch (Exception e) {
                ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
            }
        }
    }
}
