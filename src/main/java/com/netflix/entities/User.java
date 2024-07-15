package com.netflix.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private boolean isAdmin;
    private List<String> watchedMovies;

    public User(int id, String name, String email, String password, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.watchedMovies = new ArrayList<>();
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdmin = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id > 0) {
            this.id = id;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && email.contains("@")) {
            this.email = email;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null && password.length() >= 8) {
            this.password = password;
        }
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void addWatchedMovie(String movieName) {
        watchedMovies.add(movieName);
    }

    public List<String> getWatchedMovies() {
        return watchedMovies;
    }


    @Override
    public String toString() {
        return
                "ID: " + id +
                "\nName: " + name + '\'' +
                "\nE-mail: " + email +
                "\nIs an administrator:" + isAdmin;
    }
}
