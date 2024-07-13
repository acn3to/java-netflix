package services;

import entities.Media;

import java.util.ArrayList;
import java.util.List;

public class MediaService {
    private List mediasList <Media> = new ArrayList<Media>();

    public void findMediaById(int id) {
        for (Media media : mediasList) {
            if (media.getId() == id) {
                System.out.println(media);
            }
        }
    }

    public void registerMedia(Media media) {
        mediasList.add(media);
    }
    public void displayMedia(Media media) {
        System.out.println(getMediasList());
    }

    public void playMedia(List medias) {
        for (Media media : medias) {
            System.out.println(media);
            System.out.println("Playing " + media.getTitle());
        }
    }

    public void pauseMedia(List medias) {
        for (Media media : medias) {
            System.out.println(media);
            System.out.println("Pausing " + media.getTitle());
        }
    }

    public void stopMedia(List medias) {
        for (Media media : medias) {
            System.out.println(media);
            System.out.println("Stopping " + media.getTitle());
        }
    }

    public void selectMedia(String title) {
        for (Media media : mediasList) {
            if (media.getTitle().equalsIgnoreCase(title)) {
                System.out.println(media);
            }
        }
    }
    public void exitMedia(List medias) {
        for (Media media : medias) {
            System.out.println(media);
            System.out.println("Exiting " + media.getTitle());
        }
    }

    public void deleteMedia(int id) {
        for (int i = 0; i < mediasList.size(); i++) {
            Media media = mediasList.get(i);
            if (media.getId() == id) {
                mediasList.remove(i);
                break;
            }
        }
    }

    public void displayCategories(){
        System.out.println(CATEGORY);
    }
    public void displayByCategory(String category) {
        for (Media media : mediasList) {
            if (media.getCategory().equalsIgnoreCase(category)) {
                System.out.println(media);
            }
        }
    }

    public MediaService(List mediasList) {
        this.mediasList = mediasList;
    }

    public List getMediasList() {
        return mediasList;
    }

    public void setMediasList(List mediasList) {
        this.mediasList = mediasList;
    }
}
