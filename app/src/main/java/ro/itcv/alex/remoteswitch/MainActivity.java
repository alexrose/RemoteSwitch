package ro.itcv.alex.remoteswitch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        try {
            URL url = new URL("http://192.168.0.102/switchOneStatus");
            new GetClass(this, url).execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

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
                final TextView outputView = (TextView) findViewById(R.id.switchOneStatus);

                HttpURLConnection connection = (HttpURLConnection)this.url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();

                JSONObject jObject = new JSONObject(responseOutput.toString());
                final String switchOneStatus = jObject.getString("SwitchOne");

//                Button switchOneOnButton = findViewById(R.id.switchOneOn);
//                Button switchOneOffButton = findViewById(R.id.switchOneOff);
//
//                if (switchOneStatus.equals("off")) {
//                    switchOneOnButton.setVisibility(View.VISIBLE);
//                    switchOneOffButton.setVisibility(View.GONE);
//                } else {
//                    switchOneOnButton.setVisibility(View.GONE);
//                    switchOneOffButton.setVisibility(View.VISIBLE);
//                }

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        outputView.setText(switchOneStatus);
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

    public void turnSwitchOneOn(View View) {
        try {
            URL url = new URL("http://192.168.0.102/switchOneOn");
            new GetClass(this, url).execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void turnSwitchOneOff(View View) {
        try {
            URL url = new URL("http://192.168.0.102/switchOneOff");
            new GetClass(this, url).execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
