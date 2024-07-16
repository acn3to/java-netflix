package com.netflix.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TvShow extends Media {
    private Map<Integer, List<Episode>> seasons;

    public TvShow(){
    }

    public TvShow(String title, String description, String director, LocalDate releaseDate, Category category, double rating, Map<Integer, List<Episode>> seasons) {
        super(title, description, director, releaseDate, category, rating);
        this.seasons = seasons;
    }

    public Map<Integer, List<Episode>> getSeasons() {
        return this.seasons;
    }

    public void setSeasons(Map<Integer, List<Episode>> seasons) {
        this.seasons = seasons;
    }

    @Override
    public String getInformation() {
        StringBuilder info = new StringBuilder(super.getInformation());

        for (Map.Entry<Integer, List<Episode>> entry : getSeasons().entrySet()) {
            info.append("\n\nTemporada ").append(entry.getKey()).append(":");
            for (int i = 0; i < entry.getValue().size(); i++) {
                Episode episode = entry.getValue().get(i);
                info.append("\n  Episódio ").append(i + 1).append(": ")
                        .append(episode.getTitle()).append(" (").append(episode.getDuration()).append(" min)");
            }
        }

        return info.toString();
    }

    @Override
    public String toString() {
        return "TvShow{" +
                "seasons=" + getSeasons() +
                '}';
    }
}
