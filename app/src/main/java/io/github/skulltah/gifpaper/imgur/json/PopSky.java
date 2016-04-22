
package io.github.skulltah.gifpaper.imgur.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class PopSky {

    @SerializedName("element")
    @Expose
    public String element;
    @SerializedName("flags")
    @Expose
    public Flags flags;
    @SerializedName("insert_after")
    @Expose
    public String insertAfter;
    @SerializedName("prebid")
    @Expose
    public List<Prebid> prebid = new ArrayList<Prebid>();
    @SerializedName("sizes")
    @Expose
    public List<List<Integer>> sizes = new ArrayList<List<Integer>>();
    @SerializedName("slot_id")
    @Expose
    public String slotId;

}
