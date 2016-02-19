package com.huang.humblr.rest.bean;

/**
 * Created by huang on 2/17/16.
 */
public class BlogInfoItem {
    private String title;
    private int posts;
    private String name;
    private int updated;
    private String description;
    private boolean ask;
    private boolean ask_anon;
    private int likes;
    private boolean is_blocked_from_primary;

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

    public boolean is_blocked_from_primary() {
        return is_blocked_from_primary;
    }
}
