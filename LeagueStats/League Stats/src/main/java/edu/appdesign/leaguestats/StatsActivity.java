package edu.appdesign.leaguestats;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class StatsActivity extends BaseActivity {

    TextView textName;
    TextView textSummonerLevel;
    ImageView imageView;
    TextView textRWins;
    TextView textRLoss;
    TextView textRLeague;
    TextView rankedStats;
    public static String sumID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_activity);

        final Button viewMasteries = (Button) findViewById(R.id.masteries);
        final Button viewRunes = (Button) findViewById(R.id.runes);
        final Button viewMatchHistory = (Button) findViewById(R.id.history);

        viewRunes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(StatsActivity.this, RunesActivity.class);
                startActivity(intent);
            }
        });

        viewMasteries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(StatsActivity.this, MasteriesActivity.class);
                startActivity(intent);
            }
        });

        viewMatchHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(StatsActivity.this, MatchHistoryActivity.class);
                startActivity(intent);
            }
        });

        GetStats stats = new GetStats();
        stats.execute();
    }

    class GetStats extends AsyncTask<String, String, JSONObject> {

        private String api_key="d96236d2-6ee3-4cfd-afa7-f41bdbc11128";
        String region = MainActivity.region.toLowerCase();
        String name = MainActivity.name.toLowerCase();
        String url = null;
        String encodedName = null;
        String encodedKey = null;
        String encodedRegion = null;

        // JSON Node Names
        String TAG_NAME = "name";
        String TAG_PROFILEICONID = "profileIconId";
        String TAG_SUMMONERLEVEL = "summonerLevel";
        String TAG_ID = "id";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {

                // Assign views
                textName = (TextView) findViewById(R.id.name);
                textSummonerLevel = (TextView) findViewById(R.id.summonerLevel);
                imageView = (ImageView) findViewById(R.id.icon);
                textRWins = (TextView) findViewById(R.id.rankedWins);
                textRLoss = (TextView) findViewById(R.id.rankedLosses);
                textRLeague = (TextView) findViewById(R.id.rankedLeague);
                rankedStats = (TextView) findViewById(R.id.rankedStats);

                // Encode URL variables
                encodedName = URLEncoder.encode(name, "UTF-8");
                encodedKey = URLEncoder.encode(api_key, "UTF-8");
                encodedRegion = URLEncoder.encode(region, "UTF-8");

                url = "https://prod.api.pvp.net/api/lol/" + encodedRegion + "/v1.4/summoner/by-name/" + encodedName + "?api_key=" + encodedKey;
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
            Log.i("Summoner JSON", "" + json);
            JSONObject jb = null;

            if(json == null)
                cancel(true);

            isCancelled();

            try {
                if (json != null) {
                    jb = json.getJSONObject(encodedName);
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
            return jb;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                // Storing JSON item to String
                String name = json.getString(TAG_NAME);
                String icon = json.getString(TAG_PROFILEICONID);
                String sumLevel = json.getString(TAG_SUMMONERLEVEL);
                sumID = json.getString(TAG_ID);

                // Putting JSON data in TextViews
                textName.setText(name);
                textSummonerLevel.setText("Level: " + sumLevel);

                // Fetch icon from profileIconId and display it
                String iconUrl = "http://ddragon.leagueoflegends.com/cdn/4.3.12/img/profileicon/" + icon +
                        ".png";

                // Use Picasso library for easy image handling/caching
                Picasso.with(imageView.getContext()).load(iconUrl).into(imageView);
                if(sumLevel.equals("30")) {

                    GetStaticData.Summary summary = new GetStaticData.Summary();
                    summary = GetStaticData.getSummary(sumID);

                    textRLeague.setText(summary.rLeague + ": " + summary.rTier + " " + summary.rRank + ", " + summary.rLeaguePoints + "LP");

                    textRWins.setText("Wins: " + summary.rWins);
                    textRLoss.setText("Losses: " + summary.rLoss);
                }
                else
                    rankedStats.setText("No Ranked Games Played");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled () {
            AlertDialog.Builder builder = new AlertDialog.Builder(StatsActivity.this);
            builder.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    final Intent intent = new Intent(StatsActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            builder.setMessage("No summoner by that name found!");
            builder.setTitle("Error");

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }
}

