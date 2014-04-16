package edu.appdesign.leaguestats;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Nate on 4/9/2014.
 */
public class BaseActivity extends Activity
{
    public DrawerLayout drawerLayout;
    public ListView drawerList;
    public String[] layers;
    private ActionBarDrawerToggle drawerToggle;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBar actionBar = getActionBar();

        drawerToggle = new ActionBarDrawerToggle((Activity) this, drawerLayout, R.drawable.ic_drawer, 0, 0)
        {
            public void onDrawerClosed(View view)
            {
                actionBar.setTitle(R.string.app_name);
            }

            public void onDrawerOpened(View drawerView)
            {
                actionBar.setTitle(R.string.menu);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        layers = getResources().getStringArray(R.array.layers);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, android.R.id.text1,
                layers));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setContentView(final int layoutResID) {

        final Intent twitch = new Intent(this, TwitchActivity.class);
        final Intent community = new Intent(this, CommunityActivity.class);
        final Intent esports = new Intent(this, ESportsActivity.class);
        final Intent home = new Intent(this, MainActivity.class);

        drawerLayout= (DrawerLayout) getLayoutInflater().inflate(R.layout.drawer_layout, null);
        FrameLayout actContent = (FrameLayout) drawerLayout.findViewById(R.id.content_frame);

        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(drawerLayout);

        // load slide menu items
        layers = getResources().getStringArray(R.array.layers);

        drawerList = (ListView) drawerLayout.findViewById(R.id.left_drawer);

        ArrayList<String> navDrawerItems = new ArrayList<String>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new String(layers[0]));
        // Twitch
        navDrawerItems.add(new String(layers[1]));
        // Community
        navDrawerItems.add(new String(layers[2]));
        // Esports
        navDrawerItems.add(new String(layers[3]));

        // setting the nav drawer list adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, android.R.id.text1,
                layers);

        drawerList.setAdapter(adapter);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.menu // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle("League Stats");
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Menu");
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                String selected = arg0.getItemAtPosition(pos).toString();
                if(selected.equals("Twitch"))
                    startActivity(twitch);
                if(selected.equals("Community"))
                    startActivity(community);
                if(selected.equals("ESports"))
                    startActivity(esports);
                if(selected.equals("Home"))
                    startActivity(home);
            }
        });

    }
}
