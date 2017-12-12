package com.example.android.popularmovies.movielist;

import android.view.MenuItem;

import com.example.android.popularmovies.data.Movie;

import java.util.ArrayList;

public interface MovieListContract {

    interface View {
        void showMovieDetailsUI();
        void inflateSortOptionsMenu(String sortPref);
        void showMovieList(ArrayList<Movie> list);
        void onSortChanged();
    }

    interface UserActionsListener {
        void onSortChanged(MenuItem item);
        void onMovieSelected(int position);
        void start();
    }
}
