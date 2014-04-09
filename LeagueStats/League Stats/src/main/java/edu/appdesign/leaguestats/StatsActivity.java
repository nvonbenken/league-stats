package edu.appdesign.leaguestats;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

public class StatsActivity extends Activity {

    TextView textName;
    TextView textSummonerLevel;
    ImageView imageView;
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

                // Lookup Runes for Summoner ID retrieved by JSON lookup
                // Code here

                Intent intent = new Intent(StatsActivity.this, RunesActivity.class);
                startActivity(intent);
            }
        });

        viewMasteries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Lookup Masteries for Summoner ID retrieved by JSON lookup
                // Code here

                Intent intent = new Intent(StatsActivity.this, MasteriesActivity.class);
                startActivity(intent);
            }
        });

        viewMatchHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Look up match history for Summoner ID retrieved by JSON lookup
                // Code here

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
        String name = MainActivity.name;
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
            Log.i("............", "" + json);
            JSONObject jb = null;

            try {
                jb = json.getJSONObject(encodedName);

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


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

