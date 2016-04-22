
package io.github.skulltah.gifpaper.imgur.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Params {

    @SerializedName("network")
    @Expose
    public String network;
    @SerializedName("placement")
    @Expose
    public String placement;
    @SerializedName("sizeId")
    @Expose
    public String sizeId;

}
