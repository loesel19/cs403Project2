package com.example.cs403project2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "weather";
    private double lat;
    private double lon;
    private String weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestSingleton.getInstance(this).addToRequestQueue(requestObj(43.62,-83.92));
    }

    public void launchSettings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void launchStory(View view) {
        Intent intent = new Intent(this, Story.class);
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