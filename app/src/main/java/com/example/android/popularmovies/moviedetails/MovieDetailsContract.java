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

        void showActivityTitle(String title);

        void showTitle(String title);

        void showBackdrop(String path);

        void showPoster(String path);

        void showRating(String rating);

        void showOverview(String overview);

        void showDate(String date);

        void goToFavorites();

        void showCastList(ArrayList<CastMember> castList);

        void showReviewList(ArrayList<Review> reviewList);

        void showTrailersList(ArrayList<Video> trailersList);

        void showTrailer(String videoKey);

        void notifyUserErrorRemovingFav();

        void notifyUserErrorAddingFav();

        void notifyUserErrorLoadingCast();

        void notifyUserErrorLoadingReviews();

        void notifyUserErrorLoadingTrailers();

        void updateFavButtonImage(boolean isFavorite);

        void notifyUserFavStatusChanged(boolean isFavorite);
    }

    interface UserActionsListener {
        void start();
        void onFavoriteButtonClicked();
        void onTrailerClicked(Video selectedVideo);
        void onGoToFavorites();
        boolean isFavorite();
    }
}
