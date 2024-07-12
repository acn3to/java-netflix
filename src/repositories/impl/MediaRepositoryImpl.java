package repositories.impl;

import entities.Media;
import repositories.MediaRepository;

import java.util.ArrayList;
import java.util.List;

public class MediaRepositoryImpl implements MediaRepository {
    private List<Media> medias = new ArrayList<>();
    private int idMedia = 0;

    @Override
    public void save(Media media) {
        media.setId(idMedia++);
        medias.add(media);
    }

    @Override
    public Media findById(Long id) {
        return medias.stream()
                .filter(media -> media.getId().equals(id))
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
        medias.removeIf(media -> media.getId().equals(id));
    }
}
