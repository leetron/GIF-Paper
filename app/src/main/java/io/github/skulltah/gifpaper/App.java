package io.github.skulltah.gifpaper;

import android.app.Application;

import com.splunk.mint.Mint;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Mint.initAndStartSession(this, "1bc707c0");
    }
}
