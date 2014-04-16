package edu.appdesign.leaguestats;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Nate on 4/9/2014.
 */
public class GetStaticData {

    String url;
    private static String api_key="d96236d2-6ee3-4cfd-afa7-f41bdbc11128";
    JSONParser jsonParser = new JSONParser();
    static int i = 0;

    public void getRuneInfo(String pageName) {


    }

    public void getSummary(String summId) throws JSONException {

        // To go on StatsActivity

        url = "https://prod.api.pvp.net/api/lol/na/v1.3/stats/by-summoner/" + summId + "/summary?season=SEASON4&api_key=" + api_key;
        JSONObject json = jsonParser.getJSONFromUrl(url);
        JSONArray array = json.getJSONArray("playerStatSummaries");
        JSONObject rankedStats = array.getJSONObject(4);
        JSONObject normalStats = array.getJSONObject(8);
        String rankedWins = rankedStats.getString("wins");
        String rankedLosses = rankedStats.getString("losses");
        String normalWins = normalStats.getString("wins");

    }

    public static class GetChampionName extends AsyncTask <String, Void, Void> {

        public static String[] champName;
        protected Void doInBackground(String[] champId) {
            Log.i("Inside Class", "" + champId[0]);
            String url = "https://prod.api.pvp.net/api/lol/static-data/na/v1.2/champion/" + champId[0] + "?api_key=" + api_key;
            Log.i("url", url);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = jsonParser.getJSONFromUrl(url);
            try {
                champName[i] = jsonObject.getString("name");
                i++;
                Log.i("Champ Name", champName[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
