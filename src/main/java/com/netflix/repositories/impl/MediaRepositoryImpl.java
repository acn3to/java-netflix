package com.netflix.repositories.impl;

import com.netflix.entities.Media;
import com.netflix.entities.Movie;
import com.netflix.entities.TvShow;
import com.netflix.repositories.MediaRepository;

import java.util.ArrayList;
import java.util.List;

public class MediaRepositoryImpl implements MediaRepository {
    private final List<Media> medias = new ArrayList<>();
    private int idMedia = 1;

    @Override
    public void save(Media media) {
        media.setId(idMedia++);
        medias.add(media);
    }

    @Override
    public Media findById(int id) {
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
    public void update(Media entity) throws Exception {
        Media oldMedia = findById(entity.getId());

        if (oldMedia == null) {
            throw new Exception("Não foi possível encontrar a mídia desejada.");
        }

        medias.remove(oldMedia);
        medias.add(entity);
    }

    @Override
    public void delete(int id) throws Exception {
        Media media = findById(id);

        if (media == null) {
            throw new Exception("Não foi possível encontrar a mídia desejada.");
        }

        medias.remove(media);
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
