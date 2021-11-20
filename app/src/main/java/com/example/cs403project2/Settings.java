package com.example.cs403project2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

public class Settings extends AppCompatActivity {
    Spinner spnOvercast, spnBright, spnDark, spnAmbient, spnClear, spnRainy, spnSnowy;
    Switch switchEnviron;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //getting all of the xml elements
        spnBright = findViewById(R.id.spnBright);
        spnAmbient = findViewById(R.id.spnAmbient);
        spnDark = findViewById(R.id.spnDark);
        spnClear = findViewById(R.id.spnClear);
        spnOvercast = findViewById(R.id.spnOvercast);
        spnRainy = findViewById(R.id.spnRainy);
        spnSnowy = findViewById(R.id.spnSnowy);
        switchEnviron = findViewById(R.id.switchEnviron);

        //stories manager object is to organize all of the stories, categories, etc. into one class
        StoriesManager storiesManager = new StoriesManager();

        //fill out the dropdown categories for spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, storiesManager.getStoryCategories());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBright.setAdapter(adapter);
        spnDark.setAdapter(adapter);
        spnClear.setAdapter(adapter);
        spnOvercast.setAdapter(adapter);
        spnRainy.setAdapter(adapter);
        spnSnowy.setAdapter(adapter);
        spnAmbient.setAdapter(adapter);

        //use shared preferences if available
        pref = getSharedPreferences("StoriesSP", MODE_PRIVATE);
        //for that, get the positions of the categories we will set
        int bright = adapter.getPosition(pref.getString("bright", "Adventure"));
        //set default values to the spinners,
        spnBright.setSelection(bright);
        int dark = adapter.getPosition(pref.getString("dark", "Horror"));
        spnDark.setSelection(dark);
        int clear = adapter.getPosition(pref.getString("clear", "Adventure"));
        spnClear.setSelection(clear);
        int overcast = adapter.getPosition(pref.getString("overcast", "Science Fiction"));
        spnOvercast.setSelection(overcast);
        int rainy = adapter.getPosition(pref.getString("rainy", "Poetry"));
        spnRainy.setSelection(rainy);
        int snowy = adapter.getPosition(pref.getString("snowy", "Poetry"));
        spnSnowy.setSelection(snowy);
        int ambient = adapter.getPosition(pref.getString("ambient", "Science Fiction"));
        spnAmbient.setSelection(ambient);
        //and to the switch
        boolean isLight = pref.getBoolean("environ", true);
        switchEnviron.setChecked(isLight);
        //adding this because the onclicklistener will not get the case of the switch being set to weather
        //from the sharedPref
        if (!switchEnviron.isChecked())
            switchEnviron.setText("Weather");

        switchEnviron.setOnClickListener(l -> {
            if (switchEnviron.isChecked())
                switchEnviron.setText("Light");
            else
                switchEnviron.setText("Weather");
        });
    }

    public void save(View view) {
        //save all of the selections in shared preferences
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString("bright", spnBright.getSelectedItem().toString());
        prefEditor.putString("dark", spnDark.getSelectedItem().toString());
        prefEditor.putString("clear", spnClear.getSelectedItem().toString());
        prefEditor.putString("overcast", spnOvercast.getSelectedItem().toString());
        prefEditor.putString("rainy", spnRainy.getSelectedItem().toString());
        prefEditor.putString("snowy", spnSnowy.getSelectedItem().toString());
        prefEditor.putString("ambient", spnAmbient.getSelectedItem().toString());
        prefEditor.putBoolean("environ", switchEnviron.isChecked());
        prefEditor.commit();
        //return to main
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void back(View view) {
        finish();
    }
}