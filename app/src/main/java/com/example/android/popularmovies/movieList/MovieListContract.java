package com.example.android.popularmovies.movieList;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.android.popularmovies.data.Movie;

import java.util.ArrayList;

public interface MovieListContract {

    interface View {
        void showMovieDetailsUI(Bundle movieBundle);
        void inflateSortOptionsMenu(String sortPref);
        void showMovieList(ArrayList<Movie> list);
        Movie getSelectedMovie(int position);
    }

    interface UserActionsListener {
        void onSortChanged(MenuItem item);
        void onMovieSelected(int position);
        void start();
        void listRecieved(ArrayList<Movie> movieList);
    }
}
