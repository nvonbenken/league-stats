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
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Nate on 4/7/14.
 */
public class MasteriesActivity extends BaseActivity {

    private Spinner spinner;
    ArrayAdapter<String> adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.masteries_activity);
        getActionBar().setTitle("Masteries");
        GetMasteries getMasteries = new GetMasteries();
        getMasteries.execute();
        spinner = (Spinner) findViewById(R.id.mastery_selector);
    }

    class GetMasteries extends AsyncTask<String, String, JSONObject> {

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


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                // Encode URL variables
                encodedId = URLEncoder.encode(id, "UTF-8");
                encodedKey = URLEncoder.encode(api_key, "UTF-8");
                encodedRegion = URLEncoder.encode(region, "UTF-8");

                url = "http://prod.api.pvp.net/api/lol/" + region + "/v1.4/summoner/" + id + "/masteries?api_key=" + api_key;
                url2 = "https://prod.api.pvp.net/api/lol/static-data/" + region + "/v1.2/mastery?api_key=" + api_key;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            JSONParser jParser = new JSONParser();

            // Get JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
            JSONObject masteriesList = jParser.getJSONFromUrl(url2);

            // Get JSON containing Rune Info and cache it
            try {
                ObjectOutput out = new ObjectOutputStream(new FileOutputStream(new File(getCacheDir(),"")+"cacheFile2.srl"));
                out.writeObject(masteriesList.toString());
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
                JSONObject masteries = json.getJSONObject(encodedId);

                // Get JSON Array node
                JSONArray mastery = masteries.getJSONArray("pages");

                // Load Saved file
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(new File(getCacheDir(),"")+"cacheFile2.srl")));
                String storedMasteryInfo = (String) in.readObject();
                JSONObject jsonObject = new JSONObject(storedMasteryInfo);
                JSONObject dataObject = jsonObject.getJSONObject("data");
                Log.i("Mastery data object", dataObject + "");

                // Loop through pages, page names stored in string array
                String[] name = new String[mastery.length()];
                String curr;
                ArrayList<String> masteryPageNames = new ArrayList<String>();
                final String[][] masteriesArray = new String[mastery.length()][17];



                for(int i = 0; i < mastery.length(); i++) {
                    JSONObject c = mastery.getJSONObject(i);
                    name[i] = c.getString(TAG_NAME);
                    curr = c.getString(TAG_CURRENT);

                    JSONArray masteryInfo = c.getJSONArray("masteries");

                    for(int x = 0; x< masteryInfo.length(); x++) {
                        JSONObject s = masteryInfo.getJSONObject(x);
                        String masteryId = s.getString("id");
                        JSONObject singleMastery = dataObject.getJSONObject(masteryId);
                        try {
                            masteriesArray[i][x] = singleMastery.getString("name");
                        }
                        catch (JSONException e) {
                            masteriesArray[i][x] = "";
                        }
                    }

                    if(curr.equals("true"))
                        name[i] = name[i] + " [Active]";
                    masteryPageNames.add(name[i]);

                }

                adapter = new ArrayAdapter(MasteriesActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        masteryPageNames);

                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#C49246"));
                        Toast.makeText(adapterView.getContext(),
                                "Page Selected: " + adapterView.getItemAtPosition(i).toString(),
                                Toast.LENGTH_SHORT).show();
                        TextView runes = (TextView) findViewById(R.id.masteries);
                        runes.setText("Masteries: " + "\n");

                        for(int j = 0; j<15; j++) {
                            runes.append(masteriesArray[i][j]);
                            runes.append("\n");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (OptionalDataException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

