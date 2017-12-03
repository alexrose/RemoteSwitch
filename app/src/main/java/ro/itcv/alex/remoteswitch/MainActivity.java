package ro.itcv.alex.remoteswitch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
            }
        });

        try {
            URL url = new URL(getPreferenceSwitchStatus());
            new GetClass(this, url).execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        final Switch switchOneToggle = findViewById(R.id.switchOneToggle);

        switchOneToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchOneToggle.isChecked())
                    turnSwitchOne(true);
                else
                    turnSwitchOne(false);
            }
        });

        final Switch switchTwoToggle = findViewById(R.id.switchTwoToggle);
        switchTwoToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchTwoToggle.isChecked())
                    turnSwitchTwo(true);
                else
                    turnSwitchTwo(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetClass extends AsyncTask<String, Void, Void> {

        private final Context context;
        private final URL url;

        public GetClass(Context c, URL url){
            this.context = c;
            this.url = url;
        }

        protected void onPreExecute(){
            progress= new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                final Switch switchOneToggle = findViewById(R.id.switchOneToggle);
                final Switch switchTwoToggle = findViewById(R.id.switchTwoToggle);

                HttpURLConnection connection = (HttpURLConnection)this.url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");

                String line = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseOutput = new StringBuilder();
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();

                JSONObject jObject = new JSONObject(responseOutput.toString());
                final String switchOneStatus = jObject.getString(getPreferenceS1Id());
                final String switchTwoStatus = jObject.getString(getPreferenceS2Id());

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (switchOneStatus.equals("off")) {
                            switchOneToggle.setChecked(false);
                        } else {
                            switchOneToggle.setChecked(true);
                        }

                        if (switchTwoStatus.equals("off")) {
                            switchTwoToggle.setChecked(false);
                        } else {
                            switchTwoToggle.setChecked(true);
                        }

                        progress.dismiss();
                    }
                });

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void turnSwitchOne(Boolean turnOn) {
        try {
            if (turnOn) {
                URL url = new URL(getPreferenceS1On());
                new GetClass(this, url).execute();
            } else {
                URL url = new URL(getPreferenceS1Off());
                new GetClass(this, url).execute();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void turnSwitchTwo(Boolean turnOn) {
        try {
            if (turnOn) {
                URL url = new URL(getPreferenceS2On());
                new GetClass(this, url).execute();
            } else {
                URL url = new URL(getPreferenceS2Off());
                new GetClass(this, url).execute();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private String getPreferenceSwitchStatus() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        return sharedPref.getString(SettingsActivity.KEY_PREF_SWITCH_STATUS, "http://localhost/switch-get-status");
    }

    private String getPreferenceS1On() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        return sharedPref.getString(SettingsActivity.KEY_PREF_S1_ON, "http://localhost/switch-on");
    }

    private String getPreferenceS1Off() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        return sharedPref.getString(SettingsActivity.KEY_PREF_S1_OFF, "http://localhost/switch-off");
    }

    public String getPreferenceS1Id() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        return sharedPref.getString(SettingsActivity.KEY_PREF_S1_ID, "s1id");
    }

    public String getPreferenceS2On() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        return sharedPref.getString(SettingsActivity.KEY_PREF_S2_ON, "http://localhost/switch-on");
    }

    private String getPreferenceS2Off() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        return sharedPref.getString(SettingsActivity.KEY_PREF_S2_OFF, "http://localhost/switch-off");
    }

    private String getPreferenceS2Id() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        return sharedPref.getString(SettingsActivity.KEY_PREF_S2_ID, "s2id");
    }
}
