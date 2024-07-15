package com.netflix.services;

import com.netflix.entities.Media;
import com.netflix.entities.Profile;
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

    public User getUserByEmail(String email) throws Exception {
        return userRepository.findByEmail(email);
    }

    public List<Profile> getProfilesByUserId(int userId) throws Exception {
        User user = userRepository.findById(userId);
        return user.getProfiles();
    }

    public Profile getProfileById(int userId, int profileId) throws Exception {
        User user = userRepository.findById(userId);
        return user.getProfiles().stream()
                .filter(profile -> profile.getId() == profileId)
                .findFirst()
                .orElse(null);
    }

    public void addProfileToUser(int userId, Profile profile) throws Exception {
        User user = userRepository.findById(userId);
        user.getProfiles().add(profile);
        userRepository.update(user);
    }

    public int getNextProfileId(int userId) throws Exception {
        User user = userRepository.findById(userId);
        return user.getProfiles().stream()
                .mapToInt(Profile::getId)
                .max()
                .orElse(0) + 1;
    }

    public void addToProfileMyList(int userId, int profileId, Media media) throws Exception {
        User user = userRepository.findById(userId);
        Profile profile = getProfileById(userId, profileId);
        if (profile == null) {
            throw new Exception("Perfil não encontrado.");
        }
        profile.addToMyList(media);
        userRepository.update(user);  // Atualizar o perfil no repositório
    }

    public void removeFromProfileMyList(int userId, int profileId, Media media) throws Exception {
        User user = userRepository.findById(userId);
        Profile profile = getProfileById(userId, profileId);
        if (profile == null) {
            throw new Exception("Perfil não encontrado.");
        }
        profile.removeFromMyList(media);
        userRepository.update(user);  // Atualizar o perfil no repositório
    }

    public List<Media> getProfileMyList(int userId, int profileId) throws Exception {
        User user = userRepository.findById(userId);
        Profile profile = getProfileById(userId, profileId);
        if (profile == null) {
            throw new Exception("Perfil não encontrado.");
        }
        return profile.getMyList();
    }
}
