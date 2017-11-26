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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        mView.showActivityTitle(selectedMovie.getTitle());
        mView.showTitle(selectedMovie.getTitle());
        mView.showRating(selectedMovie.getRating());
        mView.showBackdrop(selectedMovie.getBackdropPath());
        mView.showPoster(selectedMovie.getPosterPath());
        mView.showOverview(selectedMovie.getOverview());
        mView.showDate(selectedMovie.getDate());

        mModel.getCast(selectedMovie, new ModelInterface.CastLoadedCallback() {
            @Override
            public void onCastLoaded(ArrayList<CastMember> castList) {
                mView.showCastList(castList);
            }

            @Override
            public void onErrorLoadingCast() {
                mView.notifyErrorLoadingCast();
            }


        });

        mModel.getTrailers(selectedMovie, new ModelInterface.TrailersLoadedCallback() {
            @Override
            public void onVideosLoaded(ArrayList<Video> trailersList) {
                mView.showTrailersList(trailersList);
            }

            @Override
            public void onErrorLoadingVideos() {
                mView.notifyErrorLoadingTrailers();
            }
        });

        mModel.getReviews(selectedMovie, new ModelInterface.ReviewsLoadedCallback() {
            @Override
            public void onReviewsLoaded(ArrayList<Review> reviewList) {
                mView.showReviewList(reviewList);
            }

            @Override
            public void onErrorLoadingReviews() {
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
                public void onErrorRemovingFav() {
                    mView.notifyErrorRemovingFav();
                }

                @Override
                public void onSuccessRemovingFav() {
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
        mView.showTrailer(vidKey);

    }


}
