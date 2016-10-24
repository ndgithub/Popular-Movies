package com.example.android.popularmovies;

/**
 * Created by Nicky on 10/21/16.
 */

public class Movie {

    private String posterPath;
    private String overview;
    private String title;
    private String backdropPath;
    private String rating;
    private String date;

    public Movie(String backdropPath,String overview, String posterPath, String title,String rating,String date) {
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.posterPath = posterPath;
        this.title = title;
        this.rating = rating;
        this.date = date;
    }

    public String getBackdropPath() {
        return backdropPath;
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

    public String getRating() {
        return rating;
    }

    public String getDate() {
        return date;
    }
}
