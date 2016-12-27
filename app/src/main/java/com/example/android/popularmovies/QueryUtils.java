/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {
    public static final String API_KEY = "d962b00501dc49c8dfd38339a7daa32a";
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();


    private QueryUtils() {
    }

    public static boolean isConnectedToInternet(Context c) {
        ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.v(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */

    public static ArrayList<Movie> extractMovieFeaturesFromJson(JSONObject baseJsonResponse) {

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

    public static ArrayList<CastMember> extractCastFromJson(JSONObject castJSON) {

        ArrayList<CastMember> castArrayList = new ArrayList<>();
        try {
            JSONArray castResultsArray = castJSON.getJSONArray("cast");
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
}
