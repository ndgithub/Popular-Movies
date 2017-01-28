package com.example.android.popularmovies.movieDetails;

/**
 * Created by Nicky on 1/27/17.
 */

public interface MovieDetailsContract {

    interface View {

    }

    interface UserActionsListener {

        public void onReviewClicked();
        public void onFavorited();



    }
}
