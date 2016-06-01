package io.github.skulltah.gifpaper.imgur;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import io.github.skulltah.gifpaper.R;
import io.github.skulltah.gifpaper.imgur.json.Datum;
import io.github.skulltah.gifpaper.imgur.json.ImgurGallery;
import io.github.skulltah.gifpaper.utils.Helper;

public class ImgurImageAdapter extends RecyclerView.Adapter<ImgurImageAdapter.MyViewHolder> {

    private Activity context;
    private ImageLoader imageLoader;
    private ImgurGallery imgurGallery;

    public ImgurImageAdapter(Activity context, ImgurGallery imgurGallery) {
        this.context = context;
        this.imgurGallery = imgurGallery;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            Helper.log(ex);
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
                || imgurGallery == null)
            return;

        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }

        final Datum imgurPost = imgurGallery.data.get(position);

        if (imgurPost.isAlbum || !imgurPost.animated)
            return;

        final String gifUrl = String.format("http://i.imgur.com/%s.gif", imgurPost.hash);
        final String thumbnailUrl = String.format("http://i.imgur.com/%ss.gif", imgurPost.hash);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder_cloud)
                .build();

        imageLoader.displayImage(thumbnailUrl, holder.imageView, options);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage(gifUrl, imgurPost.hash, holder.imageView.getRootView());
            }
        });

        holder.imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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

                return false;
            }
        });
    }

    private void downloadImage(final String gifUrl, final String hash, final View rootView) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Downloading GIF...");
        progressDialog.setMessage("Please be patient.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Thread mThread = new Thread() {
            @Override
            public void run() {
                File storagePath = new File(Environment
                        .getExternalStorageDirectory()
                        + "/gifpaper/downloaded/");
                if (!storagePath.mkdirs()) {
//                    progressDialog.dismiss();
//                    Snackbar.make(rootView, "GIF failed to download.", Snackbar.LENGTH_SHORT).show();
//                    return;
                }

                File file = new File(storagePath, hash + ".gif");

                try {
                    InputStream is = new URL(gifUrl).openStream();

                    // Set up OutputStream to write data into image file.
                    OutputStream os = new FileOutputStream(file);

                    CopyStream(is, os);
                } catch (IOException ioe) {
                    progressDialog.dismiss();
                    Snackbar.make(rootView, "GIF failed to download.", Snackbar.LENGTH_SHORT).show();
                    Helper.log(ioe);
                    return;
                }

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("image_custom", file.getPath());
                editor.commit();

                progressDialog.dismiss();
                Snackbar.make(rootView, "GIF set as wallpaper.", Snackbar.LENGTH_SHORT).show();

                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    Movie movie = Movie.decodeStream(fileInputStream);
                    Helper.showGifToast(context, movie);
                } catch (Exception ex) {
                    Helper.log(ex);
                }
            }
        };
        mThread.start();
    }

    @Override
    public int getItemCount() {
        if (imgurGallery == null) return 0;
        return imgurGallery.data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageview);
        }
    }
}