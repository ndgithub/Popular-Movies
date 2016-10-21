package com.example.android.popularmovies;

/**
 * Created by Nicky on 10/21/16.
 */

public class Movie {

    private String posterPath;
    private String overview;
    private String id;
    private String title;
    private String backdropPath;

    public Movie(String backdropPath, String id, String overview, String posterPath, String title) {
        this.backdropPath = backdropPath;
        this.id = id;
        this.overview = overview;
        this.posterPath = posterPath;
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getId() {
        return id;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getTitle() {
        return title;
    }
}
