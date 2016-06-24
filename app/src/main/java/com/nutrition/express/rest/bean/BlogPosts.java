package com.nutrition.express.rest.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by huang on 2/19/16.
 */
public class BlogPosts {
    @SerializedName("total_posts")
    private int count;
    @SerializedName("posts")
    private ArrayList<PostsItem> list;

    public int getCount() {
        return count;
    }

    public ArrayList<PostsItem> getList() {
        return list;
    }
}
