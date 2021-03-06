package edu.appdesign.leaguestats;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

/**
 * Created by Nate on 4/7/2014.
 */
public class MatchHistoryActivity extends BaseActivity {

    TextView textType;
    ListView list;
    HistoryAdapter adapter;
    Context context;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);
        list = (ListView) findViewById(R.id.list);
        getActionBar().setTitle("Recent Games");

        new GetMatchHistory(MatchHistoryActivity.this).execute();
    }

    class GetMatchHistory extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog dialog;
        private Activity activity;

        public GetMatchHistory(Activity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        }

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

                this.dialog.setMessage("Loading Match History");
                this.dialog.show();

                // Assign views
                textType = (TextView) findViewById(R.id.gameType);


                // Encode URL variables
                encodedId = URLEncoder.encode(id, "UTF-8");
                encodedKey = URLEncoder.encode(api_key, "UTF-8");
                encodedRegion = URLEncoder.encode(region, "UTF-8");

                url = "http://prod.api.pvp.net/api/lol/" + region + "/v1.3/game/by-summoner/" + id + "/recent?api_key=" + api_key;
                Log.d("Test", url);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            JSONParser jParser = new JSONParser();

            // Get JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
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
                String[] iconUrl = new String[games.length()];
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

                    try {
                        kills[i] = gameStats.getString("championsKilled");
                    }
                    catch (JSONException e) {
                        kills[i] = "0";
                    }

                    try {
                        deaths[i] = gameStats.getString("numDeaths");
                    }
                    catch (JSONException e) {
                          deaths[i] = "0";
                    }
                    try {
                        assists[i] = gameStats.getString("assists");
                    }
                    catch (JSONException e) {
                        assists[i] = "0";
                    }

                    win[i] = gameStats.getString("win");

                    if(win[i].equals("true"))
                        win[i] = "Victory";

                    else
                        win[i] = "Defeat";

                    if(type[i].equals("RANKED_SOLO_5x5"))
                        type[i] = "Ranked";

                    if(type[i].equals("ODIN_UNRANKED"))
                        type[i] = "Dominion";

                    if(type[i].equals("CAP_5x5"))
                        type[i] = "TeamBuilder";

                    if(type[i].equals("NORMAL"))
                        type[i] = "Unranked";

                    if(type[i].equals("ARAM_UNRANKED_5x5"))
                        type[i] = "ARAM";

                    GetStaticData data = new GetStaticData();
                    champId[i] = data.getChampionName(champId[i]);

                    score[i] = kills[i] +"/" + deaths[i] + "/" + assists[i];

                    iconUrl[i] = "http://ddragon.leagueoflegends.com/cdn/4.5.4/img/champion/" + champId[i] + ".png";

                    historyData[i] = new History(score[i], type[i] + ": " + win[i], iconUrl[i]);
                }

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                adapter = new HistoryAdapter(MatchHistoryActivity.this,
                        R.layout.list_adapter,
                        historyData);

                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position,
                                            long id) {

                    Intent intent = new Intent(MatchHistoryActivity.this, GameInfo.class);
                    intent.putExtra("gameNumber", position);
                    startActivity(intent);

                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

