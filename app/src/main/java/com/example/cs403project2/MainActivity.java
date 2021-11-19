/**
 * Author: Andrew Loesel(aaloesel@svsu.edu), Adriana Kubicz, Kristina Mullen, Beata Hejno, Alexander Buckstiegel
 * Professor: Dr. Avishek Mukherjee
 * Class: CS 403, Mobile App Developement
 * University: Saginaw Valley State University
 * ------------------------------------------
 * This Application will use a weather API and phones built in light sensor to display different
 * types of stories based on the gathered data.
 */
package com.example.cs403project2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    final static String TAG1 = "light"; //light debug tag
    final static String TAG2 = "weather";

    boolean environmentType; //true is to retrieve a story based on light levels, false for weather
    String category; //category of the story that's going to be displayed in StoryActivity

    LightSensorObject lightSensorObject; //our sensor event listener object
    String light_type; //the amount of light read by the light sensor

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
        environmentType = pref.getBoolean("environ",false);

        if(environmentType){
            /*
            light type options:
            dark
            ambient
            bright
             */
            checkPermissions();
            lightSensorObject = new LightSensorObject();
            light_type = lightSensorObject.getLight_Type();
            category = pref.getString(light_type, "dark");
        }else {
            /*
            weather type options:
            clear
            overcast
            rainy
            snowy
             */
            getWeatherByLocation();
            category = pref.getString(weather,"clear");
        }

    }


    public void launchSettings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void launchStory(View view) {
        Intent intent = new Intent(this, StoryActivity.class);
        /*
        category types that can be retrieved:
        horror
        adventure
        poetry
        science fiction
         */
        intent.putExtra("category",category);
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
                Log.d(TAG2,weather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d(TAG2,"Failed to get weather");
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
                weather = "overcast";
                break;
            case 5:
            case 6:
            case 8:
            case 9:
                weather = "rainy";
                break;
            case 7:
                weather = "snowy";
                break;
            default:
                weather = "clear";
        }
    }

    private void getWeatherByLocation() {
        //GPS Stuff

        //Check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //If no, request the permission fro the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            //otherwise just note that permission was already granted
            Log.d("gps", "getLocation: permissions granted");
        }

        //Gets last location from phone - exact isn't really necessary
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
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
      
       /**
     * this method asks the user for permission to use the sensors
     */
    public void checkPermissions(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) ==
                PackageManager.PERMISSION_DENIED){
//ask for permission
            requestPermissions(new
                    String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
        }
    }


//    /**
//     * this method is called from the onSensorChanged event
//     * it creates a string to set the textView from containing the light value, and
//     * percentage out of the MAX_LIGHT_VALUE. This method also sets the progress bar
//     */
//    public void setLightUIElements(){
//        int light = lightSensorObject.getLight();
//        String strLight = "";
//        int pctLight; //percentage of light / MAX_VALUE
//
//        strLight = "Light: " + ((int) light);
//        pctLight = (int)((light/ MAX_LIGHT_VALUE) * 100); //change light to a percentage
//        //this condition sets the pctg to 1 if light is greater than 0 and the light value is less
//        //than a hundredth of the max value (for aesthetic purposes)
//        if (pctLight == 0 && light > 0)
//            pctLight = 1;
//        strLight = strLight + ", " + pctLight + "%";
//        strLight = strLight + ", " + light_Type;
//        txtLight.setText(strLight);
//    }

    /**
     * the onStart method is overridden to register a listener for the light sensor
     */
    @Override
    protected void onStart(){
        super.onStart();
        lightSensorObject.sensorManager.registerListener(lightSensorObject,
                lightSensorObject.sensor_light,SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * the onStop method is overridden to unregister our sensor listener
     */
    @Override
    protected void onStop(){
        super.onStop();
        lightSensorObject.sensorManager.unregisterListener(lightSensorObject);
    }

    class LightSensorObject implements SensorEventListener {
        SensorManager sensorManager;
        Sensor sensor_light;
        float light; //simply holds the reported reading from the light sensor
        int MAX_LIGHT_VALUE = 500; //cap for actionable range of light value (0-this val)
        String light_Type;

        public LightSensorObject() {
            sensorInit();
        }

        /**
         * this is a simple method which gives handles to the sensor manager and the devices
         * light sensor.
         */
        public void sensorInit() {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensor_light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        /**
         * override method for SensorEventListener
         * in this method we can grab the light value from the light sensor, and then
         * call a method to change our ui based on that value
         *
         * @param event
         */
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                light = event.values[0];

                if (light > MAX_LIGHT_VALUE)
                    light = MAX_LIGHT_VALUE;
                setLightType();

            }
        }

        /**
         * this method gets the ratio of light to max_light_value and sets the light_Type
         * to a corresponding string.
         */
        public void setLightType() {
            double lightRatio = light / MAX_LIGHT_VALUE;
            if (lightRatio < 0.1)
                light_Type = "dark";
            else if (lightRatio < 0.25)
                light_Type = "ambient";
            else
                light_Type = "bright";
        }

        /**
         * override method for SensorEventListener ... we do not care about this for our application
         *
         * @param sensor
         * @param accuracy
         */
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        /**
         * getter methods for private instance variables
         */
        public int getLight() {
            return (int) light;
        }

        public int getMAX_LIGHT_VALUE() {
            return MAX_LIGHT_VALUE;
        }

        public String getLight_Type() {
            return light_Type;
        }
    }
}