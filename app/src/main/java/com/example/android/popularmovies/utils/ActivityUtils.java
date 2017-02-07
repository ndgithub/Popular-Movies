package com.example.android.popularmovies.utils;

import android.content.Context;

import com.example.android.popularmovies.R;

/**
 * Created by Nicky on 2/6/17.
 */

public class ActivityUtils {
    public static boolean isTwoPane(Context context) {
        return context.getResources().getBoolean(R.bool.twoPane);
    }
}
