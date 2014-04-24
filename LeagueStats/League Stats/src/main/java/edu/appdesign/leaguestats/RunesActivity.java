package edu.appdesign.leaguestats;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Nate on 4/7/14.
 */
public class RunesActivity extends BaseActivity {

    Spinner spinner;
    ArrayAdapter<String> adapter;
    String[] runeIdArray = new String[6000];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rune_activity);
        getActionBar().setTitle("Runes");
        GetRunes getRunes = new GetRunes();
        getRunes.execute();
        spinner = (Spinner) findViewById(R.id.rune_selector);
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
        String TAG_PAGES = "pages";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                // Encode URL variables
                encodedId = URLEncoder.encode(id, "UTF-8");
                encodedKey = URLEncoder.encode(api_key, "UTF-8");
                encodedRegion = URLEncoder.encode(region, "UTF-8");

                url = "http://prod.api.pvp.net/api/lol/" + region + "/v1.4/summoner/" + id + "/runes?api_key=" + api_key;
                Log.i("Runes URL", url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            JSONParser jParser = new JSONParser();

            // Get JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
            Log.i("Main JSON", "" + json);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                // Get JSON Object
                JSONObject runesObject = json.getJSONObject(encodedId);
                Log.i("Runes JSON", "" + runesObject);


                // Get JSON Array node
                JSONArray rune = runesObject.getJSONArray(TAG_PAGES);
                Log.i("Rune JSON", "" + rune);


                String[] name = new String[rune.length()];
                int prevId = 0;
                String curr;
                final String[][] runesArray = new String[rune.length()][27];
                ArrayList<String> runePageNames = new ArrayList<String>();
                GetStaticData getStaticData = new GetStaticData();

                // Loop through pages, page names stored in string array
                for(int i = 0; i < rune.length(); i++) {
                    JSONObject c = rune.getJSONObject(i);
                    JSONArray slots = c.getJSONArray(TAG_SLOTS);
                    name[i] = c.getString(TAG_NAME);

                    for(int x = 0; x<27; x++) {
                        JSONObject s = slots.getJSONObject(x);

                        if(Integer.valueOf(s.getString(TAG_RUNEID)).equals(prevId)) {
                            runesArray[i][x] = runesArray[i][x - 1];
                            prevId = Integer.valueOf(s.getString(TAG_RUNEID));
                        }
                        else if(Integer.valueOf(s.getString(TAG_RUNEID)).equals(runeIdArray[Integer.valueOf(s.getString(TAG_RUNEID))]))
                            runesArray[i][x] = runeIdArray[Integer.valueOf(s.getString(TAG_RUNEID))];

                        else
                            runesArray[i][x] = getStaticData.getRuneInfo(s.getString(TAG_RUNEID));
                            runeIdArray[Integer.valueOf(s.getString(TAG_RUNEID))] = runesArray[i][x];
                    }

                    curr = c.getString(TAG_CURRENT);

                    if(curr.equals("true"))
                       name[i] = name[i] + " [Active]";
                    runePageNames.add(name[i]);

                    Log.i("Page Names", name[i]);
                }

                adapter = new ArrayAdapter(RunesActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        runePageNames);

                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#C49246"));
                        Toast.makeText(adapterView.getContext(),
                                "Page Selected: " + adapterView.getItemAtPosition(i).toString(),
                                Toast.LENGTH_SHORT).show();
                        TextView runes = (TextView) findViewById(R.id.testRunes);
                        runes.setText("Runes: " + "\n");

                        for(int j = 0; j<27; j++) {
                            runes.append(runesArray[i][j]);
                            runes.append("\n");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

