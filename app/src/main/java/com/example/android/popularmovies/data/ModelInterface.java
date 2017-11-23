package com.example.android.popularmovies.data;

import android.view.MenuItem;

import java.util.ArrayList;

/**
 * Created by Nicky on 11/21/17.
 */

public interface ModelInterface {

    interface LoadMoviesCallback {
        void onMoviesLoaded(ArrayList<Movie> movieList);
    }

    interface CastLoadedCallback {
        void onCastLoaded(ArrayList<CastMember> castList);
        void errorLoadingCast();
    }

    interface ReviewsLoadedCallback {
        void onReviewsLoaded(ArrayList<Review> reviewList);
        void errorLoadingReviews();
    }

    interface TrailersLoadedCallback {
        void onVideosLoaded(ArrayList<Video> reviewList);
        void errorLoadingVideos();
    }

    interface addFavoritesCallback {
        void errorAddingToFav();
        void successAddingToFav();
    }

    interface removeFavoritesCallback {
        void errorRemovingFav();
        void successRemovingFav();
    }

    String getSortPref();
    void changeSortPreference(MenuItem item);

    void getMovieList(LoadMoviesCallback callback);

    void updateSortToFavorites();

    void getCast(Movie selectedMovie, ModelInterface.CastLoadedCallback callback);
    void getTrailers(Movie selectedMovie, ModelInterface.TrailersLoadedCallback callback);
    void getReviews(Movie selectedMovie,ModelInterface.ReviewsLoadedCallback callback);
    boolean isFavorite(Movie selectedMovie);
    void removeFromFavoritesDb(Movie selectedMovie, removeFavoritesCallback callback);
    void addToFavoritesDb(Movie selectedMovie, addFavoritesCallback callback);

    //    void getMovie();
    //    void getFavorites();
    //    void saveMovie();


}
