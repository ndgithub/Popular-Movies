package com.example.android.popularmovies;

import android.app.Application;
import android.support.compat.BuildConfig;

import com.facebook.stetho.Stetho;

/**
 * Created by Nicky on 11/19/17.
 */

public class PopularMoviesApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

    }

}
