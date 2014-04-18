package edu.appdesign.leaguestats;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nate on 4/7/14.
 */
public class TwitchActivity extends BaseActivity {

    TwitchAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitch_activity);
        getActionBar().setTitle("Currently Streaming");
        GetStreams getStreams = new GetStreams();
        getStreams.execute();
    }

    class GetStreams extends AsyncTask<String, String, JSONObject> {

        String url = null;
        ListView list = (ListView) findViewById(R.id.list);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                url = "https://api.twitch.tv/kraken/streams?game=League%20of%20Legends";
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            JSONParser jParser = new JSONParser();
            // Get JSON from URL
            return jParser.getJSONFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                // Get JSON Array
                JSONArray array = json.getJSONArray("streams");

                // Loop through pages, page names stored in string array
                final String[] name = new String[array.length()];
                final String[] status = new String[array.length()];
                final String[] url = new String[array.length()];
                Twitch[] twitchData = new Twitch[array.length()];

                for(int i = 0; i < array.length(); i++) {
                    JSONObject channelObject = array.getJSONObject(i).getJSONObject("channel");
                    name[i] = channelObject.getString("display_name");
                    status[i] = channelObject.getString("status");
                    url[i] = channelObject.getString("url");
                    twitchData[i] = new Twitch(name[i], status[i]);
                }

                adapter = new TwitchAdapter(TwitchActivity.this, R.layout.twitch_list_item, twitchData);

                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position,
                                            long id) {
                                // Open up twitch stream
                                final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url[position]));
                                startActivity(intent);
                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


