package com.netflix.entities;

import java.time.LocalDate;

public class Movie extends Media {
    private int durationInMinutes;

    public Movie() {}

    public Movie(String title, String description, String director, LocalDate releaseDate, Category category, double rating, int durationInMinutes) {
        super(title, description, director, releaseDate, category, rating);
        this.durationInMinutes = durationInMinutes;
    }

    @Override
    public String getInformation() {
        return super.getInformation() + "\nDuração: " + this.getDurationInMinutes() + "min";
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }
}
