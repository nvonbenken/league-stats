package edu.appdesign.leaguestats;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Nate on 4/15/2014.
 */
public class GameInfo extends BaseActivity {

    TextView p1Name;
    TextView p2Name;
    TextView p3Name;
    TextView p4Name;
    TextView p5Name;
    TextView p6Name;
    TextView p7Name;
    TextView p8Name;
    TextView p9Name;
    TextView p10Name;

    ImageView icon1;
    ImageView icon2;
    ImageView icon3;
    ImageView icon4;
    ImageView icon5;
    ImageView icon6;
    ImageView iconTrinket;

    LinearLayout team1;
    LinearLayout team2;

    String url;
    String url2;
    String region = MainActivity.region.toLowerCase();
    String id = StatsActivity.sumID;
    String api_key = "d96236d2-6ee3-4cfd-afa7-f41bdbc11128";

    public String players[] = new String[9];
    public String win;

    public String item1;
    public String item2;
    public String item3;
    public String item4;
    public String item5;
    public String item6;
    public String trinket;

    public int value;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getInt("gameNumber");
        }

        GetGameInfo getGameInfo = new GetGameInfo();
        getGameInfo.execute();
    }

    class GetGameInfo extends AsyncTask<String, String, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Assign views
            p1Name = (TextView) findViewById(R.id.p1Name);
            p2Name = (TextView) findViewById(R.id.p2Name);
            p3Name = (TextView) findViewById(R.id.p3Name);
            p4Name = (TextView) findViewById(R.id.p4Name);
            p5Name = (TextView) findViewById(R.id.p5Name);
            p6Name = (TextView) findViewById(R.id.p6Name);
            p7Name = (TextView) findViewById(R.id.p7Name);
            p8Name = (TextView) findViewById(R.id.p8Name);
            p9Name = (TextView) findViewById(R.id.p9Name);
            p10Name = (TextView) findViewById(R.id.p10Name);

            icon1 = (ImageView)findViewById(R.id.item1);
            icon2 = (ImageView) findViewById(R.id.item2);
            icon3 = (ImageView)findViewById(R.id.item3);
            icon4 = (ImageView) findViewById(R.id.item4);
            icon5 = (ImageView)findViewById(R.id.item5);
            icon6 = (ImageView) findViewById(R.id.item6);
            iconTrinket = (ImageView)findViewById(R.id.trinket);

            team1 = (LinearLayout) findViewById(R.id.team1);
            team2 = (LinearLayout) findViewById(R.id.team2);

            url = "http://prod.api.pvp.net/api/lol/" + region + "/v1.3/game/by-summoner/" + id + "/recent?api_key=" + api_key;

        }

        @Override
        protected String[] doInBackground(String... arg0) {
            JSONParser jParser = new JSONParser();

            // Get JSON from URL
            JSONObject obj = jParser.getJSONFromUrl(url);

            // Get JSON Object
            try {
                JSONArray games = obj.getJSONArray("games");
                Log.d("Games", games + "");

                JSONObject singleGame = games.getJSONObject(value);
                Log.d("Single Game", singleGame + "");
                JSONArray fellowPlayers = singleGame.getJSONArray("fellowPlayers");
                Log.d("Fellow Players", fellowPlayers + "");
                JSONObject gameStats = games.getJSONObject(value).getJSONObject("stats");
                win = gameStats.getString("win");

                item1 = gameStats.getString("item0");
                item2 = gameStats.getString("item1");
                item3 = gameStats.getString("item2");
                item4 = gameStats.getString("item3");
                item5 = gameStats.getString("item4");
                item6 = gameStats.getString("item5");
                trinket = gameStats.getString("item6");


                Log.d("Win", win);


                for(int j = 0; j < fellowPlayers.length(); j++) {
                    JSONObject c = fellowPlayers.getJSONObject(j);
                    Log.d("Summoner Ids", c.getString("summonerId"));
                    url2 = "https://prod.api.pvp.net/api/lol/" + region + "/v1.4/summoner/" + c.getString("summonerId") + "?api_key=" + api_key;
                    Log.d("Summoner Urls", url2);
                    JSONObject summoner = jParser.getJSONFromUrl(url2);
                    Log.d("Summoner JSON", summoner + "");
                    JSONObject summonerName = summoner.getJSONObject(c.getString("summonerId"));
                    Log.d("Summoner Name JSON", summonerName + "");
                    players[j] = summonerName.getString("name");
                    Log.d("Players", players[j]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return players;
        }

        @Override
        protected void onPostExecute(String[] players) {

            // Team 1
            p1Name.setText(MainActivity.name);
            p2Name.setText(players[0]);
            p3Name.setText(players[1]);
            p4Name.setText(players[2]);
            p5Name.setText(players[3]);
            // Team 2
            p6Name.setText(players[4]);
            p7Name.setText(players[5]);
            p8Name.setText(players[6]);
            p9Name.setText(players[7]);
            p10Name.setText(players[8]);

            icon1.setImageResource(getResources().getIdentifier("item1", "drawable", getPackageName()));

            if(win.equals("true")) {
                team1.setBackgroundColor(Color.parseColor("#E694ff94"));
                team2.setBackgroundColor(Color.parseColor("#E6ff8080"));
            }

            else {
                team1.setBackgroundColor(Color.parseColor("#E6ff8080"));
                team2.setBackgroundColor(Color.parseColor("#E694ff94"));
            }


        }
    }
}


