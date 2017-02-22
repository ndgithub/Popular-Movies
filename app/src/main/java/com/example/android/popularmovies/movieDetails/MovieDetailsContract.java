package com.example.android.popularmovies.movieDetails;

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
        void showFavorites(Intent intent);
        void showCastList(ArrayList<CastMember> castList);
        void showReviewList(ArrayList<Review> reviewList);
        void showVideoList(ArrayList<Video> videoList);
        void showTrailer(Intent appIntent, Intent webIntent);
        void updateFavorite(boolean fav);
        void showAddError();
        void showRemoveError();

    }

    interface UserActionsListener {

        void onFavoriteButtonClicked(boolean fav, Movie selectedMovie);
        void onTrailerClicked(Video selectedVideo);
        void onGoToFavorites();

    }
}
