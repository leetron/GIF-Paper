package io.github.skulltah.gifpaper;

import android.app.WallpaperManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.greysonparrelli.permiso.PermisoActivity;

import io.github.skulltah.gifpaper.imgur.ImgurActivity;
import io.github.skulltah.gifpaper.settings.PrefsActivity;

public class MainActivity extends PermisoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Button buttonImgurActivity = (Button) findViewById(R.id.button_imgur_activity);
        Button buttonSetWallpaper = (Button) findViewById(R.id.button_set_wallpaper);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, PrefsActivity.class);
                    startActivity(i);
                }
            });
        }

        if (buttonImgurActivity != null) {
            buttonImgurActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, ImgurActivity.class);
                    startActivity(i);
                }
            });
        }

        if (buttonSetWallpaper != null) {
            buttonSetWallpaper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(MainActivity.this, "Choose 'GIF-Paper' from the list to start the Live Wallpaper.", Toast.LENGTH_LONG);
                    toast.show();

                    Intent intent = new Intent();
                    intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                    startActivity(intent);
                }
            });
        }
    }
}
