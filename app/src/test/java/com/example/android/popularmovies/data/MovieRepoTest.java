package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.Context;

import org.junit.Before;
import org.mockito.Mock;

/**
 * Created by Nicky on 11/29/17.
 */

public class MovieRepoTest {

    MovieRepo mMovieRepo;

    @Mock
    ContentResolver mContentResolver;

    @Mock
    Context mContext;


    @Before
    public void setupMovieRepo() {
        mMovieRepo = new MovieRepo(mContentResolver,mContext);
    }





}
