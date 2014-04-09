package edu.appdesign.leaguestats;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {

    private Spinner spinner;
    public static String region;
    public static String name;

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
                enteredText.setBackgroundColor(0);
                enteredText.setInputType(InputType.TYPE_CLASS_TEXT);

                name = enteredText.getText().toString();

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


