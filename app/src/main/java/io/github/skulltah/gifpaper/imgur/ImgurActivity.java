package io.github.skulltah.gifpaper.imgur;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greysonparrelli.permiso.Permiso;

import io.github.skulltah.gifpaper.Helper;
import io.github.skulltah.gifpaper.R;
import io.github.skulltah.gifpaper.imgur.json.ImgurGallery;
import tr.xip.errorview.ErrorView;

public class ImgurActivity extends AppCompatActivity {

    private static final String rCinemagraphsUrl = "http://imgur.com/r/cinemagraphs/top/all.json";

    RecyclerView recyclerView;
    ImgurImageAdapter adapter;
    ErrorView errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgur);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        errorView = (ErrorView) findViewById(R.id.error_view);

        Permiso.getInstance().setActivity(this);

        requestPermissions();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted();
            return;
        }

        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
            @Override
            public void onPermissionResult(Permiso.ResultSet resultSet) {
                if (resultSet.areAllPermissionsGranted()) {
                    // Permission granted!
                    permissionsGranted();
                } else {
                    // Permission denied.
                    errorView.setTitle("Permission denied");
                    errorView.setSubtitle("GifPaper needs access to your files in order for it to download new GIFs.");
                    errorView.setRetryButtonText("Allow access");
                    errorView.setOnRetryListener(new ErrorView.RetryListener() {
                        @Override
                        public void onRetry() {
                            requestPermissions();
                        }
                    });
                    errorView.showRetryButton(true);
                    errorView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                Permiso.getInstance().showRationaleInDialog(
                        "Access to your GIFs",
                        "GifPaper needs access to your files in order for it to download new GIFs.",
                        null, callback);
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void permissionsGranted() {
        ImgurGallery imgurGallery = getImgurGallery();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        adapter = new ImgurImageAdapter(this, imgurGallery);
        GridLayoutManager layoutManager = new GridLayoutManager(this, (int) dpWidth / 130);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);

        if (imgurGallery == null || imgurGallery.data.size() <= 0) {
            onCouldntAccessImgur();
        }
    }

    public void onCouldntAccessImgur() {
        errorView.setTitle("Couldn't access Imgur");
        errorView.setSubtitle("Are you connected to the internet?");
        errorView.setRetryButtonText("Retry");
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                permissionsGranted();
            }
        });
        errorView.showRetryButton(true);

        errorView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Permiso.getInstance().setActivity(this);
    }

    private ImgurGallery getImgurGallery() {
        ImgurGallery imgurGallery = null;

        try {
            String json = Helper.downloadJson(this, rCinemagraphsUrl);

            if (json == null) return null;

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            imgurGallery = gson.fromJson(json, ImgurGallery.class);
        } catch (Exception e) {
            Helper.log(e);
        }

        return imgurGallery;
    }
}
