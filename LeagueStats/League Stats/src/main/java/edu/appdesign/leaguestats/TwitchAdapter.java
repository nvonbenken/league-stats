package edu.appdesign.leaguestats;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Nate on 4/15/2014.
 */
public class TwitchAdapter extends ArrayAdapter<Twitch> {

    Context context;
    int layoutResId;
    Twitch data[] = null;

    public TwitchAdapter(Context context, int layoutResId, Twitch[] data) {
        super(context, layoutResId, data);
        this.layoutResId = layoutResId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TwitchHolder holder;

        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(layoutResId, parent, false);

            holder = new TwitchHolder();
            holder.textStreamer = (TextView)convertView.findViewById(R.id.streamer);
            holder.textDescription = (TextView)convertView.findViewById(R.id.description);

            convertView.setTag(holder);
        }
        else
        {
            holder = (TwitchHolder)convertView.getTag();
        }

        Twitch twitch = data[position];
        holder.textDescription.setText(twitch.description);
        holder.textStreamer.setText(twitch.streamer);


        return convertView;
    }

    static class TwitchHolder
    {
        TextView textStreamer;
        TextView textDescription;
    }
}


