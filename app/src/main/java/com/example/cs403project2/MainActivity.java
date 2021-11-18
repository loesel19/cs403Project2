package com.example.cs403project2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    String category; //category of the story that's going to be displayed in StoryActivity

    final static String TAG = "weather";
    private double lat;
    private double lon;
    private String weather;

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


        RequestSingleton.getInstance(this).addToRequestQueue(requestObj(43.62,-83.92));
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