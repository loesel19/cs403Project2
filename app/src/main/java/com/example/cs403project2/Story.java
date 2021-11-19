package com.example.cs403project2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Story {
    private String fileName;
    private String title;
    private String author;
    private String category;

    //adri, feel free to modify this class however you need. i just needed a framework
    // of storiesmanager to store the storycategories somewhere -beata

    public Story() {
        this.fileName = "";
        this.title = "";
        this.author = "";
        this.category = "";
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
