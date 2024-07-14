package com.netflix.repositories;

import com.netflix.entities.Media;

import java.util.List;

public interface MediaRepository extends Repository<Media> {
    List<Media> findAllMovies();
    List<Media> findAllTvShows();
}
