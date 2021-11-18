package com.example.cs403project2;

import java.util.ArrayList;
import java.util.Random;

public class StoriesManager {
    static ArrayList<Story> stories;
    static ArrayList<String> storyCategories;

    public StoriesManager() {
        stories = new ArrayList<>();
        storyCategories = new ArrayList<>();

        //add the story categories here
        storyCategories.add("Horror");
        storyCategories.add("Adventure");
        storyCategories.add("Poetry");
        storyCategories.add("Something?");
    }

    public ArrayList<Story> getStories() {
        return stories;
    }

    public void addStory(){

    }

    public ArrayList<String> getStoryCategories() {
        return storyCategories;
    }

    // Method for retrieving a random horror story
    public Story getHorrorStory() {
        int storyNumber = generateStory();
        Story story = new Story();
        story.setFileName("horror" + storyNumber + ".txt");
        story.setCategory("Horror");

        switch (storyNumber) {
            case 0:
                story.setTitle("A Collapse of Horses");
                story.setAuthor("Brian Evenson");
                break;
            case 1:
                story.setTitle("Ickbarr Bigelsteine");
                story.setAuthor("Stephan D. Harris");
                break;
            case 2:
                story.setTitle("In the Cave of the Delicate Singers");
                story.setAuthor("Lucy Taylor");
                break;
            case 3:
                story.setTitle("Lacrimosa");
                story.setAuthor("Silvia Moreno-Garcia");
                break;
            case 4:
                story.setTitle("Mr. Widemouth");
                story.setAuthor("Anonymous");
                break;
            case 5:
                story.setTitle("Smile Dog");
                story.setAuthor("Anonymous");
                break;
            case 6:
                story.setTitle("Squidward's Suicide");
                story.setAuthor("Anonymous");
                break;
            case 7:
                story.setTitle("The Ash of Memory, the Dust of Desire");
                story.setAuthor("Billy Martin");
                break;
            case 8:
                story.setTitle("The Goatman");
                story.setAuthor("Anansi");
                break;
            case 9:
                story.setTitle("The Russian Sleep Experiment");
                story.setAuthor("Anonymous");
                break;
        }

        return story;
    }

    // Method for retrieving a random adventure story
    public String getAdventureStory() {
        return "Adventure";
    }

    // Method for retrieving random poetry
    public String getPoetryStory() {
        return "Poetry";
    }

    // Method for retrieving a random something? story
    public String getSomethingStory() {
        return "Something?";
    }

    // Generate a random number for choosing a story
    private int generateStory() {
        Random random = new Random();
        return random.nextInt(10);
    }
}
