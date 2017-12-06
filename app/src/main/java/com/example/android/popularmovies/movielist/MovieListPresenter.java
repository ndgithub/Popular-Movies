package com.example.android.popularmovies.movielist;


import android.util.Log;
import android.view.MenuItem;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieRepoInterface;

import java.util.ArrayList;

public class MovieListPresenter implements MovieListContract.UserActionsListener {

    private MovieRepoInterface mModel;
    private MovieListContract.View mView;

    public MovieListPresenter(MovieRepoInterface model, MovieListContract.View view) {
        mView = view;
        mModel = model;
    }

    @Override
    public void start() {
        showMovieList();
    }

    public void showMovieList() {
        mModel.getMovieList(new MovieRepoInterface.LoadMoviesCallback<ArrayList<Movie>>() {
            @Override
            public void onMoviesLoaded(ArrayList<Movie> movieList) {
                if (movieList != null) {
                    mView.showMovieList(movieList);
                    if (mModel.getCurrentMoviePos() == null) {
                        mModel.setSelectedMovie(0);
                    }
                    mView.showMovieDetailsUI();
                }
            }
        });
    }


    public void onSortByTapped() {
        String sortPref = mModel.getSortPref();
        mView.inflateSortOptionsMenu(sortPref);
    }


    @Override
    public void onSortChanged(MenuItem item) {
        mModel.setSelectedMovie(0);
        int itemId = item.getItemId();
        String pref = null;
        switch (itemId) {
            case (R.id.pop):
                pref = "popular";
                break;
            case (R.id.top):
                pref = "top_rated";
                break;
            case (R.id.fav):
                pref = "favorite";
        }
        mModel.changeSortPreference(pref);
        showMovieList();
    }

    @Override
    public void onMovieSelected(int position) {
        mModel.setSelectedMovie(position);
        mView.showMovieDetailsUI();
    }


}