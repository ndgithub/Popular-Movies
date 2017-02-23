package com.example.android.popularmovies.movielist;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.android.popularmovies.data.Movie;

import java.util.ArrayList;

public interface MovieListContract {

    interface View {
        void showMovieDetailsUI(Bundle movieBundle);
        void inflateSortOptionsMenu(String sortPref);
        void showMovieList(ArrayList<Movie> list);
    }

    interface UserActionsListener {
        void onSortChanged(MenuItem item);
        void onMovieSelected(int position,ArrayList<Movie> movieList);
        void start();
        void listRecieved(ArrayList<Movie> movieList);
        void showFirst(ArrayList<Movie> movieList);
    }
}
