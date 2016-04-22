
package io.github.skulltah.gifpaper.imgur.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ImgurGallery {

    @SerializedName("data")
    @Expose
    public List<Datum> data = new ArrayList<Datum>();
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("success")
    @Expose
    public Boolean success;
}
