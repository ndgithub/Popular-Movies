package com.example.android.popularmovies.data.remote;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieRepoInterface;

/**
 * Created by Nicky on 12/1/17.
 */

public interface MovieServiceAPI {

    interface LoadMoviesCallback<T> {
        void onMoviesLoaded(T moviesList);
    }

    interface LoadCastCallback<T> {
        void onCastLoaded(T castList);
        void onCastError();
    }

    interface LoadTrailersCallback<T> {
        void onTrailersLoaded(T trailersList);
        void onTrailersError();
    }

    interface LoadReviewsCallback<T> {
        void onReviewsLoaded(T reviewList);
        void onReviewsError();
    }

    void getMovieList(String sortPref, MovieServiceAPI.LoadMoviesCallback callback);

    void getCast(Movie selectedMovie, MovieServiceAPI.LoadCastCallback callback);

    void getTrailers(Movie selectedMovie, MovieServiceAPI.LoadTrailersCallback callback);

    void getReviews(Movie selectedMovie, MovieServiceAPI.LoadReviewsCallback callback);

}
