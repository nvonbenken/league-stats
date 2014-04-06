package edu.appdesign.leaguestats;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class StatsActivity extends ListActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_activity);

        GetStats stats = new GetStats();
        stats.execute();
    }

class GetStats extends AsyncTask<Void, Void, Void> {

    private String api_key="d96236d2-6ee3-4cfd-afa7-f41bdbc11128";
    String region = MainActivity.region;
    String name = MainActivity.name;
    String url = "https://prod.api.pvp.net/api/lol/" + region + "/v1.4/summoner/by-name/" + name + "?api_key=<" + api_key + ">";
    String encodedUrl = null;
    TextView textId = (TextView) findViewById(R.id.id);
    TextView textName = (TextView) findViewById(R.id.name);
    TextView textProfileIconId = (TextView) findViewById(R.id.profileIconId);
    TextView textRevisionDate = (TextView) findViewById(R.id.revisionDate);
    TextView textSummonerLevel = (TextView) findViewById(R.id.summonerLevel);

    long jId;
    String jName;
    int jProfileIconId;
    long jRevisionDate;
    long jSummonerLevel;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            encodedUrl = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected Void doInBackground(Void... arg0) {
        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.getJSONFromUrl(encodedUrl);

        try {

            jId = json.getLong("id");
            jName = json.getString("dataVersion");
            jProfileIconId = json.getInt("profileIconId");
            jRevisionDate = json.getLong("revisionDate");
            jSummonerLevel = json.getLong("summonerLevel");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        /**
         * Put json data on screen
         * */

        String id = String.valueOf(jId);
        String name = String.valueOf(jName);
        String icon = String.valueOf(jProfileIconId);
        String rev = String.valueOf(jRevisionDate);
        String summLevel = String.valueOf(jSummonerLevel);

        textSummonerLevel.setText(id);
        textSummonerLevel.setText(name);
        textSummonerLevel.setText(icon);
        textSummonerLevel.setText(rev);
        textSummonerLevel.setText(summLevel);

        }
    }
}

