package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.movieList.MovieListContract;
import com.example.android.popularmovies.utils.QueryUtils;
import com.example.android.popularmovies.utils.SingletonRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nicky on 1/23/17.
 */

public class MVPmodel {

    private static MVPmodel modelInstance = null;
    private ContentResolver mContentResolver;
    private SharedPreferences sharedPref;
    private Context mContext;
    private String sortPref;
    private ArrayList<Movie> movieList = new ArrayList<>();
    private MovieListContract.UserActionsListener mPresenter = null;

    public MVPmodel(ContentResolver contentResolver, Context context, MovieListContract.UserActionsListener presenter) {
        mContentResolver = contentResolver;
        mContext = context;
        PreferenceManager.setDefaultValues(context, R.xml.pref_general, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        mPresenter = presenter;

    }


    public void changeSortPreference(MenuItem item) {
        int itemId = item.getItemId();
        SharedPreferences.Editor prefEditor;
        prefEditor = sharedPref.edit();
        switch (itemId) {
            case (R.id.pop):
                prefEditor.putString("sort_by", "popular");
                break;
            case (R.id.top):
                prefEditor.putString("sort_by", "top_rated");
                break;
            case (R.id.fav):
                prefEditor.putString("sort_by", "favorite");
        }
        prefEditor.commit();
    }

    public void getMovieList() {
        sortPref = getSortPref();
        Log.v("MVPModel","sortPref is: " + sortPref);
        if (sortPref.equals("favorite")) {
            movieList = getFavoritesList();
            mPresenter.listRecieved(movieList);
        } else {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET,
                            "https://api.themoviedb.org/3/movie/" + sortPref + "?api_key=" + QueryUtils.API_KEY + "&language=en-US",
                            null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            movieList = extractMoviesFromJson(response);
                            mPresenter.listRecieved(movieList);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.v("***** - MVPModel", "Volley Error");
                        }
                    });
            SingletonRequestQueue.getInstance(mContext.getApplicationContext()).addToRequestQueue(jsObjRequest);
            Log.v("***** - MVPModel", "Movie List2: " + movieList);
        }
        Log.v("***** - MVPModel", "Movie List3: " + movieList);
    }

    public ArrayList<Movie> extractMoviesFromJson(JSONObject baseJsonResponse) {
        ArrayList<Movie> movieArrayList = new ArrayList<>();
        try {
            JSONArray movieResultsArray = baseJsonResponse.getJSONArray("results");
            for (int i = 0; i < movieResultsArray.length(); i++) {
                JSONObject currentMovie = movieResultsArray.getJSONObject(i);
                String id = currentMovie.getString("id");
                String posterPath = currentMovie.getString("poster_path");
                String overview = currentMovie.getString("overview");
                String date = currentMovie.getString("release_date");
                String rating = currentMovie.getString("vote_average");
                String title = currentMovie.getString("title");
                String backdropPath = currentMovie.getString("backdrop_path");
                movieArrayList.add(new Movie(id, backdropPath, overview, posterPath, title, rating, date));
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }
        return movieArrayList;
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

    public String getSortPref() {
        return sharedPref.getString("sort_by", null);
    }
}