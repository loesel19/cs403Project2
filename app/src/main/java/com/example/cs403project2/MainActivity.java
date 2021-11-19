package com.example.cs403project2;


import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    String category; //category of the story that's going to be displayed in StoryActivity

    final static String TAG = "weather";
    private double lat;
    private double lon;
    private String weather;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //stories manager object is to organize all of the stories, categories, etc. into one class
        StoriesManager storiesManager = new StoriesManager();

        //use shared preferences to get the category of stories that the user needs right now
        SharedPreferences pref = getSharedPreferences("StoriesSP", MODE_PRIVATE);
        //for example, it is bright outside
        category = pref.getString("bright", "dark");


        //GPS Stuff

        //Check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //If no, request the permission fro the user
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        else {
            //otherwise just note that permission was already granted
            Log.d("gps", "getLocation: permissions granted");}

        //Gets last location from phone - exact isn't really necessary
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            //Set lat and lon values to the location lat and long
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            Log.d("gps", "Latitude: " + lat + " Longitude: " + lon);
                            //Add to api queue to get the weather from these coordinates
                            RequestSingleton.getInstance(getApplicationContext()).addToRequestQueue(requestObj(lat, lon));
                        }
                    }
                });
    }


    public void launchSettings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void launchStory(View view) {
        Intent intent = new Intent(this, StoryActivity.class);
        startActivity(intent);
    }

    public void shareApp(View view){
        //this creates an implicit intent to share this app via a sms
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", "", null));
        intent.putExtra("sms_body", "Check out Transcribed Vibes! The story for today is " + category + "!");
        startActivity(intent);
    }

    private JsonObjectRequest requestObj(double lat, double lon){

        String url =
                "https://api.open-meteo.com/v1/forecast?" +
                "latitude="+lat+"&longitude="+lon+
                "&current_weather=true" +
                "&temperature_unit=fahrenheit" +
                "&windspeed_unit=mph" +
                "&precipitation_unit=inch";

        JsonObjectRequest r = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject obj = response;
                int weatherID = obj.getJSONObject("current_weather").getInt("weathercode");
                setWeather(weatherID);
                Log.d(TAG,weather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d(TAG,"Failed to get weather");
        });

        return r;
    }

    private void setWeather(int id){
        int adjustedID;
        if(id < 10){
            adjustedID = id;
        }else if(id == 85 || id == 86){
            adjustedID = 7;
        }else {
            adjustedID = id/10;
        }
        switch(adjustedID){
            case 3:
            case 4:
                weather = "Overcast";
                break;
            case 5:
            case 6:
            case 8:
            case 9:
                weather = "Rainy";
                break;
            case 7:
                weather = "Snowy";
                break;
            default:
                weather = "Clear";
        }
    }
}