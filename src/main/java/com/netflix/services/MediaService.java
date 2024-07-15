package com.netflix.services;

import com.netflix.entities.Media;
import com.netflix.repositories.MediaRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MediaService {
    private MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public void addMedia(Media media) {
        mediaRepository.save(media);
    }

    public Media getMediaById(int id) {
        return mediaRepository.findById(id);
    }

    public List<Media> getAllMedia() {
        return mediaRepository.findAll();
    }

    public List<Media> getAllMovies() {
        return mediaRepository.findAllMovies();
    }

    public List<Media> getAllTvShows() {
        return mediaRepository.findAllTvShows();
    }

    public void updateMedia(Media media) throws Exception {
        mediaRepository.update(media);
    }

    public void deleteMedia(int id) throws Exception {
        mediaRepository.delete(id);
    }

    public List<Media> filterReleaseDateInDescendingOrder(List<Media> mediaList) {
        return mediaList.stream()
                .sorted((media1, media2) -> media2.getReleaseDate().compareTo(media1.getReleaseDate()))
                .collect(Collectors.toList());
    }

    public List<Media> filterReleaseDateInAscendingOrder(List<Media> mediaList) {
        return mediaList.stream()
                .sorted(Comparator.comparing(Media::getReleaseDate))
                .collect(Collectors.toList());
    }

    public List<Media> filterByReleaseDate(List<Media> mediaList, LocalDate initialDate, LocalDate finalDate) {
        return mediaList.stream()
                .filter(media -> !media.getReleaseDate().isBefore(initialDate) && !media.getReleaseDate().isAfter(finalDate))
                .collect(Collectors.toList());
    }

    public List<Media> filterByYearAndRating(List<Media> mediaList, int year, double minRating) {
        return mediaList.stream()
                .filter(media -> media.getReleaseDate().getYear() == year && media.getRating() >= minRating)
                .collect(Collectors.toList());
    }
}
