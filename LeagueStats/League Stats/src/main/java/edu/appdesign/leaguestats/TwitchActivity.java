package edu.appdesign.leaguestats;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nate on 4/7/14.
 */
public class TwitchActivity extends BaseActivity {

    ArrayAdapter<String> adapter;

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
                Log.i("..........", url);
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            JSONParser jParser = new JSONParser();

            // Get JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
            Log.i("JSON from URL", "" + json);
            return json;
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
                ArrayList<String> streamers = new ArrayList<String>();

                for(int i = 0; i < array.length(); i++) {
                    JSONObject channelObject = array.getJSONObject(i).getJSONObject("channel");
                    name[i] = channelObject.getString("display_name");
                    status[i] = channelObject.getString("status");
                    url[i] = channelObject.getString("url");
                    streamers.add(name[i]);
                }

                adapter = new ArrayAdapter(TwitchActivity.this, R.layout.twitch_list, streamers);

                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position,
                                            long id) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(TwitchActivity.this);
                        builder.setPositiveButton("Go to Stream", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Open up twitch stream
                                final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url[position]));
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                        builder.setMessage(status[position]);
                        builder.setTitle(name[position]);

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


