package com.example.bioprep.models;

import java.io.Serializable;

public class BioGalleryItem implements Serializable {

    private final String id;
    private final String commonName;
    private final String scientificName;
    private final String imageUrl;
    private final String largeImageUrl;
    private final String thumbnailUrl;
    private final String attribution;
    private final String placeGuess;

    public BioGalleryItem(String id, String commonName, String scientificName,
                          String imageUrl, String largeImageUrl, String thumbnailUrl,
                          String attribution, String placeGuess) {
        this.id = id;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.imageUrl = imageUrl;
        this.largeImageUrl = largeImageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.attribution = attribution;
        this.placeGuess = placeGuess;
    }

    public String getId() {
        return id;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getAttribution() {
        return attribution;
    }

    public String getPlaceGuess() {
        return placeGuess;
    }
}
