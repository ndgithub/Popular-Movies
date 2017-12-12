package com.example.android.popularmovies.moviedetails;

import com.example.android.popularmovies.data.CastMember;
import com.example.android.popularmovies.data.MovieRepoInterface;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Video;

import java.util.ArrayList;

public class MovieDetailsPresenter implements MovieDetailsContract.UserActionsListener {

    private MovieDetailsContract.View mView;
    private MovieRepoInterface mModel;

    public MovieDetailsPresenter(MovieRepoInterface model, MovieDetailsContract.View view) {
        mView = view;
        mModel = model;
    }


    public void start() {
        showMovieDetails();
    }

    public void showMovieDetails() {
        Movie selectedMovie = mModel.getSelectedMovie();
        if (selectedMovie != null) {
            mView.showActivityTitle(selectedMovie.getTitle());
            mView.showTitle(selectedMovie.getTitle());
            mView.showRating(selectedMovie.getRating());
            mView.showBackdrop(selectedMovie.getBackdropPath());
            mView.showPoster(selectedMovie.getPosterPath());
            mView.showOverview(selectedMovie.getOverview());
            mView.showDate(selectedMovie.getDate());
            mView.updateFavButtonImage(mModel.isFavorite());

            mModel.getCast(new MovieRepoInterface.CastLoadedCallback<ArrayList<CastMember>>() {
                @Override
                public void onCastLoaded(ArrayList<CastMember> castList) {
                    mView.showCastList(castList);
                }

                @Override
                public void onErrorLoadingCast() {
                    mView.notifyUserErrorLoadingCast();
                }


            });

            mModel.getTrailers(new MovieRepoInterface.TrailersLoadedCallback<ArrayList<Video>>() {
                @Override
                public void onVideosLoaded(ArrayList<Video> trailersList) {
                    mView.showTrailersList(trailersList);
                }

                @Override
                public void onErrorLoadingVideos() {
                    mView.notifyUserErrorLoadingTrailers();
                }
            });

            mModel.getReviews(new MovieRepoInterface.ReviewsLoadedCallback<ArrayList<Review>>() {
                @Override
                public void onReviewsLoaded(ArrayList<Review> reviewList) {
                    mView.showReviewList(reviewList);
                }

                @Override
                public void onErrorLoadingReviews() {
                    mView.notifyUserErrorLoadingReviews();
                }
            });
        }

    }


    public boolean isFavorite() {
        return mModel.isFavorite();
    }

    @Override
    public void onGoToFavorites() {
        mModel.changeSortPreference("favorite");
        mModel.setSelectedMoviePos(0);
        mView.goToFavorites();
    }

    @Override
    public void onFavoriteButtonClicked() {
        if (mModel.isFavorite()) {
            mModel.removeFromFavorites(new MovieRepoInterface.removeFavoritesCallback() {
                @Override
                public void onErrorRemovingFav() {
                    mView.notifyUserErrorRemovingFav();
                }

                @Override
                public void onSuccessRemovingFav() {
                    mView.updateFavButtonImage(mModel.isFavorite());
                    mView.notifyUserFavStatusChanged(mModel.isFavorite());

                }
            });
        } else {
            mModel.addToFavorites(new MovieRepoInterface.addFavoritesCallback() {
                @Override
                public void errorAddingToFav() {
                    mView.notifyUserErrorAddingFav();
                }

                @Override
                public void successAddingToFav() {
                    mView.updateFavButtonImage(mModel.isFavorite());
                    mView.notifyUserFavStatusChanged(mModel.isFavorite());
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
