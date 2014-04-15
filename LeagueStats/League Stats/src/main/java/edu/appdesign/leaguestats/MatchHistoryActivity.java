package edu.appdesign.leaguestats;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Nate on 4/7/2014.
 */
public class MatchHistoryActivity extends Activity {

    TextView textType;
    ListView list;
    ArrayAdapter<String> adapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);
        list = (ListView) findViewById(R.id.list);

        GetMatchHistory getMatchHistory = new GetMatchHistory();
        getMatchHistory.execute();
    }

    class GetMatchHistory extends AsyncTask<String, String, JSONObject> {

        private String api_key = "d96236d2-6ee3-4cfd-afa7-f41bdbc11128";
        String region = MainActivity.region.toLowerCase();
        String id = StatsActivity.sumID;
        String encodedKey = null;
        String encodedRegion = null;
        String encodedId = null;
        String url = null;


        // JSON Node Names
        String TAG_MODE = "gameMode";
        String TAG_TYPE = "gameType";
        String TAG_STATS = "stats";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {

                // Assign views
                textType = (TextView) findViewById(R.id.gameType);


                // Encode URL variables
                encodedId = URLEncoder.encode(id, "UTF-8");
                encodedKey = URLEncoder.encode(api_key, "UTF-8");
                encodedRegion = URLEncoder.encode(region, "UTF-8");

                url = "http://prod.api.pvp.net/api/lol/" + region + "/v1.3/game/by-summoner/" + id + "/recent?api_key=" + api_key;
                Log.i("...........", url);
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
                JSONArray games = json.getJSONArray("games");
                Log.i("test", "" + games);
                String[] type = new String[games.length()];
                ArrayList<String> recentGameTypes = new ArrayList<String>();
                for(int i = 0; i < games.length(); i++) {
                    JSONObject c = games.getJSONObject(i);
                    type[i] = c.getString("subType");
                    Log.i("test", type[i]);

                    if(type[i].equals("RANKED_SOLO_5x5"))
                        type[i] = "Ranked (Solo)";

                    recentGameTypes.add(type[i]);
                }

                adapter = new ArrayAdapter(MatchHistoryActivity.this,
                        android.R.layout.simple_expandable_list_item_1,
                        recentGameTypes);

                list.setAdapter(adapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

