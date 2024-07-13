package services;

import entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private List usersList = new ArrayList();

    public void registerUser(User user) {
        usersList.add(user);
    }

    public void removeUser(User user) {
        usersList.remove(user);
    }

    public void findUsersById(int id) {
        for (User user : usersList) {
            if (user.getId() == id) {
                System.out.println(user);
            }
        }
    }
    public UserService(List usersList) {
        this.usersList = usersList;
    }

    public List getUsersList() {
        return usersList;
    }

    public void setUsersList(List usersList) {
        this.usersList = usersList;
    }
}
