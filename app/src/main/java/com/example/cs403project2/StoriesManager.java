package com.example.cs403project2;

import java.util.ArrayList;

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
}
