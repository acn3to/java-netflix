package com.netflix.entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class Media {
    private int id;
    private String title;
    private String description;
    private String director;
    private LocalDate releaseDate;
    private Category category;
    private double rating;

    public Media() {
    }

    public Media(int id, String title, String description, String director, LocalDate releaseDate, Category category, double rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.director = director;
        this.releaseDate = releaseDate;
        this.category = category;
        this.rating = rating;
    }

    public Media(String title, String description, String director, LocalDate releaseDate, Category category, double rating) {
        this.title = title;
        this.description = description;
        this.director = director;
        this.releaseDate = releaseDate;
        this.category = category;
        this.rating = rating;
    }

    public String getInformation() {
        return "--------- " + this.getTitle() + " ---------" +
                "\n\"" + this.getDescription() + "\"" +
                "\nCategoria: " + this.getCategory().getDescription() +
                "\nDiretor: " + this.getDirector() +
                "\nAvaliação: ⭐ " + this.getRating() +
                "\nData de lançamento: " + this.getFormattedReleaseDate();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public String getFormattedReleaseDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return this.getReleaseDate().format(formatter);
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
