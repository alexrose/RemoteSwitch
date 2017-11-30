package ro.itcv.alex.remoteswitch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_PREF_S1_ON = "preference_s1_on";
    public static final String KEY_PREF_S1_OFF = "preference_s1_off";
    public static final String KEY_PREF_S1_STATUS = "preference_s1_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
