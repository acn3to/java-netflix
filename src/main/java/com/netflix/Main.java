package com.netflix;

import com.netflix.entities.*;
import com.netflix.repositories.MediaRepository;
import com.netflix.repositories.UserRepository;
import com.netflix.repositories.impl.MediaRepositoryImpl;
import com.netflix.repositories.impl.UserRepositoryImpl;
import com.netflix.services.LoginService;
import com.netflix.services.MediaService;
import com.netflix.services.UserService;
import com.netflix.utils.ConsoleMessage;

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

        NexflixApp nexflixApp = new NexflixApp(loginService, userService, mediaService);
        nexflixApp.run();
    }
}