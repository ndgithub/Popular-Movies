package com.example.android.popularmovies.moviedetails;

import com.example.android.popularmovies.data.CastMember;
import com.example.android.popularmovies.data.ModelInterface;
import com.example.android.popularmovies.data.Movie;
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

/**
 * Created by Nicky on 11/25/17.
 */

public class MovieDetailsPresenterTest {

    @Mock
    private MovieDetailsContract.View mView;
    @Mock
    private ModelInterface mModel;

    @Captor
    ArgumentCaptor<ModelInterface.CastLoadedCallback> mCastLoadedCallbackCaptor;
    @Captor
    ArgumentCaptor<ModelInterface.ReviewsLoadedCallback> mReviewsLoadedCallbackCaptor;
    @Captor
    ArgumentCaptor<ModelInterface.TrailersLoadedCallback> mTrailersLoadedCallbackCaptor;

    @Captor
    ArgumentCaptor<ModelInterface.addFavoritesCallback> mAddFavoritesCallbackCaptor;
    @Captor
    ArgumentCaptor<ModelInterface.removeFavoritesCallback> mRemoveFavoritesCallbackCaptor;


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
        //Call method under test
        mPresenter.start(mMovie);

        //Verify view methods get called
        verify(mView).showActivityTitle(mMovie.getTitle());
        verify(mView).showTitle(mMovie.getTitle());
        verify(mView).showRating(mMovie.getRating());
        verify(mView).showBackdrop(mMovie.getBackdropPath());
        verify(mView).showPoster(mMovie.getPosterPath());
        verify(mView).showOverview(mMovie.getOverview());
        verify(mView).showDate(mMovie.getDate());

        verify(mModel).getCast(eq(mMovie), mCastLoadedCallbackCaptor.capture());
        mCastLoadedCallbackCaptor.getValue().onCastLoaded(castList);
        verify(mView).showCastList(castList);
        //If there is an error
        mCastLoadedCallbackCaptor.getValue().onErrorLoadingCast();
        verify(mView).notifyErrorLoadingCast();

        verify(mModel).getTrailers(eq(mMovie), mTrailersLoadedCallbackCaptor.capture());
        mTrailersLoadedCallbackCaptor.getValue().onVideosLoaded(trailerList);
        verify(mView).showTrailersList(trailerList);
        //If there is an error
        mTrailersLoadedCallbackCaptor.getValue().onErrorLoadingVideos();
        verify(mView).notifyErrorLoadingTrailers();


        verify(mModel).getReviews(eq(mMovie), mReviewsLoadedCallbackCaptor.capture());
        mReviewsLoadedCallbackCaptor.getValue().onReviewsLoaded(reviewList);
        verify(mView).showReviewList(reviewList);
        //If there is an error
        mReviewsLoadedCallbackCaptor.getValue().onErrorLoadingReviews();
        verify(mView).notifyErrorLoadingReviews();
    }

    @Test
    public void isFavorite_test() throws Exception {
        //Call the method under test
        mPresenter.isFavorite(mMovie);

        //Verify
        verify(mModel).isFavorite(mMovie);
    }

    @Test
    public void onGoToFavorites() throws Exception {
        mPresenter.onGoToFavorites();
        verify(mModel).updateSortToFavorites();
        verify(mView).showFavorites();
    }

    @Test
    public void onFavoriteButtonClicked_test() throws Exception {
        mPresenter.onFavoriteButtonClicked(true, mMovie);
        verify(mModel).removeFromFavoritesDb(eq(mMovie), mRemoveFavoritesCallbackCaptor.capture());
        //Removing from favorite success
        mRemoveFavoritesCallbackCaptor.getValue().onSuccessRemovingFav();
        verify(mView).updateFavorite(false);
        verify(mView, times(0)).notifyErrorRemovingFav();

        reset(mView);
        //Removing from favorite failure
        mRemoveFavoritesCallbackCaptor.getValue().onErrorRemovingFav();
        verify(mView).notifyErrorRemovingFav();
        verify(mView, times(0)).updateFavorite(anyBoolean());


        //Add to favorites success
        mPresenter.onFavoriteButtonClicked(false, mMovie);
        verify(mModel).addToFavoritesDb(eq(mMovie), mAddFavoritesCallbackCaptor.capture());
        mAddFavoritesCallbackCaptor.getValue().errorAddingToFav();
        verify(mView).notifyErrorAddingFav();
        verify(mView, times(0)).updateFavorite(anyBoolean());
        reset(mView);
        mAddFavoritesCallbackCaptor.getValue().successAddingToFav();
        verify(mView).updateFavorite(true);
        verify(mView, times(0)).notifyErrorAddingFav();

    }

    @Test
    public void onTrailerClicked_test() throws Exception {
        mPresenter.onTrailerClicked(mVideo);
        verify(mView).showTrailer(mVideo.getKey());

    }


}
