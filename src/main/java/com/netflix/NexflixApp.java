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

            String menuOptions = getMenuOptions();

            switch (InputValidator.getInteger(menuOptions)) {
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

                    if (loginService.getLoggedInUser() != null) {
                        displayProfileOptions();
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
                    Media tvShow = mediaService.getMediaById(InputValidator.getInteger("Escolha uma série (número)"));

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
        while (true) {
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

    private void handleCreateTvShow() {
        TvShow tvShow = new TvShow();

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

    private void handleEditTvShow() {
        List<Media> tvShows = mediaService.getAllTvShows();

        if (tvShows.isEmpty()) {
            ConsoleMessage.println("\nNenhum registro encontrado.\n", Ansi.Color.RED);
            return;
        }

        while (true) {
            ConsoleMessage.println("Séries cadastradas:");
            showMediaList(tvShows);

            TvShow tvShow = (TvShow) mediaService.getMediaById(InputValidator.getInteger("Escolha a série que deseja editar:"));

            if (tvShow == null) {
                ConsoleMessage.printInvalidOptionMessage();
                break;
            }

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


    private void handleDeleteTvShow() {
        List<Media> tvShows = mediaService.getAllTvShows();

        if (tvShows.isEmpty()) {
            ConsoleMessage.println("\nNenhum registro encontrado.\n", Ansi.Color.RED);
            return;
        }

        while (true) {
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

    private void displayMovieCrudOptions() {
        while (true) {
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

    private void handleCreateMovie() {
        Movie movie = new Movie();

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

    private void handleEditMovie() {
        List<Media> movies = mediaService.getAllMovies();

        if (movies.isEmpty()) {
            ConsoleMessage.println("\nNenhum registro encontrado.\n", Ansi.Color.RED);
            return;
        }

        while (true) {
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

    private void handleDeleteMovie() {
        List<Media> movies = mediaService.getAllMovies();

        if (movies.isEmpty()) {
            ConsoleMessage.println("\nNenhum registro encontrado.\n", Ansi.Color.RED);
            return;
        }

        while (true) {
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

    private String getLoginMenuOptions() {
        return "[1] Login\n" +
                "[2] Cadastrar novo usuário\n" +
                "[3] Sair";
    }


    private String getMenuOptions() {
        var defaultOptions = "[1] Visualizar catálogo de filmes\n" +
                "[2] Visualizar catálogo de séries\n";

        if (loginService.getLoggedInUser().isAdmin()) {
            defaultOptions = defaultOptions +
                    "[3] Gerenciar filmes\n" +
                    "[4] Gerenciar séries\n" +
                    "[5] Logout";
        } else {
            if (loginService.getLoggedInUser() != null && (!loginService.getLoggedInUser().isAdmin())) {
                defaultOptions += "[6] Gerenciar Perfis";
            }
            defaultOptions = defaultOptions + "\n[3] Logout";
        }


        return defaultOptions;
    }

    private String getMediaOptions() {
        return "\n[1] Assistir\n[2] Voltar";
    }

    private String getMediaListOptions() {
        return "[1] Escolher\n[2] Voltar";
    }

    private String getMediaWatchingOptions(boolean isPaused) {
        return "[1] " + (isPaused ? "Despausar" : "Pausar") + "\n[2] Sair";
    }

    private String getCrudOptions() {
        return "[1] Cadastrar" +
                "\n[2] Editar" +
                "\n[3] Excluir" +
                "\n[4] Voltar";
    }

    private void displayProfileOptions() {
        while (true) {
            ConsoleMessage.println("Escolha uma opção:");
            switch (InputValidator.getInteger(getProfileOptions())) {
                case 1:
                    createProfile();
                    break;
                case 2:
                    listProfiles();
                    break;
                case 3:
                    selectProfile();
                    break;
                case 4:
                    return;  // Voltar ao menu principal
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

            // Exemplo de uma ação a ser feita após selecionar um perfil
            // Você pode chamar outros métodos aqui ou mostrar um menu específico para o perfil
            ConsoleMessage.println("Perfil selecionado: " + profile.getName());

            // Exemplo: Mostre opções específicas para o perfil selecionado
            displayProfileSpecificOptions(profile);

        } catch (Exception e) {
            ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
        }
    }


    private String getProfileOptions() {
        return "[1] Criar novo perfil\n" +
                "[2] Listar perfis\n" +
                "[3] Selecionar perfil\n" +
                "[4] Voltar";
    }

//    private void manageMyList() {
//        try {
//            User user = loginService.getLoggedInUser();
//            if (user == null) {
//                ConsoleMessage.printInvalidOptionMessage();
//                return;
//            }
//
//            List<Profile> profiles = userService.getProfilesByUserId(user.getId());
//
//            if (profiles.isEmpty()) {
//                ConsoleMessage.println("Nenhum perfil encontrado.", Ansi.Color.RED);
//                return;
//            }
//
//            ConsoleMessage.println("Escolha um perfil:");
//            profiles.forEach(profile -> ConsoleMessage.println("[" + profile.getId() + "] " + profile.getName()));
//
//            int profileId = InputValidator.getInteger("Digite o ID do perfil:");
//            Profile profile = userService.getProfileById(user.getId(), profileId);
//
//            if (profile == null) {
//                ConsoleMessage.printInvalidOptionMessage();
//                return;
//            }
//
//            while (true) {
//                ConsoleMessage.println("Escolha uma opção:");
//                switch (InputValidator.getInteger(getMyListOptions())) {
//                    case 1:
//                        addMediaToMyList(profile);
//                        break;
//                    case 2:
//                        removeMediaFromMyList(profile);
//                        break;
//                    case 3:
//                        viewMyList(profile);
//                        break;
//                    case 4:
//                        return;  // Corrigido para sair do loop e voltar ao menu principal
//                    default:
//                        ConsoleMessage.printInvalidOptionMessage();
//                        break;
//                }
//            }
//        } catch (Exception e) {
//            ConsoleMessage.println(e.getMessage(), Ansi.Color.RED);
//        }
//    }

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
            // Exemplo de opções
            ConsoleMessage.println("[1] Adicionar mídia à lista");
            ConsoleMessage.println("[2] Remover mídia da lista");
            ConsoleMessage.println("[3] Ver minha lista");
            ConsoleMessage.println("[4] Voltar ao menu principal");

            int option = InputValidator.getInteger("Digite a opção desejada:");
            switch (option) {
                case 1:
                    addMediaToMyList(profile);
                    break;
                case 2:
                    removeMediaFromMyList(profile);
                    break;
                case 3:
                    viewMyList(profile);
                    break;
                case 4:
                    return;  // Voltar ao menu principal
                default:
                    ConsoleMessage.printInvalidOptionMessage();
                    break;
            }
        }
    }
}
