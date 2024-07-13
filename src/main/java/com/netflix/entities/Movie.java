package main.java.com.netflix.entities;

import java.time.LocalDate;

public class Movie extends Media {
    private int durationInMinutes;

    public Movie() {}

    public Movie(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public Movie(int id, String title, String description, String director, LocalDate releaseDate, Category category, double rating, int durationInMinutes) {
        super(id, title, description, director, releaseDate, category, rating);
        this.durationInMinutes = durationInMinutes;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }
}
