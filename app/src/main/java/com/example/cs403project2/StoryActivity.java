package com.example.cs403project2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class StoryActivity extends AppCompatActivity {

    StoriesManager manager;

    TextView txtTitle;
    TextView txtAuthor;
    TextView txtStory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        manager = new StoriesManager();

        txtTitle = findViewById(R.id.txtTitle);
        txtAuthor = findViewById(R.id.txtAuthor);
        txtStory = findViewById(R.id.txtStory);

        // Populate with the genre for the present weather / light conditions
        Bundle bundle = getIntent().getExtras();
        String genre = bundle.getString("category");

        try {
            // Generate and display a random story for the present genre
            displayStory(genre);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayStory(String category) throws IOException {
        // Create an empty story object
        Story story;
        Log.d("storyDebug", category);

        // Call the appropriate method to get a random story
        switch (category) {
            case "Horror":
                story = manager.getHorrorStory();
                break;
            case "Adventure":
                story = manager.getAdventureStory();
                break;
            case "Poetry":
                story = manager.getPoetryStory();
                break;
            case "Science Fiction":
                story = manager.getScienceFictionStory();
                break;
            default:
                displayStory(getRandomStory());
                return;
        }

        // Display the title and author of the story
        txtTitle.setText(story.getTitle());
        txtAuthor.setText(story.getAuthor());

        try {
            // Use an input stream to convert a txt file into a byte
            InputStream is = getAssets().open(story.getFileName());
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Display the story
            String text = new String(buffer);
            txtStory.setText(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRandomStory() {
        Random random = new Random();
        int newStory = random.nextInt(4);

        switch(newStory) {
            case 0:
                return "Horror";
            case 1:
                return "Adventure";
            case 2:
                return "Poetry";
            case 3:
                return "Science Fiction";
            default:
                return "";
        }
    }

    public void back(View view) {
        finish();
    }
}
