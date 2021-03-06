package com.example.android.popularmovies.data;

import android.util.Log;

import com.example.android.popularmovies.data.remote.MovieServiceAPI;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Nicky on 1/23/17.
 */

public class MovieRepo implements MovieRepoInterface {

    private MovieServiceAPI mMovieServiceAPI;
    private UserPrefInterface mUserPref;

    public ArrayList<Movie> mMovieList; //public for testing
    public Integer mCurrentMoviePos = 0; //public for testing

    public MovieRepo(UserPrefInterface userPref, MovieServiceAPI movieServiceAPI) {
        mUserPref = userPref;
        mMovieServiceAPI = movieServiceAPI;

    }

    public void changeSortPreference(String sortPref) {
        mUserPref.changeSortPreference(sortPref);
    }

    public void initializeMovieList(final InitMoviesCallback callback) {
        if (mMovieList == null) {
            String sortPref = mUserPref.getSortPref();
            if (sortPref.equals("favorite")) {
                mMovieList = mUserPref.getFavoritesList();
                callback.onMoviesLoaded();
            } else {
                mMovieServiceAPI.getMovieList(sortPref,
                        new MovieServiceAPI.LoadMoviesCallback<ArrayList<Movie>>() {
                            @Override
                            public void onMoviesLoaded(ArrayList movieList) {
                                mMovieList = movieList;
                                callback.onMoviesLoaded();

                            }
                        });
            }
        }

    }

    public ArrayList<Movie> returnCurrentMovieList() {
        return mMovieList;
    }

    public void loadMovieList(final LoadMoviesCallback callback) {
        String sortPref = mUserPref.getSortPref();
        if (sortPref.equals("favorite")) {
            mMovieList = mUserPref.getFavoritesList();
            callback.onMoviesLoaded(mMovieList);
        } else {
            mMovieServiceAPI.getMovieList(sortPref,
                    new MovieServiceAPI.LoadMoviesCallback<ArrayList<Movie>>() {
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

        mMovieServiceAPI.getTrailers(getSelectedMovie(),
                new MovieServiceAPI.LoadTrailersCallback() {
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
        mMovieServiceAPI.getCast(getSelectedMovie(), new MovieServiceAPI.LoadCastCallback<ArrayList<CastMember>>() {
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

        mMovieServiceAPI.getReviews(getSelectedMovie(), new MovieServiceAPI.LoadReviewsCallback() {
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
    public void setSelectedMoviePos(int position) {
        mCurrentMoviePos = position;
    }

    @Override
    public Movie getSelectedMovie() {
        if (!mMovieList.isEmpty()) {
            return mMovieList.get(mCurrentMoviePos);
        }
        return null;
    }


    public boolean isFavorite() {
        return mUserPref.isFavorite(getSelectedMovie());
    }

    public void addToFavorites(final MovieRepoInterface.addFavoritesCallback callback) {

        mUserPref.addToFavorites(getSelectedMovie(), new UserPrefInterface.AddFavoriteCallback() {
            @Override
            public void addSuccess() {
                callback.onSuccessAddingToFav();
            }

            @Override
            public void addError() {
                callback.onErrorAddingToFav();
            }
        });

    }

    public void removeFromFavorites(final MovieRepoInterface.removeFavoritesCallback callback) {
        mUserPref.removeFromFavorites(getSelectedMovie(), new UserPrefInterface.RemoveFavoriteCallback() {
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

    //Used for testing
    public void setCurrentMovieList(ArrayList<Movie> movieList) {
         mMovieList = movieList;
    }

    //Used for testing
    public void setCurrentMoviePos(int moviePos) {
         mCurrentMoviePos = moviePos;
    }


}