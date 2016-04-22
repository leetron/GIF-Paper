
package io.github.skulltah.gifpaper.imgur.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Place {

    @SerializedName("keywords")
    @Expose
    public List<String> keywords = new ArrayList<String>();
    @SerializedName("pop_sky")
    @Expose
    public PopSky popSky;
    @SerializedName("top_banner")
    @Expose
    public Object topBanner;
    @SerializedName("under_sidebar")
    @Expose
    public UnderSidebar underSidebar;

}
