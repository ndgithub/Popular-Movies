package com.example.android.popularmovies.movieList;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import android.widget.GridView;

import com.example.android.popularmovies.data.MVPmodel;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.movieDetails.MovieDetailActivity;
import com.example.android.popularmovies.utils.ActivityUtils;

import org.parceler.Parcels;

import java.util.ArrayList;

public class movieListPresenter implements MovieListContract.UserActionsListener {

    private MVPmodel model;
    private MovieListContract.View mView;
    private Context mContext;
    private ArrayList<Movie> mMovieList = new ArrayList<>();
    private GridView mGridView;
    private MovieAdapter mMovieAdapter;

    public movieListPresenter(ContentResolver contentResolver, Context context, MovieListContract.View view, GridView gridView) {
        mContext = context;
        mView = view;
        model = new MVPmodel(contentResolver,mContext,this);
        mGridView = gridView;
    }

    @Override
    public void start() {
        model.getMovieList();
    }

    @Override
    public void listRecieved(ArrayList<Movie> movieList) {
        if (movieList != null) {
            mView.showMovieList(movieList);
        }
    }

    public void onSortByTapped() {
        String sortPref = model.getSortPref();
        mView.inflateSortOptionsMenu(sortPref);
    }


    @Override
    public void onSortChanged(MenuItem item) {
        MVPmodel.fromTop = true;
        model.changeSortPreference(item);
        model.getMovieList();
    }

    @Override
    public void onMovieSelected(int position) {
        Movie selectedMovie = mView.getSelectedMovie(position);
        Intent intent = new Intent(mContext, MovieDetailActivity.class);
        Bundle elBunidi = new Bundle();
        elBunidi.putParcelable("movie",Parcels.wrap(selectedMovie));
        intent.putExtra("movi",elBunidi);
        MVPmodel.fromTop = false;
        mView.showMovieDetailsUI(elBunidi);
    }
}