package com.example.android.popularmovies.data;

import android.view.MenuItem;

import java.util.ArrayList;

/**
 * Created by Nicky on 11/21/17.
 */

public interface MovieRepoInterface {

    interface LoadMoviesCallback<T> {
        void onMoviesLoaded(T movieList);
    }

    interface InitMoviesCallback {
        void onMoviesLoaded();
    }


    interface CastLoadedCallback<T> {
        void onCastLoaded(T cast);

        void onErrorLoadingCast();
    }

    interface ReviewsLoadedCallback<T> {
        void onReviewsLoaded(T reviews);

        void onErrorLoadingReviews();
    }

    interface TrailersLoadedCallback<T> {
        void onVideosLoaded(T trailers);

        void onErrorLoadingVideos();
    }

    interface addFavoritesCallback {

        void onSuccessAddingToFav();

        void onErrorAddingToFav();

    }

    interface removeFavoritesCallback {
        void onSuccessRemovingFav();

        void onErrorRemovingFav();


    }

    void initializeMovieList(InitMoviesCallback callback);

    void loadMovieList(LoadMoviesCallback callback);

    void getCast(MovieRepoInterface.CastLoadedCallback callback);

    void getTrailers(MovieRepoInterface.TrailersLoadedCallback callback);

    void getReviews(MovieRepoInterface.ReviewsLoadedCallback callback);

    void setSelectedMoviePos(int position);

    Movie getSelectedMovie();


    //----------------------- UserPrefStuff-----------------------//

    boolean isFavorite();

    void removeFromFavorites(removeFavoritesCallback callback);

    void addToFavorites(addFavoritesCallback callback);

    String getSortPref();

    void changeSortPreference(String sortPref);

}
