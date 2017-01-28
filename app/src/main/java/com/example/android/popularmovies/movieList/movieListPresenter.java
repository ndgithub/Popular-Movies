package com.example.android.popularmovies.movieList;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MVPmodel;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.movieDetails.MovieDetailActivity;

import org.parceler.Parcels;

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
    ArrayList<Movie> movieList = new ArrayList<>();
    private GridView mGridView;
    MovieAdapter mMovieAdapter;


    private movieListPresenter(ContentResolver contentResolver, Context context, MovieListContract.View view, GridView gridView) {
        mContentResolver = contentResolver;
        mContext = context;
        mView = view;
        model = MVPmodel.getInstance(mContentResolver, mContext, this);
        mGridView = gridView;
    }

    public static movieListPresenter getInstance(ContentResolver contentResolver, Context context, MovieListContract.View view, GridView gridView) {
        if (mMoviesListPresenter == null) {
            mMoviesListPresenter = new movieListPresenter(contentResolver, context, view, gridView);
        }
        return mMoviesListPresenter;
    }


    @Override
    public void start() {
        mMovieAdapter = new MovieAdapter(mContext, movieList);
        mGridView.setAdapter(mMovieAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onMovieSelected(position);
            }
        });
        model.getMovieList();
    }

    public void onSortByTapped() {
        String sortPref = model.getSortPref();
        mView.inflateSortOptionsMenu(sortPref);

    }

    @Override
    public void listRecieved(ArrayList<Movie> movieList) {
        if (movieList != null) {
            mMovieAdapter.clear();
            mMovieAdapter.addAll(movieList);
            Log.v("***** - MainActivity", "showMovies, MovieList = " + movieList);
        } else {
            //Toast.makeText(mView, R.string.error_retrieving_movies, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSortChanged(MenuItem item) {
        model.changeSortPreference(item);
        model.getMovieList();

    }

    @Override
    public void onMovieSelected(int position) {
        Movie selectedMovie = (Movie) mMovieAdapter.getItem(position);
        Intent intent = new Intent(mContext, MovieDetailActivity.class);
        intent.putExtra("movie", Parcels.wrap(selectedMovie));
        mView.showMovieDetailsUI(intent);
    }


}
