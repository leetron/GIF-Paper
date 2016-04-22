package io.github.skulltah.gifpaper.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.channguyen.rsv.RangeSliderView;

import io.github.skulltah.gifpaper.R;

public class PrefsActivity extends AppCompatActivity {
    RangeSliderView rangeSliderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);

        rangeSliderView = (RangeSliderView) findViewById(R.id.rangeSliderView);
        final TextView tvHint = (TextView) findViewById(R.id.tv_fps);
        final Button buttonAdvancedSettings = (Button) findViewById(R.id.button_advanced_settings);

        if (buttonAdvancedSettings != null) {
            buttonAdvancedSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(PrefsActivity.this, SettingsActivity.class);
                    startActivity(i);
                }
            });
        }

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int frameRate = prefs.getInt("frame_rate", 100 / 3);

        rangeSliderView.setInitialIndex((100 / frameRate) - 1);

        if (tvHint != null) {
            tvHint.setText(getHint((100 / frameRate) - 1));
        }

        rangeSliderView.setOnSlideListener(new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                if (tvHint != null) {
                    tvHint.setText(getHint(index));
                }

                int frameUpdateRate = 100 / (index + 1);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("frame_rate", frameUpdateRate);
                editor.commit();
            }
        });
    }

    private String getHint(int index) {
        switch (index) {
            default:
            case 0:
                return "10 fps, jittery but battery efficient.";
            case 1:
                return "20 fps, a bit jittery but battery efficient.";
            case 2:
                return "30 fps, reasonably smooth and battery efficient.";
            case 3:
                return "40 fps, smooth but will use a lot of power.";
            case 4:
                return "50 fps, silky smooth but might heat up your phone.";
            case 5:
                return "60 fps, practically a movie; will devour your battery.";
        }
    }
}
