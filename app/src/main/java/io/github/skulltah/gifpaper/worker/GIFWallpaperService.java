package io.github.skulltah.gifpaper.worker;

import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.github.skulltah.gifpaper.Helper;
import io.github.skulltah.gifpaper.R;

public class GIFWallpaperService extends WallpaperService {

    @Override
    public WallpaperService.Engine onCreateEngine() {
        return new GIFWallpaperEngine();
    }

    private class GIFWallpaperEngine extends WallpaperService.Engine
            implements SharedPreferences.OnSharedPreferenceChangeListener {
        private final Handler handler;
        MediaPlayer mediaPlayer;
        private int frameDuration = 100 / 3;
        private SurfaceHolder holder;
        private Movie movie;
        private boolean visible;
        private SharedPreferences prefs;
        private String imageLocation;
        private float height;
        private float width;
        private float scaleX;
        private float scaleY;
        private float offset;
        private Paint paint;
        private WallpaperManager wallpaperManager;
        private int desiredMinimumWidth;
        private float widthHeightRatio;
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
            this.wallpaperManager = WallpaperManager.getInstance(getBaseContext());

            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);

            initialize();
        }

        private void initialize() {
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

            if (movie == null)
                return;

            this.width = width;
            this.height = height;

            desiredMinimumWidth = wallpaperManager.getDesiredMinimumWidth();

            scaleX = getScale(desiredMinimumWidth, movie.width(), height, movie.height());
            scaleY = getScale(desiredMinimumWidth, movie.width(), height, movie.height());

            draw();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            this.holder = holder;

            if (movie == null) return;

            desiredMinimumWidth = wallpaperManager.getDesiredMinimumWidth();

            scaleX = getScale(desiredMinimumWidth, movie.width(), height, movie.height());
            scaleY = getScale(desiredMinimumWidth, movie.width(), height, movie.height());
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
            if (movie == null) return;

            if (widthHeightRatio > 1)
                offset = -xOffset * movie.width() * xStep;
            else
                offset = xPixels;

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

                    if (scaleX <= 0)
                        scaleX = 1;
                    if (scaleY <= 0)
                        scaleY = 1;

                    // Adjust size and position so that
                    // the image looks good on your screen
                    canvas.scale(scaleX, scaleY);

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

            float w = movie.width();
            float h = movie.height();

            scaleX = getScale(desiredMinimumWidth, w, height, h);
            scaleY = getScale(desiredMinimumWidth, w, height, h);

            widthHeightRatio = w / h;

            visible = isVisible();

            if (visible) {
                handler.post(drawGIF);
            }
        }

        private float getScale(float origX, float targetX, float origY, float targetY) {
            float scaleX = getScale(origX, targetX);
            float scaleY = getScale(origY, targetY);

            if (scaleX > scaleY)
                return scaleX;

            return scaleY;
        }

        private float getScale(float orig, float target) {
            return orig / target;
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

        private void getGIF() {
            File file = new File(imageLocation);

            if (file.exists()) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);

                    Movie movie = Movie.decodeStream(fileInputStream);

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