package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.MovieContract.FavoritesEntry;

/**
 * Created by Nicky on 9/29/16.
 */

public class FavMovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorite movies";
    private static final int DATABASE_VERSION = 1;

    public FavMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE_QUERY = "CREATE TABLE " + FavoritesEntry.TABLE_NAME + "(" +
                FavoritesEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoritesEntry.COLUMN_MOVIE_ID + " TEXT," +
                FavoritesEntry.COLUMN_BACKDROP_PATH + " TEXT," +
                FavoritesEntry.COLUMN_OVERVIEW + " TEXT," +
                FavoritesEntry.COLUMN_POSTER_PATH + " TEXT," +
                FavoritesEntry.COLUMN_TITLE + " TEXT," +
                FavoritesEntry.COLUMN_RATING + " TEXT," +
                FavoritesEntry.COLUMN_RELEASE_DATE + " TEXT)";

        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
