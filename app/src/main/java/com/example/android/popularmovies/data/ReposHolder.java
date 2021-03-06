package com.example.android.popularmovies.data;

import android.util.Log;

import com.example.android.popularmovies.data.remote.MovieServiceAPI;

/**
 * Created by Nicky on 12/6/17.
 */

public class ReposHolder {

    private static MovieRepoInterface mMovieRepo = null;


    private ReposHolder() {

    }

    public static synchronized MovieRepoInterface getMovieRepo(UserPrefInterface userPrefRepo, MovieServiceAPI movieServiceAPI) {

        if (mMovieRepo == null) {
            mMovieRepo = new MovieRepo(userPrefRepo, movieServiceAPI);
            return mMovieRepo;
        } else {
            return mMovieRepo;
        }
    }


}
