package edu.appdesign.leaguestats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
public class MatchHistoryActivity extends BaseActivity {

    TextView textType;
    ListView list;
    HistoryAdapter adapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);
        list = (ListView) findViewById(R.id.list);
        getActionBar().setTitle("Recent Games");

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
        String TAG_TYPE = "subType";
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

                final String[] type = new String[games.length()];
                final String[] champId = new String[games.length()];
                String[] kills = new String[games.length()];
                String[] deaths = new String[games.length()];
                String[] assists = new String[games.length()];
                final String[] score = new String[games.length()];
                final String[] win = new String[games.length()];
                String[] cs = new String[games.length()];
                History[] historyData = new History[games.length()];

                for(int i = 0; i < games.length(); i++) {
                    JSONObject c = games.getJSONObject(i);
                    JSONObject gameStats = games.getJSONObject(i).getJSONObject(TAG_STATS);
                    type[i] = c.getString(TAG_TYPE);
                    champId[i] = c.getString("championId");
                    cs[i] = gameStats.getString("minionsKilled");
                    kills[i] = gameStats.getString("championsKilled");
                    deaths[i] = gameStats.getString("numDeaths");
                    assists[i] = gameStats.getString("assists");
                    win[i] = gameStats.getString("win");

                    if(win[i].equals("true"))
                        win[i] = "Victory";
                    else
                        win[i] = "Defeat";

                    if(type[i].equals("RANKED_SOLO_5x5"))
                        type[i] = "Ranked (Solo)";

                    if(type[i].equals("CAP_5x5"))
                        type[i] = "TeamBuilder";

                    if(type[i].equals("NORMAL"))
                        type[i] = "Unranked";

                    score[i] = kills[i] +"/" + deaths[i] + "/" + assists[i];

                    historyData[i] = new History(score[i], champId[i], R.drawable.ic_launcher); // Placeholder image

                }

                adapter = new HistoryAdapter(MatchHistoryActivity.this,
                        R.layout.list_adapter,
                        historyData);

                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position,
                                            long id) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MatchHistoryActivity.this);
                        builder.setPositiveButton("More Info", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final Intent intent = new Intent(MatchHistoryActivity.this, GameInfo.class);
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                        builder.setMessage(champId[position] + "\n" + score[position] + "\n" + win[position]);
                        builder.setTitle(type[position]);

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

