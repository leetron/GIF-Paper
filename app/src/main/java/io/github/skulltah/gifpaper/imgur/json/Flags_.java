
package io.github.skulltah.gifpaper.imgur.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Flags_ {

    @SerializedName("adblockplus")
    @Expose
    public Adblockplus adblockplus;
    @SerializedName("album")
    @Expose
    public Boolean album;
    @SerializedName("album_page")
    @Expose
    public Boolean albumPage;
    @SerializedName("gallery")
    @Expose
    public Boolean gallery;
    @SerializedName("gallery_nav")
    @Expose
    public Boolean galleryNav;
    @SerializedName("in_gallery")
    @Expose
    public Boolean inGallery;
    @SerializedName("logged_in")
    @Expose
    public Boolean loggedIn;
    @SerializedName("mature")
    @Expose
    public Boolean mature;
    @SerializedName("moderated")
    @Expose
    public Moderated moderated;
    @SerializedName("moderated_nsfw")
    @Expose
    public Boolean moderatedNsfw;
    @SerializedName("nsfw")
    @Expose
    public Boolean nsfw;
    @SerializedName("other")
    @Expose
    public Boolean other;
    @SerializedName("page_load")
    @Expose
    public Boolean pageLoad;
    @SerializedName("pro")
    @Expose
    public Boolean pro;
    @SerializedName("promoted")
    @Expose
    public Boolean promoted;
    @SerializedName("referer")
    @Expose
    public Boolean referer;
    @SerializedName("secure")
    @Expose
    public Secure secure;
    @SerializedName("share")
    @Expose
    public Boolean share;
    @SerializedName("spam")
    @Expose
    public Boolean spam;
    @SerializedName("subreddit")
    @Expose
    public Boolean subreddit;
    @SerializedName("subreddit_nsfw")
    @Expose
    public Boolean subredditNsfw;
    @SerializedName("under_10")
    @Expose
    public Boolean under10;
    @SerializedName("unmoderated")
    @Expose
    public Boolean unmoderated;
    @SerializedName("user_page")
    @Expose
    public Boolean userPage;

}
