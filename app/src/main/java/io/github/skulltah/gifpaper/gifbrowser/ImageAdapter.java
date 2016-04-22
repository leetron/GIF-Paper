package io.github.skulltah.gifpaper.gifbrowser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;

import io.github.skulltah.gifpaper.Helper;
import io.github.skulltah.gifpaper.R;
import io.github.skulltah.gifpaper.imgur.ImgurActivity;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    ImageLoader imageLoader;
    private ArrayList<String> items;

    public ImageAdapter(Context c) {
        imageLoader = ImageLoader.getInstance();
        items = Helper.getAllShownImagesPath(c);
        imageLoader.init(ImageLoaderConfiguration.createDefault(c));

        if (items == null || items.size() <= 0) {
            Intent i = new Intent(c, ImgurActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.images_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Context context = holder.imageView.getContext();
        if (context == null
                || items == null)
            return;

        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }

        final String location = items.get(position);
        imageLoader.displayImage("file:///" + location, holder.imageView, new ImageSize(120, 120));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(holder.imageView.getRootView(), "Setting GIF as wallpaper...", Snackbar.LENGTH_INDEFINITE).show();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("image_custom", location);
                editor.commit();

                Snackbar.make(holder.imageView.getRootView(), "GIF set as wallpaper!", Snackbar.LENGTH_SHORT).show();
            }
        });

        holder.imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ImageView view = (ImageView) v;
                            //overlay is black with transparency of 0x77 (119)
                            view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                            view.invalidate();
                            break;
                        }
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL: {
                            ImageView view = (ImageView) v;
                            //clear the overlay
                            view.getDrawable().clearColorFilter();
                            view.invalidate();
                            break;
                        }
                    }
                } catch (Exception ex) {
                    Helper.log(ex);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageview);
        }
    }
}