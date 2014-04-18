package edu.appdesign.leaguestats;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


/**
 * Created by Nate on 4/9/2014.
 */
public class GetStaticData {

    private static String api_key = "d96236d2-6ee3-4cfd-afa7-f41bdbc11128";
    public static String region = MainActivity.region.toLowerCase();

    public void getRuneInfo(String pageName) {


    }

    public String getChampionName(String champId) {
        String name = null;
        try {
            name = new GetChampionName()
                    .execute(champId)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return name;
    }

    public static Summary getSummary(String summId) throws JSONException {
        Summary summary = new Summary();
        try {
            summary = new GetSummary()
                    .execute(summId)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return summary;
    }

    public static class GetChampionName extends AsyncTask<String, String, String> {

        public static String champName;

        protected String doInBackground(String[] champId) {
            String url = "https://prod.api.pvp.net/api/lol/static-data/" + region + "/v1.2/champion/" + champId[0] + "?api_key=" + api_key;
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = jsonParser.getJSONFromUrl(url);
            Log.i("JSON", "" + jsonObject);
            try {
                champName = jsonObject.getString("key");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return champName;
        }
    }

    public static class GetSummary extends AsyncTask<String, String, Summary> {

        protected Summary doInBackground(String[] summId) {
            String url = "https://prod.api.pvp.net/api/lol/" + MainActivity.region.toLowerCase() + "/v1.3/stats/by-summoner/" + summId[0] + "/summary?season=SEASON4&api_key=" + api_key;
            Log.i("Summary URL", url);
            String url2 = "https://prod.api.pvp.net/api/lol/" + MainActivity.region.toLowerCase() + "/v2.3/league/by-summoner/" + summId[0] + "/entry?api_key=" + api_key;
            JSONParser jsonParser = new JSONParser();
            JSONArrayParser jsonArrayParser = new JSONArrayParser();
            JSONObject jsonObject = jsonParser.getJSONFromUrl(url);
            JSONArray jsonArray = jsonArrayParser.getJSONFromUrl(url2);
            Summary summary = new Summary();
            Log.i("Stats JSON", "" + jsonObject);
            JSONArray array;
            JSONObject league;
            try {
                array = jsonObject.getJSONArray("playerStatSummaries");
                league = jsonArray.getJSONObject(0);
                JSONObject rankedStats = array.getJSONObject(4);
                JSONObject normalStats = array.getJSONObject(8);
                summary.rWins = rankedStats.getString("wins");
                summary.rLoss = rankedStats.getString("losses");
                summary.nWins = normalStats.getString("wins");
                summary.rTier = league.getString("tier");
                summary.rLeague = league.getString("leagueName");
                summary.rRank = league.getString("rank");
                summary.rLeaguePoints = league.getString("leaguePoints");
                Log.i("Ranked", "" + summary.rWins + " " + summary.rLoss);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return summary;
        }
    }

    public static class Summary {
        public String rWins;
        public String rLoss;
        public String nWins;
        public String rTier;
        public String rLeague;
        public String rRank;
        public String rLeaguePoints;

        public Summary() {
            super();
        }

        public Summary(String rWins, String rLoss, String nWins, String rTier, String rLeague, String rRank, String rLeaguePoints) {
            super();
            this.rWins = rWins;
            this.rLoss = rLoss;
            this.nWins = nWins;
            this.rTier = rTier;
            this.rLeague = rLeague;
            this.rRank = rRank;
            this.rLeaguePoints = rLeaguePoints;
        }
    }
}
