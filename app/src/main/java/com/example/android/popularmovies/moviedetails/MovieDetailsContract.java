package com.example.android.popularmovies.moviedetails;

import android.content.Intent;

import com.example.android.popularmovies.data.CastMember;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Video;

import java.util.ArrayList;

/**
 * Created by Nicky on 1/27/17.
 */

public interface MovieDetailsContract {

    interface View {
        void showMainInfoCard();
        void showFavorites();
        void showCastList(ArrayList<CastMember> castList);
        void showReviewList(ArrayList<Review> reviewList);
        void showTrailersList(ArrayList<Video> trailersList);
        void showTrailer(Intent appIntent, Intent webIntent);
        void updateFavorite(boolean fav);
        void notifyErrorRemovingFav();
        void notifyErrorAddingFav();
        void notifyErrorLoadingCast();
        void notifyErrorLoadingReviews();
        void notifyErrorLoadingTrailers();
    }

    interface UserActionsListener {
        void start(Movie selectedMovie);
        void onFavoriteButtonClicked(boolean fav, Movie selectedMovie);
        void onTrailerClicked(Video selectedVideo);
        void onGoToFavorites();
        boolean isFavorite(Movie selectedMovie);
    }
}
