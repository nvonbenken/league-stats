package edu.appdesign.leaguestats;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Nate on 4/15/2014.
 */
public class HistoryAdapter extends ArrayAdapter<History> {

    Context context;
    int layoutResId;
    History data[] = null;

    public HistoryAdapter(Context context, int layoutResId, History[] data) {
        super(context, layoutResId, data);
        this.layoutResId = layoutResId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryHolder holder;

        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(layoutResId, parent, false);

            holder = new HistoryHolder();
            holder.imageIcon = (ImageView)convertView.findViewById(R.id.icon);
            holder.textTitle = (TextView)convertView.findViewById(R.id.gameType);
            holder.textScore = (TextView)convertView.findViewById(R.id.score);

            convertView.setTag(holder);
        }
        else
        {
            holder = (HistoryHolder)convertView.getTag();
        }

        History history = data[position];
        holder.textScore.setText(history.score);
        holder.textTitle.setText(history.gametype);
        Picasso.with(this.context).load(history.icon).into(holder.imageIcon);


        return convertView;
    }

    static class HistoryHolder
    {
        ImageView imageIcon;
        TextView textTitle;
        TextView textScore;
    }
}


