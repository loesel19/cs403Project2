/**
 * Author: Andrew Loesel(aaloesel@svsu.edu), Adriana Kubicz, Kristina Mullen, Beata Hejno, Alexander Buckstiegel
 * Professor: Dr. Avishek Mukherjee
 * Class: CS 403, Mobile App Development
 * University: Saginaw Valley State University
 * ------------------------------------------
 * This Application will use a weather API and phones built in light sensor to display different
 * types of stories based on the gathered data.
 */
package com.example.cs403project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.content.ContextCompat;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    final static String TAG1 = "light"; //light debug tag
    final static String TAG2 = "weather";

    SharedPreferences pref;
    boolean environmentType; //true is to retrieve a story based on light levels, false for weather
    String category; //category of the story that's going to be displayed in StoryActivity
    String status;

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

        checkPermissions(); //check for permissions for location and light
        lightSensorObject = new LightSensorObject();
        getWeatherByLocation(); //get the weather

        //use shared preferences to get the category of stories that the user needs right now
        pref = getSharedPreferences("StoriesSP", MODE_PRIVATE);
        environmentType = pref.getBoolean("environ", true);
    }

    //go to the settings page
    public void launchSettings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    private void chooseCategory(){
        if (environmentType) {
            /*
            light type options:
            dark
            ambient
            bright
             */
            light_type = lightSensorObject.getLight_Type();
            Log.d(TAG1, lightSensorObject.getLight() + "");
            Log.d(TAG1, light_type + "");
            status = light_type;
        } else {
            /*
            weather type options:
            clear
            overcast
            rainy
            snowy
             */
            Log.d(TAG2, weather + "");
            status = weather;
        }
        category = pref.getString(status, "Random");
    }

    public void launchStory(View view) {
        //chooses the story category first
        chooseCategory();
        //and then sends you to the StoryActivity
        Intent intent = new Intent(this, StoryActivity.class);
        /*
        category types that can be retrieved:
        horror
        adventure
        poetry
        science fiction
        random
         */
        intent.putExtra("category", category);
        intent.putExtra("sensor",status);
        startActivity(intent);
    }

    public void shareApp(View view) {
        //determine the story category
        chooseCategory();
        //this creates an implicit intent to share this app via a sms
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", "", null));
        intent.putExtra("sms_body", "Check out Transcribed Vibes! My category today is " + category + "!");
        startActivity(intent);
    }

    private JsonObjectRequest requestObj(double lat, double lon) {

        //open-meteo API, free for open source non-commercial use
        String url =
                "https://api.open-meteo.com/v1/forecast?" +
                        "latitude=" + lat + "&longitude=" + lon +
                        "&current_weather=true" +
                        "&temperature_unit=fahrenheit" +
                        "&windspeed_unit=mph" +
                        "&precipitation_unit=inch";

        JsonObjectRequest r = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject obj = response;
                //uses their current weather feature, and just gets the weather code
                int weatherID = obj.getJSONObject("current_weather").getInt("weathercode");
                Log.d(TAG2, "got weatherID");
                setWeather(weatherID); //sets the weather based on the code received
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d(TAG2, "Failed to get weather");
        });

        return r;
    }

    private void setWeather(int id) {
        //there are many codes,
        // so for simplicity narrowed potential results to those relevant for this app
        int adjustedID;
        if (id < 10) { //if the code is under 10 keep it
            adjustedID = id;
        } else if (id == 85 || id == 86) { //if code calls for snow showers
            adjustedID = 7; //70's are the codes for the other snow events, so change to snow event
        } else {
            adjustedID = id / 10; //all other available event codes were separated by 10's
        }
        switch (adjustedID) {
            case 3: //3 overcase
            case 4: //fog
                weather = "Overcast";
                break;
            case 5: //51, 53, 55 levels of drizzle 56, 57 freezing drizzle
            case 6: //61,63,65 levels of rain
            case 8: //80,81,82 levels of rain showers
            case 9: //95,96,99 thunderstorms
                weather = "Rainy";
                break;
            case 7: //71,73,75,77 levels of snow 85,86 snow showers
                weather = "Snowy";
                break;
            default:
                //0 clear sky
                //1 mainly clear
                //2 partly cloudy
                weather = "Clear";
        }
        Log.d(TAG2,"Set the weather");
    }

    private void getWeatherByLocation() {
        //GPS Stuff
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //Gets last location from phone - exact isn't really necessary
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (!location.equals(null)) {
                            //Set lat and lon values to the location lat and long
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            Log.d(TAG2, "Latitude: " + lat + " Longitude: " + lon);
                        } else {
                            Log.d(TAG2, "Could not get weather by location");
                        }
                    }
                }).addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Add to api queue to get the weather from these coordinates
                Log.d(TAG2, "Before req: Latitude: " + lat + " Longitude: " + lon);
                RequestSingleton.getInstance(getApplicationContext()).addToRequestQueue(requestObj(lat, lon));
            }
        });
    }
      
       /**
     * this method asks the user for permission to use the sensors
     */
    public void checkPermissions(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//ask for permission
            requestPermissions(new
                    String[]{Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }else {
            //otherwise just note that permission was already granted
            Log.d("perm", "permissions granted");
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
        String light_Type;
        float light; //simply holds the reported reading from the light sensor
        int MAX_LIGHT_VALUE = 500; //cap for actionable range of light value (0-this val)

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
            float[] currentValue = event.values;
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                light = currentValue[0];
                if (light > MAX_LIGHT_VALUE) {
                    light = MAX_LIGHT_VALUE;
                }
            }
            setLightType();
        }

        /**
         * this method gets the ratio of light to max_light_value and sets the light_Type
         * to a corresponding string.
         */
        public void setLightType() {
            double lightRatio = light / MAX_LIGHT_VALUE;

            if (lightRatio < 0.1)
                light_Type = "Dark";
            else if (lightRatio < 0.25)
                light_Type = "Ambient";
            else
                light_Type = "Bright";
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