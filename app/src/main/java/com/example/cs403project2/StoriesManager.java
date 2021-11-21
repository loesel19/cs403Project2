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
        storyCategories.add("Science Fiction");
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
        // Generate a random story within the appropriate genre
        int storyNumber = generateStory();

        // Create a story item to be returned with the file name of the story, as well as the genre,
        // title, and author of the story. This will be used to populate the text fields within the
        // story activity.
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
    public Story getAdventureStory() {
        // Generate a random story within the appropriate genre
        int storyNumber = generateStory();

        // Create a story item to be returned with the file name of the story, as well as the genre,
        // title, and author of the story. This will be used to populate the text fields within the
        // story activity.
        Story story = new Story();
        story.setFileName("adv" + storyNumber + ".txt");
        story.setCategory("Adventure");

        switch (storyNumber) {
            case 0:
                story.setTitle("ALN23061912RK");
                story.setAuthor("Cyndy Spice");
                break;
            case 1:
                story.setTitle("Aloha Treasure");
                story.setAuthor("Debbie Curtin");
                break;
            case 2:
                story.setTitle("Dance to the End of Life");
                story.setAuthor("Divya Narasimhan");
                break;
            case 3:
                story.setTitle("Hindsight is a Wonderful Thing");
                story.setAuthor("Alice Richardson");
                break;
            case 4:
                story.setTitle("Mahalo King Cod Filet");
                story.setAuthor("David Gottfried");
                break;
            case 5:
                story.setTitle("Tales from the Ancient Forest");
                story.setAuthor("Alex Lewig");
                break;
            case 6:
                story.setTitle("The Collector");
                story.setAuthor("Shane Ransom");
                break;
            case 7:
                story.setTitle("The Last Train");
                story.setAuthor("Marlee Brooks");
                break;
            case 8:
                story.setTitle("The Tall Pine and the Pond");
                story.setAuthor("Robyn Jackson");
                break;
            case 9:
                story.setTitle("Unpaving Paradise");
                story.setAuthor("Ruth Midgley");
                break;
        }
        return story;
    }

    // Method for retrieving random poetry
    public Story getPoetryStory() {
        return new Story();
    }

    // Method for retrieving a random science fiction story
    public Story getScienceFictionStory() {
        // Generate a random story within the appropriate genre
        int storyNumber = generateStory();

        // Create a story item to be returned with the file name of the story, as well as the genre,
        // title, and author of the story. This will be used to populate the text fields within the
        // story activity.
        Story story = new Story();
        story.setFileName("sf" + storyNumber + ".txt");
        story.setCategory("Science Fiction");

        switch (storyNumber) {
            case 0:
                story.setTitle("Articulated Restraint");
                story.setAuthor("Mary Robinette Kowal");
                break;
            case 1:
                story.setTitle("Bloodchild");
                story.setAuthor("Octavia Butler");
                break;
            case 2:
                story.setTitle("Exhalation");
                story.setAuthor("Ted Chiang");
                break;
            case 3:
                story.setTitle("Homesick");
                story.setAuthor("Sarah Gailey");
                break;
            case 4:
                story.setTitle("In Xanadu");
                story.setAuthor("Lavie Tidhar");
                break;
            case 5:
                story.setTitle("Inventory");
                story.setAuthor("Carmen Maria Machado");
                break;
            case 6:
                story.setTitle("Mountain Ways");
                story.setAuthor("Ursula K. Le Guin");
                break;
            case 7:
                story.setTitle("Requiem Without Sound");
                story.setAuthor("Izzy Wasserstein");
                break;
            case 8:
                story.setTitle("The Effluent Engine");
                story.setAuthor("N.K. Jemisin");
                break;
            case 9:
                story.setTitle("The Fermi Paradox Is Our Business Model");
                story.setAuthor("Charlie Jane Anders");
                break;
        }
        return story;
    }

    // Generate a random number for choosing a story
    private int generateStory() {
        Random random = new Random();
        return random.nextInt(10);
    }
}
