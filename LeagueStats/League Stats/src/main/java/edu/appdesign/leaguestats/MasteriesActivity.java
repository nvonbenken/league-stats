package edu.appdesign.leaguestats;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Nate on 4/7/14.
 */
public class MasteriesActivity extends Activity {

    private Spinner spinner;
    public static String page;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.masteries_activity);
        addListenerOnSpinnerSelection();
    }

    public void addListenerOnSpinnerSelection() {
        spinner = (Spinner) findViewById(R.id.mastery_selector);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(adapterView.getContext(),
                        "Page Selected: " + adapterView.getItemAtPosition(i).toString(),
                        Toast.LENGTH_SHORT).show();
                page = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
