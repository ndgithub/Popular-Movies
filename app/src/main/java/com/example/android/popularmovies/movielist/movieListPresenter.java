package com.example.android.popularmovies.movielist;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.android.popularmovies.data.MVPmodel;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.moviedetails.MovieDetailActivity;
import com.example.android.popularmovies.utils.ActivityUtils;

import org.parceler.Parcels;

import java.util.ArrayList;

public class MovieListPresenter implements MovieListContract.UserActionsListener {

    private MVPmodel mModel;
    private MovieListContract.View mView;
    private Context mContext;

    public MovieListPresenter(ContentResolver contentResolver, Context context, MovieListContract.View view) {
        mContext = context;
        mView = view;
        mModel = new MVPmodel(contentResolver,mContext,this);
    }

    @Override
    public void start() {
        mModel.getMovieList();
    }

    @Override
    public void listRecieved(ArrayList<Movie> movieList) {
        if (movieList != null) {
            mView.showMovieList(movieList);
        }
    }

    @Override
    public void showFirst(ArrayList<Movie> movieList) {
        if (ActivityUtils.isTwoPane(mContext) && MVPmodel.fromTop) {
            onMovieSelected(0,movieList);
        }
    }

    public void onSortByTapped() {
        String sortPref = mModel.getSortPref();
        mView.inflateSortOptionsMenu(sortPref);
    }


    @Override
    public void onSortChanged(MenuItem item) {
        MVPmodel.fromTop = true;
        mModel.changeSortPreference(item);
        mModel.getMovieList();
    }

    @Override
    public void onMovieSelected(int position,ArrayList<Movie> movieList) {
        Movie selectedMovie = movieList.get(position);
        Intent intent = new Intent(mContext, MovieDetailActivity.class);
        Bundle elBunidi = new Bundle();
        elBunidi.putParcelable("movie",Parcels.wrap(selectedMovie));
        intent.putExtra("movi",elBunidi);
        MVPmodel.fromTop = false;
        mView.showMovieDetailsUI(elBunidi);
    }
}