package com.netflix.repositories.impl;

import com.netflix.entities.Media;
import com.netflix.entities.Movie;
import com.netflix.entities.TvShow;
import com.netflix.repositories.MediaRepository;

import java.util.ArrayList;
import java.util.List;

public class MediaRepositoryImpl implements MediaRepository {
    private List<Media> medias = new ArrayList<>();
    private int idMedia = 1;

    @Override
    public void save(Media media) {
        media.setId(idMedia++);
        medias.add(media);
    }

    @Override
    public Media findById(Long id) {
        return medias.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Media> findAll() {
        return medias;
    }

    @Override
    public void update(Media entity) {

    }

    @Override
    public void delete(Long id) {
        medias.removeIf(media -> media.getId() == id);
    }

    @Override
    public List<Media> findAllMovies() {
        return this.medias.stream()
                .filter(media -> media instanceof Movie)
                .toList();
    }

    @Override
    public List<Media> findAllTvShows() {
        return this.medias.stream()
                .filter(media -> media instanceof TvShow)
                .toList();
    }
}
