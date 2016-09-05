package com.nutrition.express.model.rest.bean;

import java.util.List;

/**
 * Created by huang on 2/18/16.
 */
public class PostsItem {
    private String blog_name;
    private long id;
    private String post_url;
    private String slug;
    private String type;
    private String date;
    private long timestamp;
    private String state;
    private String format;
    private String reblog_key;
    private List<String> tags;
    private String short_url;
    private String summary;
    private String recommended_source;
    private String recommended_color;
    //// TODO: 4/24/16 unknown
    private List<String> highlighted;
    private long note_count;
    private String caption;
    private ReblogItem reblog;
    private List<TrailItem> trail;
    private String video_url;
    private boolean html5_capable;
    private String thumbnail_url;
    private int thumbnail_width;
    private int thumbnail_height;
    private int duration;
    private List<PlayerItem> player;
    private String viedo_type;
    private long liked_timestamp;

    public String getBlog_name() {
        return blog_name;
    }

    public long getId() {
        return id;
    }

    public String getPost_url() {
        return post_url;
    }

    public String getSlug() {
        return slug;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getState() {
        return state;
    }

    public String getFormat() {
        return format;
    }

    public String getReblog_key() {
        return reblog_key;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getShort_url() {
        return short_url;
    }

    public String getSummary() {
        return summary;
    }

    public String getRecommended_source() {
        return recommended_source;
    }

    public String getRecommended_color() {
        return recommended_color;
    }

    public List<String> getHighlighted() {
        return highlighted;
    }

    public long getNote_count() {
        return note_count;
    }

    public String getCaption() {
        return caption;
    }

    public ReblogItem getReblog() {
        return reblog;
    }

    public List<TrailItem> getTrail() {
        return trail;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public boolean isHtml5_capable() {
        return html5_capable;
    }

    public int getThumbnail_width() {
        return thumbnail_width;
    }

    public int getThumbnail_height() {
        return thumbnail_height;
    }

    public int getDuration() {
        return duration;
    }

    public List<PlayerItem> getPlayer() {
        return player;
    }

    public String getViedo_type() {
        return viedo_type;
    }

    public long getLiked_timestamp() {
        return liked_timestamp;
    }
}
