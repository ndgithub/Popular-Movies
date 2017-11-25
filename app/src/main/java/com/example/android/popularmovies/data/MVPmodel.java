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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.moviedetails.MovieDetailsPresenter;
import com.example.android.popularmovies.movielist.MovieListPresenter;
import com.example.android.popularmovies.utils.QueryUtils;
import com.example.android.popularmovies.utils.SingletonRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nicky on 1/23/17.
 */

public class MVPmodel implements ModelInterface {

    public static boolean fromTop = true;
    private ContentResolver mContentResolver;
    private SharedPreferences mSharedPref;
    private Context mContext;
    private String mSortPref;
    private ArrayList<Movie> mMovieList = new ArrayList<>();

    public MVPmodel(ContentResolver contentResolver, Context context) {
        mContentResolver = contentResolver;
        mContext = context;
        PreferenceManager.setDefaultValues(context, R.xml.pref_general, false);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void changeSortPreference(MenuItem item) {
        int itemId = item.getItemId();
        SharedPreferences.Editor prefEditor = mSharedPref.edit();
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

    public void updateSortToFavorites() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putString("sort_by", "favorite");
        prefEditor.apply();
        MVPmodel.fromTop = true;

    }


    public void getMovieList(final LoadMoviesCallback callback) {
        mSortPref = getSortPref();
        if (mSortPref.equals("favorite")) {
            mMovieList = getFavoritesList();
            callback.onMoviesLoaded(mMovieList);
        } else {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET,
                            "https://api.themoviedb.org/3/movie/" + mSortPref + "?api_key=" + QueryUtils.API_KEY + "&language=en-US",
                            null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            mMovieList = extractMoviesFromJson(response);
                            callback.onMoviesLoaded(mMovieList);
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
        return mSharedPref.getString("sort_by", null);
    }

    public void getTrailers(Movie selectedMovie, final ModelInterface.TrailersLoadedCallback callback) {
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                (Request.Method.GET,
                        "http://api.themoviedb.org/3/movie/" + selectedMovie.getId() +
                                "/videos?api_key=" + QueryUtils.API_KEY, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Video> videoList = getVideosFromJson(response);
                        callback.onVideosLoaded(videoList);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (QueryUtils.isConnectedToInternet(mContext)) {
                            callback.errorLoadingVideos();
                        }
                    }
                });
        SingletonRequestQueue.getInstance(mContext).addToRequestQueue(jsonObjRequest);
    }

    public void getCast(Movie selectedMovie, final ModelInterface.CastLoadedCallback callback) {
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                (Request.Method.GET,
                        "http://api.themoviedb.org/3/movie/" + selectedMovie.getId() +
                                "/casts?api_key=" + QueryUtils.API_KEY, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<CastMember> castList = extractCastFromJson(response);
                        callback.onCastLoaded(castList);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (QueryUtils.isConnectedToInternet(mContext)) {
                            callback.errorLoadingCast();
                        }
                    }
                });
        SingletonRequestQueue.getInstance(mContext).addToRequestQueue(jsonObjRequest);
    }

    public void getReviews(Movie selectedMovie, final ModelInterface.ReviewsLoadedCallback callback) {

        JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                (Request.Method.GET,
                        "http://api.themoviedb.org/3/movie/" + selectedMovie.getId() +
                                "/reviews?api_key=" + QueryUtils.API_KEY, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Review> reviewList = getReviewsFromJson(response);
                        callback.onReviewsLoaded(reviewList);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (QueryUtils.isConnectedToInternet(mContext)) {
                            callback.errorLoadingReviews();
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

    public void addToFavoritesDb(Movie selectedMovie, ModelInterface.addFavoritesCallback callback) {

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
            callback.errorAddingToFav();
        } else {
            callback.successAddingToFav();

        }
    }

    public void removeFromFavoritesDb(Movie selectedMovie, ModelInterface.removeFavoritesCallback callback) {
        int rowsDel = mContentResolver.delete(MovieDbContract.FavoritesEntry.CONTENT_URI,
                MovieDbContract.FavoritesEntry.COLUMN_MOVIE_ID + " = " + selectedMovie.getId(), null);
        if (rowsDel == 1) {
            callback.successRemovingFav();
        } else {
            callback.errorRemovingFav();
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
                String videoKey = currentVideo.getString("key");
                String videoName = currentVideo.getString("name");
                videoArrayList.add(new Video(videoKey, videoName));
            }

        } catch (JSONException e) {
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
                String reviewAuthor = currentReview.getString("author");
                String reviewContent = currentReview.getString("content");
                String reviewURL = currentReview.getString("url");
                reviewArrayList.add(new Review(reviewAuthor, reviewContent, reviewURL));
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }
        return reviewArrayList;
    }


}