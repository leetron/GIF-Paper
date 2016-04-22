
package io.github.skulltah.gifpaper.imgur.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("account_id")
    @Expose
    public Object accountId;
    @SerializedName("account_url")
    @Expose
    public Object accountUrl;
    @SerializedName("album_cover")
    @Expose
    public Object albumCover;
    @SerializedName("album_cover_height")
    @Expose
    public Integer albumCoverHeight;
    @SerializedName("album_cover_width")
    @Expose
    public Integer albumCoverWidth;
    @SerializedName("animated")
    @Expose
    public Boolean animated;
    @SerializedName("author")
    @Expose
    public String author;
    @SerializedName("bandwidth")
    @Expose
    public String bandwidth;
    @SerializedName("create_datetime")
    @Expose
    public String createDatetime;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("ext")
    @Expose
    public String ext;
    @SerializedName("favorited")
    @Expose
    public Boolean favorited;
    @SerializedName("hash")
    @Expose
    public String hash;
    @SerializedName("height")
    @Expose
    public Integer height;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("is_album")
    @Expose
    public Boolean isAlbum;
    @SerializedName("looping")
    @Expose
    public Boolean looping;
    @SerializedName("mimetype")
    @Expose
    public String mimetype;
    @SerializedName("nsfw")
    @Expose
    public Boolean nsfw;
    @SerializedName("num_images")
    @Expose
    public Integer numImages;
    @SerializedName("place")
    @Expose
    public Place place;
    @SerializedName("prefer_video")
    @Expose
    public Boolean preferVideo;
    @SerializedName("reddit")
    @Expose
    public String reddit;
    @SerializedName("score")
    @Expose
    public Integer score;
    @SerializedName("section")
    @Expose
    public String section;
    @SerializedName("size")
    @Expose
    public Integer size;
    @SerializedName("subreddit")
    @Expose
    public String subreddit;
    @SerializedName("timestamp")
    @Expose
    public String timestamp;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("video_host")
    @Expose
    public Object videoHost;
    @SerializedName("video_source")
    @Expose
    public Object videoSource;
    @SerializedName("views")
    @Expose
    public Integer views;
    @SerializedName("width")
    @Expose
    public Integer width;

}
