package com.example.android.popularmovies;

/**
 * Created by Nicky on 1/9/17.
 */

public class Video {
    private String key;
    private String videoTitle;

    public Video(String key, String title) {
        this.key = key;
        this.videoTitle = title;
    }

    public String getKey() {
        return key;
    }

    public String getVideoTitle() {
        return videoTitle;
    }
}
