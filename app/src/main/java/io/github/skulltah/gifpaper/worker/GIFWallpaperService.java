package io.github.skulltah.gifpaper.worker;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.github.skulltah.gifpaper.R;
import io.github.skulltah.gifpaper.utils.Helper;

public class GIFWallpaperService extends WallpaperService {

    @Override
    public WallpaperService.Engine onCreateEngine() {
        return new GIFWallpaperEngine();
    }

    private class GIFWallpaperEngine extends WallpaperService.Engine
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private final Handler handler;

        private SurfaceHolder holder;
        private SharedPreferences prefs;
        private boolean visible;

        private float screenHeight, screenWidth;
        // Videos
        private MediaPlayer mediaPlayer;
        // GIFs
        private int frameDuration = 100 / 3;
        private Movie movie;
        private String imageLocation;
        private float scaleX, scaleY, scale;
        private float offset;
        private Paint paint;
        private Runnable drawGIF = new Runnable() {
            public void run() {
                draw();
            }
        };

        public GIFWallpaperEngine() {
            handler = new Handler();

            prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            prefs.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            this.holder = surfaceHolder;
            this.paint = new Paint();

            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);

            DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
            this.screenHeight = displayMetrics.heightPixels;
            this.screenWidth = displayMetrics.widthPixels;
//            screenHeight = 960;
//            screenWidth = 540;
            loadBackgroundFromPrefs();
        }

        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key == null) return;

            switch (key) {
                case "image_custom":
                    loadBackgroundFromPrefs();
                    break;
                case "frame_rate":
                    frameDuration = prefs.getInt("frame_rate", 100 / 3);
                    break;
                case "antialias":
                    paint.setAntiAlias(prefs.getBoolean("antialias", true));
                    break;
                case "filter_bitmap":
                    paint.setFilterBitmap(prefs.getBoolean("filter_bitmap", true));
                    break;
                case "dither":
                    paint.setDither(prefs.getBoolean("dither", true));
                    break;
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            this.visible = visible;
            if (visible) {
                handler.post(drawGIF);
            } else {
                handler.removeCallbacks(drawGIF);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            this.screenHeight = height;
            this.screenWidth = width;
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

            this.holder = holder;
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            visible = false;
            handler.removeCallbacks(drawGIF);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                                     float xStep, float yStep, int xPixels, int yPixels) {
            super.onOffsetsChanged(xOffset, yOffset, xStep, yStep, xPixels, yPixels);

            offset = -xOffset * (GIFWidth - screenWidth);

            draw();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            handler.removeCallbacks(drawGIF);
        }

        private void draw() {
            if (movie == null
                    || mediaPlayer != null)
                return;

            try {
                if (visible) {
                    Canvas canvas = holder.lockCanvas();
                    canvas.save();

                    // Adjust size and position so that
                    // the image looks good on your screen
                    canvas.scale(scale, scale);

                    movie.draw(canvas, offset, 0, paint);

                    canvas.restore();

                    holder.unlockCanvasAndPost(canvas);

                    movie.setTime((int) (System.currentTimeMillis() % movie.duration()));

                    handler.removeCallbacks(drawGIF);
                    handler.postDelayed(drawGIF, frameDuration);
                }
            } catch (Exception ex) {
                Helper.log(ex);
            }
        }

        public void setMovie(Movie movie) {
            if (movie == null)
                return;

            this.movie = movie;

            calculateScale(GIFWidth, GIFHeight);

            visible = isVisible();

            if (visible) {
                handler.post(drawGIF);
            }
        }

        private void calculateScale(float sourceWidth, float sourceHeight) {
            float scaleX = screenWidth / sourceWidth;
            float scaleY = screenHeight / sourceHeight;

            this.scaleX = scaleX;
            this.scaleY = scaleY;

            float ratio = Math.max(
                    (float) screenHeight / sourceWidth,
                    (float) screenHeight / sourceHeight);
            int width = Math.round((float) ratio * sourceWidth);
            int height = Math.round((float) ratio * sourceHeight);

            this.scale = ratio;
        }

        private void loadBackgroundFromPrefs() {
            imageLocation = prefs.getString("image_custom", null);

            if (imageLocation == null) {
                fallbackToSample();
            } else {
                if (imageLocation.endsWith(".gif")) {
                    getGIF();
                } else if (imageLocation.endsWith(".gifv")
                        || imageLocation.endsWith(".webm")
                        || imageLocation.endsWith(".mp4")) {
                    getVideo();
                }
            }
        }

        private float GIFWidth,
                GIFHeight;

        private void getGIF() {
            File file = new File(imageLocation);

            if (file.exists()) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    Movie movie = Movie.decodeStream(fileInputStream);

                    GIFWidth = movie.width();
                    GIFHeight = movie.height();

                    setMovie(movie);
                    mediaPlayer = null;
                } catch (FileNotFoundException ex) {
                    Helper.log(ex);
                    fallbackToSample();
                }
            }
        }

        private void getVideo() {
//            File file = new File(imageLocation);
//
//            if (file.exists()) {
//                try {
//                    FileInputStream fileInputStream = new FileInputStream(file);
//
//                    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sample);
//                    mediaPlayer.setDisplay(holder);
//                    mediaPlayer.setDataSource(fileInputStream.getFD());
//                    mediaPlayer.prepare();
//                    mediaPlayer.start();
//
//                    movie = null;
//                } catch (IOException ioe) {
//                    Helper.log(ioe);
//                    fallbackToSample();
//                }
//            }
        }

        private void fallbackToSample() {
            if (holder == null)
                return;

            try {
                InputStream is = getResources().openRawResource(R.raw.samplegif);

                if (is != null) {
                    try {
                        movie = Movie.decodeStream(is);

                        setMovie(movie);

                        mediaPlayer = null;
                    } finally {
                        is.close();
                    }
                } else {
                    throw new IOException("Unable to open R.raw.sample");
                }
            } catch (Exception ex) {
                Helper.log(ex);
            }
        }
    }
}