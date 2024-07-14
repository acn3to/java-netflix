package com.netflix.services;

import com.netflix.entities.User;

import java.util.List;

public class LoginService {
    private final UserService userService;
    private User loggedInUser;

    public LoginService(UserService userService) {
        this.userService = userService;
    }

    public boolean authenticate(String email, String password) {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                loggedInUser = user;
                return true;
            }
        }
        return false;
    }

    public void login(String email, String password) throws Exception {
        if (authenticate(email, password)) {
            return;
        }

        throw new Exception("Credenciais inválidas.");
    }

    public void logout() {
        loggedInUser = null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
