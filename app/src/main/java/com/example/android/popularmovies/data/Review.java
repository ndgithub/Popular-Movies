package com.example.android.popularmovies.data;

/**
 * Created by Nicky on 1/10/17.
 */

public class Review {
    private String mAuthor;
    private String mContent;
    private String mURL;

    public Review(String author, String content, String URL) {
        this.mAuthor = author;
        this.mContent = content;
        this.mURL = URL;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public String getURL() {
        return mURL;
    }
}
