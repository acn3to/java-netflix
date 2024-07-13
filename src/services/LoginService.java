package services;

import entities.User;

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

    public void login(String email, String password) {
        if (authenticate(email, password)) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    public void logout() {
        loggedInUser = null;
        System.out.println("Logged out successfully.");
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
