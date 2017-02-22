package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.movieDetails.MovieDetailsPresenter;
import com.example.android.popularmovies.movieList.MovieListContract;
import com.example.android.popularmovies.movieList.movieListPresenter;
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
    public static boolean fromTop = true;
    private ContentResolver mContentResolver;
    private SharedPreferences sharedPref;
    private Context mContext;
    private String sortPref;
    private ArrayList<Movie> movieList = new ArrayList<>();
    private movieListPresenter mPresenter = null;
    private MovieDetailsPresenter movieDetailsPresenter = null;

    public MVPmodel(ContentResolver contentResolver, Context context, movieListPresenter presenter) {
        mContentResolver = contentResolver;
        mContext = context;
        PreferenceManager.setDefaultValues(context, R.xml.pref_general, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        mPresenter = presenter;
    }

    public MVPmodel(ContentResolver contentResolver, Context context, MovieDetailsPresenter detailsPresenter) {
        mContentResolver = contentResolver;
        mContext = context;
        PreferenceManager.setDefaultValues(context, R.xml.pref_general, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        movieDetailsPresenter = detailsPresenter;
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
        prefEditor.apply();
    }

    public void getMovieList() {
        sortPref = getSortPref();
        //Log.v("***** - MVPModel","sortPref is: " + sortPref);
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
                        }
                    });
            SingletonRequestQueue.getInstance(mContext.getApplicationContext()).addToRequestQueue(jsObjRequest);

        }

    }

    private ArrayList<Movie> extractMoviesFromJson(JSONObject baseJsonResponse) {
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

    public void getTrailersAndUpdateUI(Movie selectedMovie) {
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                (Request.Method.GET,
                        "http://api.themoviedb.org/3/movie/" + selectedMovie.getId() +
                                "/videos?api_key=" + QueryUtils.API_KEY, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Video> videoList = getVideosFromJson(response);
                        movieDetailsPresenter.trailerListRecieved(videoList);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (QueryUtils.isConnectedToInternet(mContext)) {
                            Toast.makeText(mContext, R.string.videos_unavailable, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        SingletonRequestQueue.getInstance(mContext).addToRequestQueue(jsonObjRequest);
    }

    public void getCastListAndUpdateUI(Movie selectedMovie) {
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                (Request.Method.GET,
                        "http://api.themoviedb.org/3/movie/" + selectedMovie.getId() +
                                "/casts?api_key=" + QueryUtils.API_KEY, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<CastMember> castList = extractCastFromJson(response);
                        movieDetailsPresenter.castListRecieved(castList);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (QueryUtils.isConnectedToInternet(mContext)) {
                            Toast.makeText(mContext, R.string.error_retrieving_cast_data, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        SingletonRequestQueue.getInstance(mContext).addToRequestQueue(jsonObjRequest);
    }

    //TODO: Reviews get cutoff (also videos)
    public void getReviewsAndUpdateUI(Movie selectedMovie) {

            JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                    (Request.Method.GET,
                            "http://api.themoviedb.org/3/movie/" + selectedMovie.getId() +
                                    "/reviews?api_key=" + QueryUtils.API_KEY, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            ArrayList<Review> reviewList = getReviewsFromJson(response);
                            movieDetailsPresenter.reviewListRecieved(reviewList);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (QueryUtils.isConnectedToInternet(mContext)) {
                                Toast.makeText(mContext, R.string.reviews_unavailable, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            SingletonRequestQueue.getInstance(mContext).addToRequestQueue(jsonObjRequest);

    }

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

    public void addToFavoritesDb(Movie selectedMovie) {

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
            movieDetailsPresenter.favAddError();
        } else {
            movieDetailsPresenter.onAddedToFavorites();

        }
    }

    public void removeFromFavoritesDb(Movie selectedMovie) {
        int rowsDel = mContentResolver.delete(MovieDbContract.FavoritesEntry.CONTENT_URI,
                MovieDbContract.FavoritesEntry.COLUMN_MOVIE_ID + " = " + selectedMovie.getId(), null);
        if (rowsDel == 1) {

            movieDetailsPresenter.onRemovedFromFavorites();

        } else {

            movieDetailsPresenter.favRemoveError();
        }
    }


    public static ArrayList<CastMember> extractCastFromJson(JSONObject baseJsonResponse) {

        ArrayList<CastMember> castArrayList = new ArrayList<>();
        try {
            JSONArray castResultsArray = baseJsonResponse.getJSONArray("cast");
            for (int i = 0; i < 6; i++) {
                JSONObject currentCastMember = castResultsArray.getJSONObject(i);
                String characterName = currentCastMember.getString("character");
                String actorName = currentCastMember.getString("name");
                String picPath = currentCastMember.getString("profile_path");
                castArrayList.add(new CastMember(actorName, characterName, picPath));
            }
        } catch (JSONException e) {
            //Prevent app from crashing if there is a problem with parsing json.
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }
        return castArrayList;
    }
    public static ArrayList<Video> getVideosFromJson(JSONObject baseJsonResponse) {
        ArrayList<Video> videoArrayList = new ArrayList<>();
        try {
            JSONArray videoResultsArray = baseJsonResponse.getJSONArray("results");
            for (int i = 0; i < videoResultsArray.length(); i++) {
                JSONObject currentVideo = videoResultsArray.getJSONObject(i);
                String videoKey =  currentVideo.getString("key");
                String videoName = currentVideo.getString("name");
                videoArrayList.add(new Video(videoKey,videoName));
            }

        } catch (JSONException e) {
            //Prevent app from crashing if there is a problem with parsing json.
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }
        return videoArrayList;
    }

    public static ArrayList<Review> getReviewsFromJson(JSONObject baseJsonResponse) {
        ArrayList<Review> reviewArrayList = new ArrayList<>();
        try {
            JSONArray reviewResultsArray = baseJsonResponse.getJSONArray("results");
            for (int i = 0; i < reviewResultsArray.length(); i++) {
                JSONObject currentReview = reviewResultsArray.getJSONObject(i);
                String reviewAuthor =  currentReview.getString("author");
                String reviewContent = currentReview.getString("content");
                String reviewURL = currentReview.getString("url");
                reviewArrayList.add(new Review(reviewAuthor,reviewContent,reviewURL));
            }

        } catch (JSONException e) {
            //Prevent app from crashing if there is a problem with parsing json.
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }
        return reviewArrayList;
    }
}