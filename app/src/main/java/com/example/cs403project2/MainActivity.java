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
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    String light_Type;
    LightSensorObject lightSensorObject; //our sensor event listener object
    String TAG = "light"; //light debug tag
    String light_type; //the amount of light read by the light sensor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        lightSensorObject = new LightSensorObject();

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
    class LightSensorObject implements SensorEventListener{
        SensorManager sensorManager;
        Sensor sensor_light;
        float light; //simply holds the reported reading from the light sensor
        int MAX_LIGHT_VALUE = 500; //cap for actionable range of light value (0-this val)
        String light_Type;
        public LightSensorObject(){
            sensorInit();
        }

        /**
         * this is a simple method which gives handles to the sensor manager and the devices
         * light sensor.
         */
        public void sensorInit(){
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensor_light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        /**
         * override method for SensorEventListener
         * in this method we can grab the light value from the light sensor, and then
         * call a method to change our ui based on that value
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
        public void setLightType(){
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
         * @param sensor
         * @param accuracy
         */
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy){

        }
        /**
         * getter methods for private instance variables
         */
        public int getLight(){
            return (int) light;
        }
        public int getMAX_LIGHT_VALUE(){
            return MAX_LIGHT_VALUE;
        }
        public String getLight_Type(){
            return light_Type;
        }
    }
}