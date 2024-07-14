package com.netflix.services;

import com.netflix.entities.User;
import com.netflix.repositories.UserRepository;

import java.util.List;

public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(User user) throws Exception {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new Exception("Este endereço de e-mail já está cadastrado!");
        }

        userRepository.save(user);
    }

    public User getUserById(int id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void updateUser(User user) throws Exception {
        userRepository.update(user);
    }

    public void deleteUser(int id) throws Exception {
        userRepository.delete(id);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
