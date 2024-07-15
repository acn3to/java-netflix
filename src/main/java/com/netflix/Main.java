package com.netflix;

import com.netflix.entities.Category;
import com.netflix.entities.Movie;
import com.netflix.entities.TvShow;
import com.netflix.entities.User;
import com.netflix.repositories.MediaRepository;
import com.netflix.repositories.UserRepository;
import com.netflix.repositories.impl.MediaRepositoryImpl;
import com.netflix.repositories.impl.UserRepositoryImpl;
import com.netflix.services.LoginService;
import com.netflix.services.MediaService;
import com.netflix.services.UserService;
import com.netflix.utils.ConsoleMessage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        MediaRepository mediaRepository = new MediaRepositoryImpl();
        UserRepository userRepository = new UserRepositoryImpl();

        MediaService mediaService = new MediaService(mediaRepository);
        UserService userService = new UserService(userRepository);
        LoginService loginService = new LoginService(userService);

        try {
            userService.addUser(new User(1, "Usuário Administrador", "admin@email.com", "root", true));
        } catch (Exception e) {
            ConsoleMessage.println("Não foi possível instanciar o usuário root");
        }

        // TESTES
        mediaService.addMedia(new Movie("Harry Potter e a Pedra Filosofal", "A história do menino que sobreviveu", "Chris Columbus", LocalDate.of(2001, 11, 16), Category.FANTASY, 4.7, 152));
        mediaService.addMedia(new Movie("Senhor dos Anéis: A Sociedade do Anel", "A jornada épica para destruir o anel", "Peter Jackson", LocalDate.of(2001, 12, 19), Category.FANTASY, 4.8, 178));
        mediaService.addMedia(new Movie("Matrix", "Um hacker descobre a verdade sobre sua realidade", "Lana Wachowski, Lilly Wachowski", LocalDate.of(1999, 3, 31), Category.FANTASY, 4.6, 136));
        mediaService.addMedia(new Movie("O Poderoso Chefão", "A saga da família mafiosa Corleone", "Francis Ford Coppola", LocalDate.of(1972, 3, 24), Category.ADVENTURE, 4.9, 175));
        mediaService.addMedia(new Movie("Star Wars: Uma Nova Esperança", "A luta dos rebeldes contra o Império", "George Lucas", LocalDate.of(1977, 5, 25), Category.ROMANCE, 4.7, 121));
        Map<Integer, List<String>> seasons1 = new HashMap<>();
        seasons1.put(1, List.of("Episódio 1: Piloto", "Episódio 2: O Começo"));
        seasons1.put(2, List.of("Episódio 1: Retorno", "Episódio 2: Novos Desafios", "Episódio 3: Conflito"));
        Map<Integer, List<String>> seasons2 = new HashMap<>();
        seasons2.put(1, List.of("Episódio 1: Introdução", "Episódio 2: Enigma"));
        seasons2.put(2, List.of("Episódio 1: Mistério", "Episódio 2: Revelação"));
        seasons2.put(3, List.of("Episódio 1: Desfecho", "Episódio 2: Fim"));
        mediaService.addMedia(new TvShow("As Aventuras de Joãozinho", "Uma série sobre aventuras e mistérios", "Diretor 1", LocalDate.of(2022, 1, 1), Category.DRAMA, 4.5, seasons1));
        mediaService.addMedia(new TvShow("Sherlock", "Uma série sobre investigações e enigmas", "Diretor 2", LocalDate.of(2021, 6, 15), Category.SCIENCE_FICTION, 4.8, seasons2));


        NexflixApp nexflixApp = new NexflixApp(loginService, userService, mediaService);
        nexflixApp.run();
    }
}