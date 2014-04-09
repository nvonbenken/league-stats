package edu.appdesign.leaguestats;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Nate on 4/7/14.
 */
public class RunesActivity extends Activity {

    public static String page;
    TextView textName;
    Spinner spinner;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rune_activity);
        spinner = (Spinner) findViewById(R.id.rune_selector);
        addListenerOnSpinnerSelection();
        GetRunes getRunes = new GetRunes();
        getRunes.execute();

    }

    public void addListenerOnSpinnerSelection() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(adapterView.getContext(),
                        "Page Selected: " + adapterView.getItemAtPosition(i).toString(),
                        Toast.LENGTH_SHORT).show();
                page = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    class GetRunes extends AsyncTask<String, String, JSONObject> {

        private String api_key="d96236d2-6ee3-4cfd-afa7-f41bdbc11128";
        String region = MainActivity.region.toLowerCase();
        String id = StatsActivity.sumID;
        String encodedKey = null;
        String encodedRegion = null;
        String encodedId = null;
        String url = null;


        // JSON Node Names
        String TAG_NAME = "name";
        String TAG_CURRENT = "current";
        String TAG_SLOTS = "slots";
        String TAG_RUNEID = "runeId";
        String TAG_RUNESLOTID = "runeSlotId";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {

                // Assign views
                textName = (TextView) findViewById(R.id.name);

                // Encode URL variables
                encodedId = URLEncoder.encode(id, "UTF-8");
                encodedKey = URLEncoder.encode(api_key, "UTF-8");
                encodedRegion = URLEncoder.encode(region, "UTF-8");

                url = "http://prod.api.pvp.net/api/lol/" + region + "/v1.4/summoner/" + id + "/runes?api_key=" + api_key;
                Log.i("..........", url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            JSONParser jParser = new JSONParser();

            // Get JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
            Log.i("............", "" + json);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                // Get JSON Object
                JSONObject runes = json.getJSONObject(encodedId);

                // Get JSON Array node
                JSONArray rune = runes.getJSONArray("pages");

                // Loop through pages, page names stored in string array
                String[] name = new String[rune.length()];
                String curr;
                for(int i = 0; i < rune.length(); i++) {
                    JSONObject c = rune.getJSONObject(i);
                    name[i] = c.getString(TAG_NAME);
                    curr = c.getString(TAG_CURRENT);

                    if(curr.equals("true"))
                       name[i] = name[i] + " [Active]";

                    Log.i(".........", name[i]);
                }

                // Set TextView
                textName.setText(name[0]);

                // Show page names on Spinner



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

