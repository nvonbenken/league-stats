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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Nate on 4/7/14.
 */
public class RunesActivity extends BaseActivity {

    Spinner spinner;
    ArrayAdapter<String> adapter;

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
        String url2 = null;

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
                url2 = "https://prod.api.pvp.net/api/lol/static-data/" + region + "/v1.2/rune?api_key=" + api_key;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            JSONParser jParser = new JSONParser();

            // Get JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
            JSONObject runeInfo = jParser.getJSONFromUrl(url2);

            // Get JSON containing Rune Info and cache it
            try {
                ObjectOutput out = new ObjectOutputStream(new FileOutputStream(new File(getCacheDir(),"")+"cacheFile.srl"));
                out.writeObject( runeInfo.toString() );
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

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

                // Load Saved file
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(new File(getCacheDir(),"")+"cacheFile.srl")));
                String storedRuneInfo = (String) in.readObject();
                JSONObject jsonObject = new JSONObject(storedRuneInfo);
                JSONObject dataObject = jsonObject.getJSONObject("data");

                // Initialize variables
                String[] name = new String[rune.length()];
                String curr;
                final String[][] runesArray = new String[rune.length()][27];
                ArrayList<String> runePageNames = new ArrayList<String>();

                // Loop through pages, page names stored in string array
                for(int i = 0; i < rune.length(); i++) {
                    JSONObject c = rune.getJSONObject(i);
                    JSONArray slots = c.getJSONArray(TAG_SLOTS);
                    name[i] = c.getString(TAG_NAME);

                    for(int x = 0; x<27; x++) {
                        JSONObject s = slots.getJSONObject(x);
                        Log.d("Rune Slots", s + "");
                        String runeId = s.getString(TAG_RUNEID);
                        JSONObject singleRune = dataObject.getJSONObject(runeId);
                        try {
                            runesArray[i][x] = singleRune.getString("name");
                        }
                        catch (JSONException e) {
                            runesArray[i][x] = "No rune equipped";
                        }
                    }

                    curr = c.getString(TAG_CURRENT);

                    if(curr.equals("true"))
                       name[i] = name[i] + " [Active]";
                    runePageNames.add(name[i]);
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

