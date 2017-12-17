package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Nicky on 12/3/17.
 */

public class UserPrefImpl implements UserPrefInterface {

    ContentResolver mContentResolver;
    Context mContext;
    private SharedPreferences mSharedPref;


    public UserPrefImpl(ContentResolver contentResolver, Context context) {
        mContentResolver = contentResolver;
        mContext = context;
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Log.v("***", "msharedPref: " + mSharedPref.toString());
    }


    @Override
    public boolean isFavorite(Movie selectedMovie) {
        String thisMovieId = selectedMovie.getId();
        String[] projection = {MovieDbContract.FavoritesEntry.COLUMN_MOVIE_ID};
        Cursor cursor = mContentResolver.query(MovieDbContract.FavoritesEntry.CONTENT_URI, projection,
                MovieDbContract.FavoritesEntry.COLUMN_MOVIE_ID + " = " + thisMovieId, null, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    @Override
    public void removeFromFavorites(Movie selectedMovie, UserPrefInterface.RemoveFavoriteCallback callback) {
        int rowsDel = mContentResolver.delete(MovieDbContract.FavoritesEntry.CONTENT_URI,
                MovieDbContract.FavoritesEntry.COLUMN_MOVIE_ID + " = " + selectedMovie.getId(), null);
        if (rowsDel == 1) {
            callback.removeSuccess();
        } else {
            callback.removeError();
            Log.v("!!!", "3");
        }
    }

    @Override
    public void addToFavorites(Movie selectedMovie, UserPrefInterface.AddFavoriteCallback callback) {
        ContentValues cv = new ContentValues();
        cv.put(MovieDbContract.FavoritesEntry.COLUMN_MOVIE_ID, selectedMovie.getId());
        cv.put(MovieDbContract.FavoritesEntry.COLUMN_BACKDROP_PATH, selectedMovie.getBackdropPath());
        cv.put(MovieDbContract.FavoritesEntry.COLUMN_OVERVIEW, selectedMovie.getOverview());
        cv.put(MovieDbContract.FavoritesEntry.COLUMN_POSTER_PATH, selectedMovie.getPosterPath());
        cv.put(MovieDbContract.FavoritesEntry.COLUMN_TITLE, selectedMovie.getTitle());
        cv.put(MovieDbContract.FavoritesEntry.COLUMN_RATING, selectedMovie.getRating());
        cv.put(MovieDbContract.FavoritesEntry.COLUMN_RELEASE_DATE, selectedMovie.getDate());

        Uri newRowUri = mContentResolver.insert(MovieDbContract.FavoritesEntry.CONTENT_URI, cv);

        if (newRowUri == null) {
            callback.addError();
        } else {
            callback.addSuccess();
        }
    }

    @Override
    public String getSortPref() {
        return mSharedPref.getString("sort_by", "popular");
    }

    @Override
    public void changeSortPreference(String sortPref) {
        SharedPreferences.Editor prefEditor = mSharedPref.edit();
        switch (sortPref) {
            case ("popular"):
                prefEditor.putString("sort_by", "popular");
                break;
            case ("top_rated"):
                prefEditor.putString("sort_by", "top_rated");
                break;
            case ("favorite"):
                prefEditor.putString("sort_by", "favorite");
        }
        prefEditor.apply();
    }

    public ArrayList<Movie> getFavoritesList() {

        String[] projection = {MovieDbContract.FavoritesEntry.COLUMN_MOVIE_ID,
                MovieDbContract.FavoritesEntry.COLUMN_BACKDROP_PATH,
                MovieDbContract.FavoritesEntry.COLUMN_OVERVIEW,
                MovieDbContract.FavoritesEntry.COLUMN_POSTER_PATH,
                MovieDbContract.FavoritesEntry.COLUMN_TITLE,
                MovieDbContract.FavoritesEntry.COLUMN_RATING,
                MovieDbContract.FavoritesEntry.COLUMN_RELEASE_DATE};

        Cursor cursor = mContentResolver.query(
                MovieDbContract.FavoritesEntry.CONTENT_URI,   // The content URI of the words table
                projection,             // The columns to return for each row
                null,                   // Selection criteria
                null,                   // Selection criteria
                null);

        int idColumnIndex = cursor.getColumnIndex(MovieDbContract.FavoritesEntry.COLUMN_MOVIE_ID);
        int backdropColumnIndex = cursor.getColumnIndex(MovieDbContract.FavoritesEntry.COLUMN_BACKDROP_PATH);
        int overviewColumnIndex = cursor.getColumnIndex(MovieDbContract.FavoritesEntry.COLUMN_OVERVIEW);
        int PosterPathColumnIndex = cursor.getColumnIndex(MovieDbContract.FavoritesEntry.COLUMN_POSTER_PATH);
        int titleColumnIndex = cursor.getColumnIndex(MovieDbContract.FavoritesEntry.COLUMN_TITLE);
        int ratingColumnIndex = cursor.getColumnIndex(MovieDbContract.FavoritesEntry.COLUMN_RATING);
        int dateColumnIndex = cursor.getColumnIndex(MovieDbContract.FavoritesEntry.COLUMN_RELEASE_DATE);
        ArrayList<Movie> list = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                String movieId = cursor.getString(idColumnIndex);
                String backdrop = cursor.getString(backdropColumnIndex);
                String overview = cursor.getString(overviewColumnIndex);
                String posterPath = cursor.getString(PosterPathColumnIndex);
                String title = cursor.getString(titleColumnIndex);
                String rating = cursor.getString(ratingColumnIndex);
                String date = cursor.getString(dateColumnIndex);

                list.add(new Movie(movieId, backdrop, overview, posterPath, title, rating, date));
            }
        } finally {
            cursor.close();
        }
        return list;
    }
}
