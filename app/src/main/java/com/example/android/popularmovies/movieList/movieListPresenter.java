package com.example.android.popularmovies.movieList;

import android.content.ContentResolver;
import android.content.Context;
import android.view.MenuItem;

import com.example.android.popularmovies.data.MVPmodel;
import com.example.android.popularmovies.data.Movie;

import java.util.ArrayList;

/**
 * Created by Nicky on 1/21/17.
 */

public class movieListPresenter implements MovieListContract.UserActionsListener {

    private MVPmodel model;

    private ContentResolver mContentResolver;
    private MovieListContract.View mView;

    private static movieListPresenter mMoviesListPresenter = null;
    private String sortPref;
    private Context mContext;


    private movieListPresenter(ContentResolver contentResolver, Context context, MovieListContract.View view) {
        mContentResolver = contentResolver;
        mContext = context;
        mView = view;
        model = MVPmodel.getInstance(mContentResolver, mContext,this);
    }

    public static movieListPresenter getInstance(ContentResolver contentResolver, Context context, MovieListContract.View view) {
        if (mMoviesListPresenter == null) {
            mMoviesListPresenter = new movieListPresenter(contentResolver, context, view);
        }
        return mMoviesListPresenter;
    }


    @Override
    public void start() {
        model.getMovieList();
    }

    public void onSortByTapped() {
        String sortPref = model.getSortPref();
        mView.inflateSortOptionsMenu(sortPref);

    }

    @Override
    public void listRecieved(ArrayList<Movie> movieList) {
        mView.showMovies(movieList);
    }

    @Override
    public void onSortChanged(MenuItem item) {
        model.changeSortPreference(item);
        model.getMovieList();

    }

    @Override
    public void onMovieSelected(int position) {
        mView.showMovieDetailsUI(position);
    }


}
