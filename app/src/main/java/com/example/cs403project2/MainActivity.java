package com.example.cs403project2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    String category; //category of the story that's going to be displayed in StoryActivity

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
}