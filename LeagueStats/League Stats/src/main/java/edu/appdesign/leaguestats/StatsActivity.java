package edu.appdesign.leaguestats;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nate on 3/19/14.
 */
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
    String url = "https://prod.api.pvp.net/api/lol/" + region + "/v1.3/summoner/by-name/" + name + "?api_key=<" + api_key + ">";
    String encodedUrl = null;
    JSONArray stats = null;
    private String TAG_STATS;
    private String TAG_ID;
    private String TAG_NAME;
    private String TAG_PROFILEICONID;
    private String TAG_REVISIONDATE;
    private String TAG_SUMMONERLEVEL;
    ArrayList<HashMap<String, String>> statList;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        //dialog = new ProgressDialog(StatsActivity.this);
        //dialog.setMessage("Please wait...");
        //dialog.setCancelable(false);
        // dialog.show();
        try {
            encodedUrl = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected Void doInBackground(Void... arg0) {
        // Creating service handler class instance
        ServiceHandler sh = new ServiceHandler();

        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(encodedUrl, ServiceHandler.GET);

        Log.d("Response: ", "> " + jsonStr);

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                stats = jsonObj.getJSONArray(TAG_STATS);

                // looping through All Contacts
                for (int i = 0; i < stats.length(); i++) {
                    JSONObject s = stats.getJSONObject(i);

                    String id = s.getString(TAG_ID);
                    String name = s.getString(TAG_NAME);
                    String profileIconId = s.getString(TAG_PROFILEICONID);
                    String revisionDate= s.getString(TAG_REVISIONDATE);
                    String summonerLevel = s.getString(TAG_SUMMONERLEVEL);


                    // tmp hashmap for single contact
                    HashMap<String, String> stat = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    stat.put(TAG_ID, id);
                    stat.put(TAG_NAME, name);
                    stat.put(TAG_PROFILEICONID, profileIconId);
                    stat.put(TAG_REVISIONDATE, revisionDate);
                    stat.put(TAG_SUMMONERLEVEL, summonerLevel);

                    // adding contact to contact list
                    statList.add(stat);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // Dismiss the progress dialog
        //if (dialog.isShowing())
        //    dialog.dismiss();
        /**
         * Updating parsed JSON data into ListView
         * */

        ListAdapter adapter = new SimpleAdapter(
                StatsActivity.this, statList,
                R.layout.stats_item, new String[] { TAG_ID, TAG_NAME,
                TAG_PROFILEICONID, TAG_REVISIONDATE, TAG_SUMMONERLEVEL }, new int[] { R.id.id,
                R.id.name, R.id.profileIconId, R.id.revisionDate, R.id.summonerLevel });

        setListAdapter(adapter);
        }
    }
}

