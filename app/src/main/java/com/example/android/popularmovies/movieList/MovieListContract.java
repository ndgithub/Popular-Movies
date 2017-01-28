package com.example.android.popularmovies.movieList;

import android.view.MenuItem;

import com.example.android.popularmovies.data.Movie;

import java.util.ArrayList;

/**
 * Created by Nicky on 1/20/17.
 */

public interface MovieListContract {

    interface View {
        void  showMovies(ArrayList<Movie> movieList);
        void showMovieDetailsUI(int position);
        void inflateSortOptionsMenu(String sortPref);
    }

    interface UserActionsListener {
        void onSortChanged(MenuItem item);
        void onMovieSelected(int position);
        void start();
        void listRecieved(ArrayList<Movie> movieList);
    }
}
