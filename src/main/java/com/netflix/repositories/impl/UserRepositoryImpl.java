package com.netflix.repositories.impl;

import com.netflix.entities.User;
import com.netflix.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {
    private List<User> users = new ArrayList<>();
    private int idUser = 0;

    @Override
    public void save(User user) {
        user.setId(idUser++);
        users.add(user);
    }

    @Override
    public User findById(Long id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public void update(User user) {
    }

    @Override
    public void delete(Long id) {
        users.removeIf(user -> user.getId() == id);
    }

    @Override
    public User findByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }
}
