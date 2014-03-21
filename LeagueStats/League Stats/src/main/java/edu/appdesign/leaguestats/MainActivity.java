package edu.appdesign.leaguestats;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {

    private ProgressDialog dialog;
    private Spinner spinner;
    private String api_key="d96236d2-6ee3-4cfd-afa7-f41bdbc11128";
    public static String region;
    private static String TAG_STATS;
    private static String TAG_ID;
    private static String TAG_NAME;
    private static String TAG_PROFILEICONID;
    private static String TAG_REVISIONDATE;
    private static String TAG_SUMMONERLEVEL;
    public static String name;

    JSONArray stats = null;

    ArrayList<HashMap<String, String>> statList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        addListenerOnSpinnerSelection();

        final Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText enteredText = (EditText)findViewById(R.id.name);
                name = enteredText.toString();

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                Intent intent = new Intent(MainActivity.this, StatsActivity.class);

                startActivity(intent);
            }
        });
    }

    public void addListenerOnSpinnerSelection() {
        spinner = (Spinner) findViewById(R.id.region_selector);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(adapterView.getContext(),
                        "Region Selected: " + adapterView.getItemAtPosition(i).toString(),
                        Toast.LENGTH_SHORT).show();
                region = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}


