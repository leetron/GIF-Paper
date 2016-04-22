
package io.github.skulltah.gifpaper.imgur.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Prebid {

    @SerializedName("bidder")
    @Expose
    public String bidder;
    @SerializedName("params")
    @Expose
    public Params params;

}
