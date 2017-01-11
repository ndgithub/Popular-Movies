package com.example.android.popularmovies;

/**
 * Created by Nicky on 1/10/17.
 */

public class Review {
    private String author;
    private String content;
    private String URL;

    public Review(String author, String content, String URL) {
        this.author = author;
        this.content = content;
        this.URL = URL;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getURL() {
        return URL;
    }
}
