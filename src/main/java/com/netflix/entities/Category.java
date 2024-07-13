package com.netflix.entities;

public enum Category {
    ADVENTURE("Aventura"),
    COMEDY("Comédia"),
    FANTASY("Fantasia"),
    TERROR("Terror"),
    ANIMATION("Animação"),
    SCIENCE_FICTION("Ficção-científica"),
    DRAMA("Drama"),
    ROMANCE("Romance");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}


