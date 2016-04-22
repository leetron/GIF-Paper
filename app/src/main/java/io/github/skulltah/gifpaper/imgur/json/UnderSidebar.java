
package io.github.skulltah.gifpaper.imgur.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class UnderSidebar {

    @SerializedName("abp")
    @Expose
    public String abp;
    @SerializedName("element")
    @Expose
    public String element;
    @SerializedName("flags")
    @Expose
    public Flags_ flags;
    @SerializedName("insert_into")
    @Expose
    public String insertInto;
    @SerializedName("prebid")
    @Expose
    public List<Prebid_> prebid = new ArrayList<Prebid_>();
    @SerializedName("sizes")
    @Expose
    public List<List<Integer>> sizes = new ArrayList<List<Integer>>();
    @SerializedName("slot_id")
    @Expose
    public String slotId;

}
