package com.netflix.entities;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TvShow extends Media {
    private Map<Integer, List<Episode>> seasons;

    public TvShow() {
        this.seasons = new HashMap<>();
    }

    public TvShow(String title, String description, String director, LocalDate releaseDate, Category category, double rating, Map<Integer, List<Episode>> seasons) {
        super(title, description, director, releaseDate, category, rating);
        this.seasons = seasons;
    }

    public Map<Integer, List<Episode>> getSeasons() {
        return seasons;
    }

    public void setSeasons(Map<Integer, List<Episode>> seasons) {
        this.seasons = seasons;
    }

    @Override
    public String toString() {
        return "TvShow{" +
                "seasons=" + getSeasons() +
                '}';
    }
}
