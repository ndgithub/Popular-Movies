package com.example.android.popularmovies.data;

import org.parceler.Parcel;

/**
 * Created by Nicky on 10/21/16.
 */

@Parcel
public class Movie {
    private String mId;
    private String mPosterPath;
    private String mOverview;
    private String mTitle;
    private String mBackdropPath;
    private String mRating;
    private String mDate;

    public Movie() {
    }

    public Movie(String id, String backdropPath, String overview, String posterPath, String title, String rating, String date) {
        this.mId = id;
        this.mBackdropPath = backdropPath;
        this.mOverview = overview;
        this.mPosterPath = posterPath;
        this.mTitle = title;
        this.mRating = rating;
        this.mDate = date;
    }

    public String getId() {return mId;}

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getRating() {
        return mRating;
    }

    public String getDate() {
        return mDate;
    }
}
