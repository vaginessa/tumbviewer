package com.nutrition.express.videolist;

/**
 * Created by huang on 5/16/16.
 */
public class VideoInfo {
    private String resourceUrl;
    private String imageUrl;
    private int imageWidth;
    private int imageHeight;

    public VideoInfo(String resourceUrl, String imageUrl, int imageWidth, int imageHeight) {
        this.resourceUrl = resourceUrl;
        this.imageUrl = imageUrl;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }
}
