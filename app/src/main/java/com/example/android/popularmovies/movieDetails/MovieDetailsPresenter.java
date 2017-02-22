package com.example.android.popularmovies.movieDetails;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;


import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.CastMember;
import com.example.android.popularmovies.data.MVPmodel;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Video;
import com.example.android.popularmovies.movieList.MainActivity;

import java.util.ArrayList;

import javax.sql.RowSetEvent;

public class MovieDetailsPresenter implements MovieDetailsContract.UserActionsListener {


    private Context mContext;
    private MovieDetailsContract.View mView;
    private MVPmodel mModel;

    public MovieDetailsPresenter(ContentResolver contentResolver, Context context, MovieDetailsContract.View view) {
        mContext = context;
        mView = view;
        mModel = new MVPmodel(contentResolver, context, this);
    }


    @Override
    public void onTrailerClicked(Video selectedVideo) {
        String vidKey = selectedVideo.getKey();
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + vidKey));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + vidKey));
        mView.showTrailer(appIntent, webIntent);


    }


    public void start(Movie selectedMovie) {
        mModel.getCastListAndUpdateUI(selectedMovie);
        mModel.getTrailersAndUpdateUI(selectedMovie);
        mModel.getReviewsAndUpdateUI(selectedMovie);
    }


    //Callback from model
    public void castListRecieved(ArrayList<CastMember> castList) {
        mView.showCastList(castList);
    }

    //Callback from model
    public void reviewListRecieved(ArrayList<Review> reviewList) {
        mView.showReviewList(reviewList);
    }

    //Callback from model
    public void trailerListRecieved(ArrayList<Video> videoList) {
        mView.showVideoList(videoList);
    }


    public boolean isFavorite(Movie selectedMovie) {
        return mModel.isFavorite(selectedMovie);
    }

    @Override
    public void onGoToFavorites() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putString("sort_by", "favorite");
        prefEditor.apply();
        MVPmodel.fromTop = true;
        Intent intent = new Intent(mContext, MainActivity.class);

        mView.showFavorites(intent);
    }

    //Callback from model
    public void onAddedToFavorites() {
        mView.updateFavorite(true);

    }

    //Callback from model
    public void onRemovedFromFavorites() {
        mView.updateFavorite(false);
    }

    public void favRemoveError() {
        mView.showAddError();
    }

    public void favAddError() {
        mView.showRemoveError();
    }

    @Override
    public void onFavoriteButtonClicked(boolean fav, Movie selectedMovie) {
        if (fav) {
            mModel.removeFromFavoritesDb(selectedMovie);
        } else {
            mModel.addToFavoritesDb(selectedMovie);
        }
    }

}
