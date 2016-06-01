package io.github.skulltah.gifpaper.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import io.github.skulltah.gifpaper.R;

public class Helper {

    private static int timeout = 3000;

    /**
     * Getting All Images Path
     *
     * @param activity
     * @return ArrayList with images Path
     */
    public static ArrayList<String> getAllShownImagesPath(Context activity) {

        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                if (absolutePathOfImage.endsWith(".gif")
                        || absolutePathOfImage.endsWith(".gifv")
                        || absolutePathOfImage.endsWith(".webm")
//                        || absolutePathOfImage.endsWith(".mp4")
                        )
                    listOfAllImages.add(absolutePathOfImage);
            }

            cursor.close();
        }
        return listOfAllImages;
    }

    public static void log(Exception ex) {
        ex.printStackTrace();//TODO
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String downloadJson(Context context, String url) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (!isNetworkAvailable(context))
            return null;//TODO
//            return CacheHelper.retrieve(context, url);

        String response = null;

        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();

                    response = sb.toString();

//                    CacheHelper.save(context, url, response);
            }

        } catch (IOException e) {
            log(e);

//            response = CacheHelper.retrieve(context, url);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception e) {
                    log(e);
                }
            }
        }

        return response;
    }

    public static void showGifToast(final Activity context, final Movie movie) {
        final LayoutInflater inflater = context.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.gif_toast, null);
        final GifView gifView = (GifView) layout.findViewById(R.id.gif_view);

        gifView.setMovie(movie);

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Toast toastLocal = new Toast(context);
                toastLocal.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toastLocal.setDuration(Toast.LENGTH_LONG);
                toastLocal.setView(layout);
                toastLocal.show();
            }
        });
    }
}
