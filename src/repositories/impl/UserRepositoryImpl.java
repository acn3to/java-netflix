package repositories.impl;

import entities.User;
import repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {
    private List<User> userList = new ArrayList<>();
    private int idUser = 0;

    @Override
    public void save(User user) {
        user.setId(idUser++);
        userList.add(user);
    }

    @Override
    public User findById(Long id) {
        return userList.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return userList;
    }

    @Override
    public void update(User user) {
    }

    @Override
    public void delete(Long id) {
        userList.removeIf(user -> user.getId().equals(id));
    }
}
