package com.example.android.popularmovies.data.remote;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.popularmovies.data.CastMember;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieRepoInterface;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Video;
import com.example.android.popularmovies.utils.QueryUtils;
import com.example.android.popularmovies.utils.SingletonRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nicky on 12/1/17.
 */

public class MovieServiceApiImpl implements MovieServiceAPI {

    Context mContext;

    public MovieServiceApiImpl(Context context) {
        mContext = context;
    }

    @Override
    public void getMovieList(final String sortPref, final MovieRepoInterface.LoadMoviesCallback callback) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET,
                        "https://api.themoviedb.org/3/movie/" + sortPref + "?api_key=" + QueryUtils.API_KEY + "&language=en-US",
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Movie> movieList = extractMoviesFromJson(response);
                        callback.onMoviesLoaded(movieList);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        SingletonRequestQueue.getInstance(mContext.getApplicationContext()).addToRequestQueue(jsObjRequest);

    }

    @Override
    public void getCast(Movie selectedMovie, final MovieServiceAPI.LoadCastCallback callback) {

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
                            callback.onCastError();
                        }
                    }
                });
        SingletonRequestQueue.getInstance(mContext).addToRequestQueue(jsonObjRequest);
    }

    @Override
    public void getTrailers(Movie selectedMovie, final MovieServiceAPI.LoadTrailersCallback callback) {
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                (Request.Method.GET,
                        "http://api.themoviedb.org/3/movie/" + selectedMovie.getId() +
                                "/videos?api_key=" + QueryUtils.API_KEY, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Video> videoList = getVideosFromJson(response);
                        callback.onTrailersLoaded(videoList);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (QueryUtils.isConnectedToInternet(mContext)) {
                            callback.onTrailersError();
                        }
                    }
                });
        SingletonRequestQueue.getInstance(mContext).addToRequestQueue(jsonObjRequest);
    }

    @Override
    public void getReviews(Movie selectedMovie, final MovieServiceAPI.LoadReviewsCallback callback) {
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
                            callback.onReviewsError();
                        }
                    }
                });
        SingletonRequestQueue.getInstance(mContext).addToRequestQueue(jsonObjRequest);
    }


    //----------------------- JSON Helpers -----------------------//
    private static ArrayList<CastMember> extractCastFromJson(JSONObject baseJsonResponse) {

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

    private static ArrayList<Video> getVideosFromJson(JSONObject baseJsonResponse) {
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

    private static ArrayList<Review> getReviewsFromJson(JSONObject baseJsonResponse) {
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
