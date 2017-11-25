package com.example.android.popularmovies.movielist;


import android.view.MenuItem;

import com.example.android.popularmovies.data.MVPmodel;
import com.example.android.popularmovies.data.ModelInterface;
import com.example.android.popularmovies.data.Movie;

import java.util.ArrayList;

public class MovieListPresenter implements MovieListContract.UserActionsListener {

    private ModelInterface mModel;
    private MovieListContract.View mView;

    public MovieListPresenter(ModelInterface model, MovieListContract.View view) {
        mView = view;
        mModel = model;
    }

    @Override
    public void start() {
        showMovieList();
    }

    public void showMovieList() {
        mModel.getMovieList(new ModelInterface.LoadMoviesCallback() {
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
        if (MVPmodel.fromTop) {
            onMovieSelected(0);
        }
    }

    void onSortByTapped() {
        String sortPref = mModel.getSortPref();
        mView.inflateSortOptionsMenu(sortPref);
    }


    @Override
    public void onSortChanged(MenuItem item) {
        MVPmodel.fromTop = true;
        mModel.changeSortPreference(item);
        showMovieList();
    }

    @Override
    public void onMovieSelected(int position) {
        MVPmodel.fromTop = false;
        mView.showMovieDetailsUI(position);
    }
}