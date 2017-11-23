package com.example.android.popularmovies.moviedetails;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.CastMember;
import com.example.android.popularmovies.data.MVPmodel;
import com.example.android.popularmovies.data.ModelInterface;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Video;
import com.example.android.popularmovies.movielist.MainActivity;

import java.util.ArrayList;

public class MovieDetailsPresenter implements MovieDetailsContract.UserActionsListener {

    private MovieDetailsContract.View mView;
    private ModelInterface mModel;

    public MovieDetailsPresenter(ModelInterface model, MovieDetailsContract.View view) {
        mView = view;
        mModel = model;
    }


    public void start(Movie selectedMovie) {
        loadUI(selectedMovie);
    }

    private void loadUI(Movie selectedMovie) {
        mView.showMainInfoCard();
        mModel.getCast(selectedMovie, new ModelInterface.CastLoadedCallback() {
            @Override
            public void onCastLoaded(ArrayList<CastMember> castList) {
                mView.showCastList(castList);
            }

            @Override
            public void errorLoadingCast() {
                mView.notifyErrorLoadingCast();
            }


        });
        mModel.getTrailers(selectedMovie, new ModelInterface.TrailersLoadedCallback() {
            @Override
            public void onVideosLoaded(ArrayList<Video> trailersList) {
                mView.showTrailersList(trailersList);
            }

            @Override
            public void errorLoadingVideos() {
                mView.notifyErrorLoadingTrailers();
            }
        });
        mModel.getReviews(selectedMovie, new ModelInterface.ReviewsLoadedCallback() {
            @Override
            public void onReviewsLoaded(ArrayList<Review> reviewList) {
                mView.showReviewList(reviewList);
            }

            @Override
            public void errorLoadingReviews() {
                mView.notifyErrorLoadingReviews();
            }
        });
    }


    public boolean isFavorite(Movie selectedMovie) {
        return mModel.isFavorite(selectedMovie);
    }

    @Override
    public void onGoToFavorites() {
        mModel.updateSortToFavorites();
        mView.showFavorites();
    }

    @Override
    public void onFavoriteButtonClicked(final boolean fav, Movie selectedMovie) {
        if (fav) {
            mModel.removeFromFavoritesDb(selectedMovie, new ModelInterface.removeFavoritesCallback() {
                @Override
                public void errorRemovingFav() {
                    mView.notifyErrorRemovingFav();
                }

                @Override
                public void successRemovingFav() {
                    mView.updateFavorite(!fav);
                }
            });
        } else {
            mModel.addToFavoritesDb(selectedMovie, new ModelInterface.addFavoritesCallback() {
                @Override
                public void errorAddingToFav() {
                    mView.notifyErrorAddingFav();
                }

                @Override
                public void successAddingToFav() {
                    mView.updateFavorite(!fav);
                }
            });
        }
    }

    @Override
    public void onTrailerClicked(Video selectedVideo) {
        String vidKey = selectedVideo.getKey();
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + vidKey));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + vidKey));
        mView.showTrailer(appIntent, webIntent);

    }


}
