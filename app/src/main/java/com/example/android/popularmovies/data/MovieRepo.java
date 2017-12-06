package com.example.android.popularmovies.data;

import android.util.Log;

import com.example.android.popularmovies.data.remote.MovieServiceAPI;

import java.util.ArrayList;

/**
 * Created by Nicky on 1/23/17.
 */

public class MovieRepo implements MovieRepoInterface {

    private MovieServiceAPI mMovieServiceAPI;
    private UserPrefInterface mUserPref;
    private Movie mSelectedMovie;
    private ArrayList<Movie> mMovieList;

    public MovieRepo(UserPrefInterface userPref, MovieServiceAPI movieServiceAPI) {
        mUserPref = userPref;
        mMovieServiceAPI = movieServiceAPI;
    }

    public void changeSortPreference(String sortPref) {
        mUserPref.changeSortPreference(sortPref);
    }

    public void getMovieList(final LoadMoviesCallback callback) {
        String sortPref = mUserPref.getSortPref();
        if (sortPref.equals("favorite")) {
            mMovieList = mUserPref.getFavoritesList();
            callback.onMoviesLoaded(mMovieList);
        } else {
            mMovieServiceAPI.getMovieList(sortPref, new LoadMoviesCallback<ArrayList<Movie>>() {
                @Override
                public void onMoviesLoaded(ArrayList movieList) {
                    mMovieList = movieList;
                    callback.onMoviesLoaded(mMovieList);
                }
            });

        }

    }

    public String getSortPref() {
        return mUserPref.getSortPref();

    }

    public void getTrailers(final MovieRepoInterface.TrailersLoadedCallback callback) {

        mMovieServiceAPI.getTrailers(mSelectedMovie, new MovieServiceAPI.LoadTrailersCallback() {
            @Override
            public void onTrailersLoaded(Object trailersList) {
                callback.onVideosLoaded(trailersList);
            }

            @Override
            public void onTrailersError() {
                callback.onErrorLoadingVideos();
            }
        });


    }

    public void getCast(final MovieRepoInterface.CastLoadedCallback callback) {
        mMovieServiceAPI.getCast(mSelectedMovie, new MovieServiceAPI.LoadCastCallback<ArrayList<CastMember>>() {
            @Override
            public void onCastLoaded(ArrayList<CastMember> castList) {
                callback.onCastLoaded(castList);
            }

            @Override
            public void onCastError() {
                callback.onErrorLoadingCast();
            }

        });


    }

    public void getReviews(final MovieRepoInterface.ReviewsLoadedCallback callback) {

        mMovieServiceAPI.getReviews(mSelectedMovie, new MovieServiceAPI.LoadReviewsCallback() {
            @Override
            public void onReviewsLoaded(Object reviewList) {
                callback.onReviewsLoaded(reviewList);
            }

            @Override
            public void onReviewsError() {
                callback.onErrorLoadingReviews();
            }
        });

    }

    @Override
    public void setSelectedMovie(Movie selectedMovie) {
        mSelectedMovie = selectedMovie;
    }

    @Override
    public Movie getSelectedMovie() {
        return mSelectedMovie;
    }


    public boolean isFavorite() {
        return mUserPref.isFavorite(mSelectedMovie);
    }

    public void addToFavorites(final MovieRepoInterface.addFavoritesCallback callback) {

        mUserPref.addToFavorites(mSelectedMovie, new UserPrefInterface.AddFavoriteCallback() {
            @Override
            public void addSuccess() {
                callback.successAddingToFav();
            }

            @Override
            public void addError() {
                callback.errorAddingToFav();
            }
        });

    }

    public void removeFromFavorites(final MovieRepoInterface.removeFavoritesCallback callback) {
        mUserPref.removeFromFavorites(mSelectedMovie, new UserPrefInterface.RemoveFavoriteCallback() {
            @Override
            public void removeSuccess() {
                callback.onSuccessRemovingFav();
            }

            @Override
            public void removeError() {
                callback.onErrorRemovingFav();
            }
        });

    }

    public ArrayList<Movie> getCurrentMovieList() {
        return mMovieList;
    }


}