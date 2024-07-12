package services;

import entities.Category;
import entities.Media;
import repositories.MediaRepository;

import java.util.ArrayList;
import java.util.List;

public class MediaService {
    private MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public void addMedia(Media media) {
        mediaRepository.save(media);
    }

    public Media getMediaById(Long id) {
        return mediaRepository.findById(id);
    }

    public List<Media> getAllMedia() {
        return mediaRepository.findAll();
    }

    public void updateMedia(Media media) {
        mediaRepository.update(media);
    }

    public void deleteMedia(Long id) {
        mediaRepository.delete(id);
    }

    public MediaRepository getMediaRepository() {
        return mediaRepository;
    }

    public void setMediaRepository(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }
}
