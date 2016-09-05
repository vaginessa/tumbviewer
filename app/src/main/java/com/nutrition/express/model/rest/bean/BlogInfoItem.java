package com.nutrition.express.model.rest.bean;

/**
 * Created by huang on 2/17/16.
 */
public class BlogInfoItem {
    private String title;
    private String name;
    private String total_posts;
    private int posts;
    private String url;
    private int updated;
    private String description;
    private boolean is_nsfw;
    private boolean ask;
    private String ask_page_title;
    private boolean ask_anon;
    private boolean share_likes;
    private int likes;

    public String getTitle() {
        return title;
    }

    public int getPosts() {
        return posts;
    }

    public String getName() {
        return name;
    }

    public int getUpdated() {
        return updated;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAsk() {
        return ask;
    }

    public boolean isAsk_anon() {
        return ask_anon;
    }

    public int getLikes() {
        return likes;
    }

    public String getTotal_posts() {
        return total_posts;
    }

    public String getUrl() {
        return url;
    }

    public boolean is_nsfw() {
        return is_nsfw;
    }

    public String getAsk_page_title() {
        return ask_page_title;
    }

    public boolean isShare_likes() {
        return share_likes;
    }

}
