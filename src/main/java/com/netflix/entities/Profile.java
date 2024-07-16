package com.netflix.entities;

import java.util.ArrayList;
import java.util.List;

public class Profile {
    private int id;
    private String name;
    private User owner;
    private final List<Media> myList = new ArrayList<>();

    public Profile(int id, String name, User owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Media> getMyList() {
        return myList;
    }

    public void addToMyList(Media media) {
        if (media != null && !myList.contains(media)) {
            myList.add(media);
        }
    }

    public void removeFromMyList(Media media) {
        myList.remove(media);
    }

    @Override
    public String toString() {
        return "ID: " + id + "\nNome: " + name;
    }
}
