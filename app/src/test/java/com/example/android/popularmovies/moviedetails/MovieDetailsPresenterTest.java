package com.example.android.popularmovies.moviedetails;

import com.example.android.popularmovies.data.CastMember;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieRepoInterface;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Video;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by Nicky on 11/25/17.
 */

public class MovieDetailsPresenterTest {

    @Mock
    private MovieDetailsContract.View mView;
    @Mock
    private MovieRepoInterface mModel;

    @Captor
    ArgumentCaptor<MovieRepoInterface.CastLoadedCallback> mCastLoadedCallbackCaptor;
    @Captor
    ArgumentCaptor<MovieRepoInterface.ReviewsLoadedCallback> mReviewsLoadedCallbackCaptor;
    @Captor
    ArgumentCaptor<MovieRepoInterface.TrailersLoadedCallback> mTrailersLoadedCallbackCaptor;
    @Captor
    ArgumentCaptor<MovieRepoInterface.addFavoritesCallback> mAddFavoritesCallbackCaptor;
    @Captor
    ArgumentCaptor<MovieRepoInterface.removeFavoritesCallback> mRemoveFavoritesCallbackCaptor;

    private Movie mMovie;
    private MovieDetailsPresenter mPresenter;
    private Video mVideo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mMovie = new Movie("id", "backdropPath", "overview", "posterPath", "title", "rating", "2017-09-05");
        mPresenter = new MovieDetailsPresenter(mModel, mView);
        mVideo = new Video("key","title");
    }

    @Test
    public void start_test() {
        //Given movie and cast list
        ArrayList<CastMember> castList = new ArrayList<>();
        castList.add(new CastMember("actor_name", "character_name", "pic_path"));
        ArrayList<Review> reviewList = new ArrayList<>();
        reviewList.add(new Review("author", "content", "path"));
        ArrayList<Video> trailerList = new ArrayList<>();
        trailerList.add(new Video("key", "title"));
        when(mModel.getSelectedMovie()).thenReturn(mMovie);
        //Call method under test

        mPresenter.start();

        //Verify view methods get called
        verify(mView).showActivityTitle(mMovie.getTitle());
        verify(mView).showTitle(mMovie.getTitle());
        verify(mView).showRating(mMovie.getRating());
        verify(mView).showBackdrop(mMovie.getBackdropPath());
        verify(mView).showPoster(mMovie.getPosterPath());
        verify(mView).showOverview(mMovie.getOverview());
        verify(mView).showDate(mMovie.getDate());

        verify(mModel).getCast(mCastLoadedCallbackCaptor.capture());
        mCastLoadedCallbackCaptor.getValue().onCastLoaded(castList);
        verify(mView).showCastList(castList);
        //If there is an error
        mCastLoadedCallbackCaptor.getValue().onErrorLoadingCast();
        verify(mView).notifyUserErrorLoadingCast();

        verify(mModel).getTrailers(mTrailersLoadedCallbackCaptor.capture());
        mTrailersLoadedCallbackCaptor.getValue().onVideosLoaded(trailerList);
        verify(mView).showTrailersList(trailerList);
        //If there is an error
        mTrailersLoadedCallbackCaptor.getValue().onErrorLoadingVideos();
        verify(mView).notifyUserErrorLoadingTrailers();


        verify(mModel).getReviews(mReviewsLoadedCallbackCaptor.capture());
        mReviewsLoadedCallbackCaptor.getValue().onReviewsLoaded(reviewList);
        verify(mView).showReviewList(reviewList);
        //If there is an error
        mReviewsLoadedCallbackCaptor.getValue().onErrorLoadingReviews();
        verify(mView).notifyUserErrorLoadingReviews();


        //Verify that no view methods are called if there is no current movie (no favorites)
        reset(mView);
        when(mModel.getSelectedMovie()).thenReturn(null);
        verifyZeroInteractions(mView);

    }

    @Test
    public void isFavorite_test() throws Exception {
        //Call the method under test
        mPresenter.isFavorite();

        //Verify
        verify(mModel).isFavorite();
    }

    @Test
    public void onGoToFavorites_test() throws Exception {
        mPresenter.onGoToFavorites();
        verify(mModel).changeSortPreference("favorite");
        verify(mModel).setSelectedMoviePos(0);
        verify(mView).goToFavorites();
    }

    @Test
    public void onFavoriteButtonClicked_test_removed_from_favorites() throws Exception {
        //Setup for already a favorite movie, and need to remove from favorites.
        when(mModel.isFavorite()).thenReturn(true);
        mPresenter.onFavoriteButtonClicked();
        //Verify model is told
        verify(mModel).removeFromFavorites(mRemoveFavoritesCallbackCaptor.capture());

        //Removing from favorite success callback
        when(mModel.isFavorite()).thenReturn(false);
        mRemoveFavoritesCallbackCaptor.getValue().onSuccessRemovingFav();
        verify(mView).updateFavButtonImage(false);
        verify(mView).notifyUserFavStatusChanged(false);

        //Removing from favorite error callback
        when(mModel.isFavorite()).thenReturn(false);
        mRemoveFavoritesCallbackCaptor.getValue().onErrorRemovingFav();
        verify(mView).notifyUserErrorRemovingFav();




    }
    @Test
    public void onFavoriteButtonClicked_test_added_tofavorites() throws Exception {
        //Setup for not currently a fav movie, and need to add to favorites.
        when(mModel.isFavorite()).thenReturn(false);
        mPresenter.onFavoriteButtonClicked();
        //Verify model is told
        verify(mModel).addToFavorites(mAddFavoritesCallbackCaptor.capture());

        //adding to favorites success callback
        when(mModel.isFavorite()).thenReturn(true);
        mAddFavoritesCallbackCaptor.getValue().successAddingToFav();
        verify(mView).updateFavButtonImage(true);
        verify(mView).notifyUserFavStatusChanged(true);

        // adding to favorites failure callback
        mAddFavoritesCallbackCaptor.getValue().errorAddingToFav();
        verify(mView).notifyUserErrorAddingFav();
    }

    @Test
    public void onTrailerClicked_test() throws Exception {
        mPresenter.onTrailerClicked(mVideo);
        verify(mView).showTrailer(mVideo.getKey());

    }


}
