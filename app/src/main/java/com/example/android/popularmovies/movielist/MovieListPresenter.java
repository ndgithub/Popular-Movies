package com.example.android.popularmovies.movielist;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieRepoInterface;

import java.util.ArrayList;

public class MovieListPresenter implements MovieListContract.UserActionsListener {

    private MovieRepoInterface mModel;
    private MovieListContract.View mView;
    public boolean fromTop;

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

                }
            }
        });
    }

    @Override
    public void showFirst() {
        if (fromTop) {
            onMovieSelected(mModel.getCurrentMovieList().get(0));
        }
    }

    public void onSortByTapped() {
        String sortPref = mModel.getSortPref();
        mView.inflateSortOptionsMenu(sortPref);
    }


    @Override
    public void onSortChanged(MenuItem item) {
        fromTop = true;
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
                pref =  "favorite";
        }
        mModel.changeSortPreference(pref);
        showMovieList();
    }

    @Override
    public void onMovieSelected(Movie selectedMovie) {
        fromTop = false;
        mModel.setSelectedMovie(selectedMovie);
        mView.showMovieDetailsUI();
    }


}