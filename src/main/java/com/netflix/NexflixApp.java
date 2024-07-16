package com.netflix;

import com.netflix.entities.*;
import com.netflix.services.LoginService;
import com.netflix.services.MediaService;
import com.netflix.services.UserService;
import com.netflix.utils.ConsoleMessage;
import com.netflix.utils.InputValidator;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import org.fusesource.jansi.Ansi;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NexflixApp {
    private final LoginService loginService;
    private final UserService userService;
    private final MediaService mediaService;
    static Scanner scanner = new Scanner(System.in);
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
            aperteParaContinuar();
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
            showLoggedUserInformation();
            ConsoleMessage.println("Navegue pelas opções abaixo:");

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
//                case 11:
//                    removeProfile();
//                    break;
//                case 2:
//                    displayMediaListOptions(mediaService.getAllTvShows());
//                    break;



//                ADICIONAR OPÇÃO DE VER HISTÓRICO DE FILMES AQUI:
//              case 3:
//                  displayWatchedMovies();
//                  break;
//
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
     * Displays the list of movies and provides options to the user.<p>
     * Options include selecting a movie to view its details or exiting the list.
     * If no movies are available, a message is displayed and the method returns.
     * The loop continues until the user chooses to exit.
     */
    private void displayMovieListOptions() {
        List<Media> movies = mediaService.getAllMovies();
        clearConsole();
        if (movies.isEmpty()) {
            ConsoleMessage.println("\nNenhum filme encontrado.\n", Ansi.Color.RED);
            aperteParaContinuar();
            return;
        }

        while (true) {
            clearConsole();
            ConsoleMessage.printMovieCatalogTitle();
            showMediaList(movies);

            switch (InputValidator.getInteger(getMediaListOptions())) {
                case 1:
                    Media movie = mediaService.getMediaById(InputValidator.getInteger("Escolha um filme (número)"));

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
            clearConsole();
            ConsoleMessage.println("\nNenhuma série encontrada.\n", Ansi.Color.RED);
            aperteParaContinuar();
            return;
        }

        while (true) {
            clearConsole();
            ConsoleMessage.printTvShowCatalogTitle();
            showMediaList(tvShows);
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
            clearConsole();
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
            clearConsole();
            ConsoleMessage.println("Escolha um episódio:");
            for (int i = 0; i < episodeList.size(); i++) {
                ConsoleMessage.println("[" + (i + 1) + "] " + episodeList.get(i));
            }

            if (episodeList.get(InputValidator.getInteger("") - 1).isEmpty()) {
                ConsoleMessage.printInvalidOptionMessage();
                aperteParaContinuar();
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
            clearConsole();
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
            case 1:
                handleSelectMedia();
                break;
            case 2:
                displayFilterOptions(mediaList);
                break;
            case 3:
                resetFilters(isListOfMovies);
                break;
            case 4:
                exitMediaList();
                break;
            default:
                ConsoleMessage.printInvalidOptionMessage();
                break;
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
            case 1:
                applyFilterAndDisplay(() -> mediaService.filterReleaseDateInDescendingOrder(mediaList));
                break;
            case 2:
                applyFilterAndDisplay(() -> mediaService.filterReleaseDateInAscendingOrder(mediaList));
                break;
            case 3:
                applyDateRangeFilter(mediaList);
                break;
            case 4:
                applyYearAndRatingFilter(mediaList);
                break;
            case 5:
                applyCategoryFilter(mediaList);
                break;
            case 6:
                applyTitleFilter(mediaList);
                break;
            case 7:
                applyRatingFilter(mediaList);
                break;
            case 8:
                applyDirectorFilter(mediaList);
                break;
            case 10:
            case 9:
                displayMenuOptions();
                return;
            default:
                ConsoleMessage.printInvalidOptionMessage();
                break;}
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
     * Solicita ao usuário que insira uma categoria e filtra a lista de itens de mídia por essa categoria.
     * Exibe todas as categorias disponíveis com índices para seleção pelo usuário.
     * Define a flag {@code hasFilters} como true.
     *
     * @param mediaList A lista de itens de mídia para filtrar por categoria.
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

        boolean categoriaValida = false;
        Category categoriaSelecionada = null;

        while (!categoriaValida) {
            try {
                String indiceCategoriaStr = InputValidator.getString("Selecione a categoria pelo número:");
                int indiceCategoria = Integer.parseInt(indiceCategoriaStr);

                if (indiceCategoria >= 1 && indiceCategoria <= 8) {
                    categoriaSelecionada = Category.values()[indiceCategoria - 1];
                    categoriaValida = true;
                } else {
                    ConsoleMessage.println("Número de categoria inválido! Por favor, selecione um número válido.", Ansi.Color.RED);
                }
            } catch (NumberFormatException e) {
                ConsoleMessage.println("Entrada inválida! Por favor, digite um número válido.", Ansi.Color.RED);
            }
        }

        displayMediaListOptions(mediaService.filterByCategory(mediaList, categoriaSelecionada));
    }


    /**
     * Solicita ao usuário que digite um título e filtra a lista de itens de mídia por esse título.
     * Define a flag {@code hasFilters} como true.
     *
     * @param mediaList A lista de itens de mídia para filtrar por título.
     */
    private void applyTitleFilter(List<Media> mediaList) {
        this.hasFilters = true;

        ConsoleMessage.println("Digite o título para filtrar:");

        String titulo = InputValidator.getString("Título: ");
        List<Media> mediaFiltrada = mediaService.filterByTitle(mediaList, titulo);

        if (mediaFiltrada.isEmpty()) {
            ConsoleMessage.println("Nenhum item encontrado com o título especificado.", Ansi.Color.RED);
        } else {
            displayMediaListOptions(mediaFiltrada);
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
     * Solicita ao usuário que selecione um diretor da lista mostrada e filtra a lista de itens de mídia por esse diretor.
     * Define a flag {@code hasFilters} como true.
     *
     * @param mediaList A lista de itens de mídia para filtrar por diretor.
     */
    private void applyDirectorFilter(List<Media> mediaList) {
        this.hasFilters = true;

        ConsoleMessage.println("Digite o nome do diretor para filtrar:");

        String diretor = InputValidator.getString("Diretor: ");
        List<Media> mediaFiltrada = mediaService.filterByDirector(mediaList, diretor);

        if (mediaFiltrada.isEmpty()) {
            ConsoleMessage.println("Nenhum item encontrado com o diretor especificado.", Ansi.Color.RED);
        } else {
            displayMediaListOptions(mediaFiltrada);
        }
    }



    private void displayTvShowCrudOptions() {
        while (true) {
            clearConsole();
            ConsoleMessage.println("Escolha uma opção:");
            switch (InputValidator.getInteger(getCrudOptions())) {
                case 1:
                    handleCreateTvShow();
                    break;
                case 2:
                    handleEditTvShow();
                    break;
                case 3:
                    handleDeleteTvShow();
                    break;
                case 4:
                    return;
                default:
                    ConsoleMessage.printInvalidOptionMessage();
                    break;
            }
        }
    }

    /**
     * Handles the creation of a new TV show.
     * Prompts the user to input TV show details and saves to the media service.
     */
    private void handleCreateTvShow() {
        TvShow tvShow = new TvShow();
        clearConsole();
        tvShow.setTitle(InputValidator.getString("Insira o título da série:"));
        tvShow.setDescription(InputValidator.getString("Insira a descrição da série:"));
        tvShow.setDirector(InputValidator.getString("Insira o diretor da série:"));
        tvShow.setReleaseDate(InputValidator.getLocalDate("Insira a data de lançamento da série:"));
        tvShow.setCategory(InputValidator.getCategory("Insira a categoria da série:"));
        tvShow.setRating(InputValidator.getDouble("Insira a nota de avaliação da série:"));

        // TODO: creation of seasons

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
            aperteParaContinuar();
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
            aperteParaContinuar();
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
                case 1:
                    handleCreateMovie();
                    break;
                case 2:
                    handleEditMovie();
                    break;
                case 3:
                    handleDeleteMovie();
                    break;
                case 4:
                    return;
                default:
                    ConsoleMessage.printInvalidOptionMessage();
                    break;
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
            aperteParaContinuar();
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
            aperteParaContinuar();
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

    private void watchMovie(Scanner scanner) {
        System.out.print("Enter the movie name: ");
        String movieName = scanner.nextLine();

        if (loginService.getLoggedInUser() != null) {
            loginService.getLoggedInUser().addWatchedMovie(movieName);
            System.out.println("You watched: " + movieName);
        } else {
            System.out.println("You need to log in first.");
        }
    }

    private void displayWatchedMovies() {
        if (loginService.getLoggedInUser() != null) {
            List<String> watchedMovies = loginService.getLoggedInUser().getWatchedMovies();
            if (watchedMovies.isEmpty()) {
                System.out.println("You haven't watched any movies yet.");
            } else {
                System.out.println("Watched movies:");
                for (String movie : watchedMovies) {
                    System.out.println("- " + movie);
                }
            }
        } else {
            System.out.println("You need to log in first.");
        }
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
            defaultOptions += "[1] Selecionar perfil \n" +
                    "[2] Criar perfil \n" +
                    "[3] Excluir perfil \n" +
                    "[4] Logout";
            if (loginService.getLoggedInUser() != null && (!loginService.getLoggedInUser().isAdmin())) {
            }
        //    defaultOptions = defaultOptions + "\n[3] Logout";
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
        return "[1] " + (isPaused ? "Despausar" : "Pausar") + "\n[2] Sair";
    }

    /**
     * @return A formatter string of options for CRUD operations
     */
    private String getCrudOptions() {
        return "[1] Cadastrar" +
                "\n[2] Editar" +
                "\n[3] Excluir" +
                "\n[4] Voltar";
    }

    public void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
    public static void aperteParaContinuar() {
        System.out.println("Pressione para continuar...");
        scanner.next();
    }


    private String getFilterOptions() {
        return "[1] Ordenar por data de lançamento (decrescente)" +
                "\n[2] Ordenar por data de lançamento (crescente)" +
                "\n[3] Filtrar por intervalo de datas" +
                "\n[4] Filtrar por ano e avaliação mínima" +
                "\n[5] Filtrar por Categoria" +
                "\n[6] Filtrar por Título" +
                "\n[7] Filtrar por Avaliação" +
                "\n[8] Filtrar por Diretor" +
                "\n[9] Voltar";
    }

    private void displayProfileOptions() {
        while (true) {
            ConsoleMessage.println("Escolha uma opção:");
            switch (InputValidator.getInteger(getProfileOptions())) {
                case 1:
                    selectProfile();
                    break;
                case 2:
                    createProfile();
                    break;
                case 3:
                    return;
                default:
                    ConsoleMessage.printInvalidOptionMessage();
                    break;
            }
        }
    }

    private void createProfile() {
        ConsoleMessage.println("------------------------\nCriar novo perfil\n------------------------");

        try {
            if (loginService.getLoggedInUser() == null) {
                ConsoleMessage.printInvalidOptionMessage();
                return;
            }

            String name = InputValidator.getString("Digite o nome do perfil:");
            User user = loginService.getLoggedInUser();
            int profileId = userService.getNextProfileId(user.getId()); // Obter o próximo ID disponível

            Profile profile = new Profile(profileId, name, user);
            userService.addProfileToUser(user.getId(), profile);

            ConsoleMessage.println("Perfil criado com sucesso!", Ansi.Color.GREEN);
        } catch (Exception e) {
            ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
        }
    }

    private void listProfiles() {
        ConsoleMessage.println("------------------------\nTodos os Perfis\n------------------------");

        try {
            if (loginService.getLoggedInUser() == null) {
                ConsoleMessage.printInvalidOptionMessage();
                return;
            }

            User user = loginService.getLoggedInUser();
            List<Profile> profiles = userService.getProfilesByUserId(user.getId());

            if (profiles.isEmpty()) {
                ConsoleMessage.println("Nenhum perfil encontrado.", Ansi.Color.RED);
                return;
            }

            AsciiTable table = new AsciiTable();
            table.addRule();
            table.addRow("ID", "Nome");
            table.addRule();

            for (Profile profile : profiles) {
                table.addRow(profile.getId(), profile.getName());
            }

            table.addRule();
            table.setTextAlignment(TextAlignment.LEFT);
            ConsoleMessage.println(table.render());
        } catch (Exception e) {
            ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
        }
    }

    private void selectProfile() {
        try {
            User user = loginService.getLoggedInUser();
            if (user == null) {
                ConsoleMessage.printInvalidOptionMessage();
                return;
            }

            List<Profile> profiles = userService.getProfilesByUserId(user.getId());

            if (profiles.isEmpty()) {
                ConsoleMessage.println("Nenhum perfil encontrado.", Ansi.Color.RED);
                return;
            }

            ConsoleMessage.println("Escolha um perfil:");
            profiles.forEach(profile -> ConsoleMessage.println("[" + profile.getId() + "] " + profile.getName()));

            int profileId = InputValidator.getInteger("Digite o ID do perfil:");
            Profile profile = userService.getProfileById(user.getId(), profileId);

            if (profile == null) {
                ConsoleMessage.printInvalidOptionMessage();
                return;
            }

            ConsoleMessage.println("Perfil selecionado: " + profile.getName());

            displayProfileSpecificOptions(profile);

        } catch (Exception e) {
            ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
        }
    }


    private String getProfileOptions() {
        return "[1] Selecionar perfil\n" +
                "[2] Criar perfil\n" +
                "[3] Voltar\n";
    }

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

    private String getMyListOptions() {
        return "[1] Adicionar mídia à lista\n" +
                "[2] Remover mídia da lista\n" +
                "[3] Ver minha lista\n" +
                "[4] Voltar";
    }

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
                case 1:
                    displayMediaListOptions(mediaService.getAllMovies());
                    break;
                case 2:
                    displayMediaListOptions(mediaService.getAllTvShows());
                    break;
                case 3:
                    addMediaToMyList(profile);
                    break;
                case 4:
                    removeMediaFromMyList(profile);
                    break;
                case 5:
                    viewMyList(profile);
                    break;
                case 6:
                    return;
                default:
                    ConsoleMessage.printInvalidOptionMessage();
                    break;
            }
        }
    }

    private void removeProfile() {
        try {
            User user = loginService.getLoggedInUser();
            if (user == null) {
                ConsoleMessage.printInvalidOptionMessage();
                return;
            }

            List<Profile> profiles = userService.getProfilesByUserId(user.getId());

            if (profiles.isEmpty()) {
                ConsoleMessage.println("Nenhum perfil encontrado.", Ansi.Color.RED);
                return;
            }

            ConsoleMessage.println("Escolha um perfil:");
            profiles.forEach(profile -> ConsoleMessage.println("[" + profile.getId() + "] " + profile.getName()));

            int profileId = InputValidator.getInteger("Digite o ID do perfil:");
            Profile profile = userService.getProfileById(user.getId(), profileId);

            if (profile == null) {
                ConsoleMessage.printInvalidOptionMessage();
                return;
            }

            System.out.println("id: " + profileId);

            userService.removeProfile(loginService.getLoggedInUser().getId(), profile.getId());

            // Adicione uma mensagem de confirmação
            ConsoleMessage.println("Perfil removido com sucesso.", Ansi.Color.GREEN);
        } catch (Exception e) {
            ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
        }
    }
}
