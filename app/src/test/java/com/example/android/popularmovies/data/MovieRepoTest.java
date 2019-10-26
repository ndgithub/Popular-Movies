package com.example.android.popularmovies.data;

import com.example.android.popularmovies.data.remote.MovieServiceAPI;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ndesai on 12/16/17.
 */

public class MovieRepoTest {

    @Mock
    private MovieServiceAPI mMovieServiceAPI;

    @Mock
    private UserPrefInterface mUserPref;

    @Mock
    private MovieRepoInterface.InitMoviesCallback mInitMoviesCallback;

    @Mock
    private MovieRepoInterface.LoadMoviesCallback mLoadListCallback;
    @Captor
    private ArgumentCaptor<MovieServiceAPI.LoadMoviesCallback> mMoviesCallbackCaptor;


    @Mock
    private MovieRepoInterface.TrailersLoadedCallback mTrailersLoadedCallback;
    @Captor
    private ArgumentCaptor<MovieServiceAPI.LoadTrailersCallback> mTrailersCallbackCaptor;

    @Mock
    private MovieRepoInterface.CastLoadedCallback mCastLoadedCallback;
    @Captor
    private ArgumentCaptor<MovieServiceAPI.LoadCastCallback> mCastCallbackCaptor;

    @Mock
    private MovieRepoInterface.ReviewsLoadedCallback mReviewsLoadedCallback;
    @Captor
    private ArgumentCaptor<MovieServiceAPI.LoadReviewsCallback> mReviewsCallbackCaptor;

    @Mock
    private MovieRepoInterface.addFavoritesCallback mAddFavCallback;
    @Captor
    private ArgumentCaptor<UserPrefInterface.AddFavoriteCallback> mAddFavCallbackCaptor;

    @Mock
    private MovieRepoInterface.removeFavoritesCallback mRemoveFavCallback;
    @Captor
    private ArgumentCaptor<UserPrefInterface.RemoveFavoriteCallback> mRemoveFavCallbackCaptor;


    private MovieRepo mMovieRepo_testSub;
    private Movie mCurrentMovie;

    @Before
    public void testSetup() {
        MockitoAnnotations.initMocks(this);
        mMovieRepo_testSub = new MovieRepo(mUserPref, mMovieServiceAPI);

        // Start each test with a currently loaded movie list and position
        ArrayList<Movie> movieList = new ArrayList<>();
        mCurrentMovie = new Movie("346364", "/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg",
                "overview", "/tcheoA2nPATCm2vvXw2hVQoaEFD.jpg", "It",
                "7.3", "2017-09-05");
        movieList.add(mCurrentMovie);
        mMovieRepo_testSub.setCurrentMovieList(movieList);
        mMovieRepo_testSub.setCurrentMoviePos(0);
    }

    @Test
    public void initializeMovieList_test_WhenSortPrefIsFav() {
        //Setup
        mMovieRepo_testSub.setCurrentMovieList(null);
        ArrayList<Movie> movieList = new ArrayList<>();
        movieList.add(new Movie("346364", "/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg", "overview", "/tcheoA2nPATCm2vvXw2hVQoaEFD.jpg", "It", "7.3", "2017-09-05"));

        when(mUserPref.getSortPref()).thenReturn("favorite");
        when(mUserPref.getFavoritesList()).thenReturn(movieList);
        //Interact
        mMovieRepo_testSub.initializeMovieList(mInitMoviesCallback);
        //Verify
        verify(mUserPref).getFavoritesList();
        verify(mInitMoviesCallback).onMoviesLoaded();
        assertEquals(mMovieRepo_testSub.mMovieList, movieList);

    }

    @Test
    public void initializeMovieList_test_WhenSortPrefIsPopOrTop() {
        //Setup
        mMovieRepo_testSub.mMovieList = null;
        ArrayList<Movie> movieList = new ArrayList<>();
        movieList.add(new Movie("346364", "/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg", "overview", "/tcheoA2nPATCm2vvXw2hVQoaEFD.jpg", "It", "7.3", "2017-09-05"));

        //Setup for sort preference is popular or favorite
        when(mUserPref.getSortPref()).thenReturn("popular");
        //Interact
        mMovieRepo_testSub.initializeMovieList(mInitMoviesCallback);

        //Verify
        verify(mMovieServiceAPI).getMovieList(eq("popular"), mMoviesCallbackCaptor.capture());
        mMoviesCallbackCaptor.getValue().onMoviesLoaded(movieList);
        verify(mInitMoviesCallback).onMoviesLoaded();
        assertEquals(mMovieRepo_testSub.mMovieList, movieList);
    }


    @Test
    public void loadMovieList_test_whenSortPrefIsFavorite() {
        ArrayList<Movie> movieList = new ArrayList<>();
        movieList.add(new Movie("346364", "/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg", "overview", "/tcheoA2nPATCm2vvXw2hVQoaEFD.jpg", "It", "7.3", "2017-09-05"));

        //Setup for sort preference is popular or favorite
        when(mUserPref.getSortPref()).thenReturn("favorite");
        when(mUserPref.getFavoritesList()).thenReturn(movieList);


        //Interact
        mMovieRepo_testSub.loadMovieList(mLoadListCallback);

        //Verify
        verify(mUserPref).getFavoritesList();
        verify(mLoadListCallback).onMoviesLoaded(movieList);
        assertEquals(mMovieRepo_testSub.mMovieList, movieList);
    }

    @Test
    public void loadMovieList_test_whenSortPrefIsPopOrTop() {
        //Setup for sort preference is popular or favorite
        ArrayList<Movie> movieList = new ArrayList<>();
        movieList.add(new Movie("346364", "/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg", "overview", "/tcheoA2nPATCm2vvXw2hVQoaEFD.jpg", "It", "7.3", "2017-09-05"));
        when(mUserPref.getSortPref()).thenReturn("popular");

        //Interact
        mMovieRepo_testSub.loadMovieList(mLoadListCallback);

        //Verify
        verify(mMovieServiceAPI).getMovieList(eq("popular"), mMoviesCallbackCaptor.capture());
        mMoviesCallbackCaptor.getValue().onMoviesLoaded(movieList);

        assertEquals(mMovieRepo_testSub.mMovieList, movieList);
        verify(mLoadListCallback).onMoviesLoaded(movieList);

    }

    @Test
    public void getSortPref_test() {
        //Interact
        mMovieRepo_testSub.getSortPref();

        //Verify
        verify(mUserPref).getSortPref();
    }

    @Test
    public void getTrailers_test() {
        //Setup
        ArrayList<Video> trailerList = new ArrayList<>();
        trailerList.add(new Video("key", "video title"));


        //Interact
        mMovieRepo_testSub.getTrailers(mTrailersLoadedCallback);

        //Verify
        verify(mMovieServiceAPI).getTrailers(eq(mCurrentMovie), mTrailersCallbackCaptor.capture());

        //If Success
        mTrailersCallbackCaptor.getValue().onTrailersLoaded(trailerList);
        verify(mTrailersLoadedCallback).onVideosLoaded(trailerList);

        //If Error
        mTrailersCallbackCaptor.getValue().onTrailersError();
        verify(mTrailersLoadedCallback).onErrorLoadingVideos();
    }


    @Test
    public void getCast_test() {
        //Setup
        ArrayList<CastMember> castList = new ArrayList<>();
        castList.add(new CastMember("Real Name", "Character Name", "pic path"));

        //Interact
        mMovieRepo_testSub.getCast(mCastLoadedCallback);

        //Verify
        verify(mMovieServiceAPI).getCast(eq(mCurrentMovie), mCastCallbackCaptor.capture());
        //On Success
        mCastCallbackCaptor.getValue().onCastLoaded(castList);
        verify(mCastLoadedCallback).onCastLoaded(castList);
        //On Error
        mCastCallbackCaptor.getValue().onCastError();
        mCastLoadedCallback.onErrorLoadingCast();
    }

    @Test
    public void getReviews_test() {
        //Setup
        ArrayList<Review> reviewList = new ArrayList<>();
        reviewList.add(new Review("author", "content", "url"));

        //Interact
        mMovieRepo_testSub.getReviews(mReviewsLoadedCallback);

        //Verify
        verify(mMovieServiceAPI).getReviews(eq(mCurrentMovie), mReviewsCallbackCaptor.capture());
        //On Success
        mReviewsCallbackCaptor.getValue().onReviewsLoaded(reviewList);
        mReviewsLoadedCallback.onReviewsLoaded(reviewList);
        //On Error
        mReviewsCallbackCaptor.getValue().onReviewsError();
        mReviewsLoadedCallback.onErrorLoadingReviews();
    }

    @Test
    public void addToFavorites_test() {
        //No Setup Required

        //Interact
        mMovieRepo_testSub.addToFavorites(mAddFavCallback);

        //Verify
        verify(mUserPref).addToFavorites(eq(mCurrentMovie), mAddFavCallbackCaptor.capture());
        //On Success
        mAddFavCallbackCaptor.getValue().addSuccess();
        verify(mAddFavCallback).onSuccessAddingToFav();
        //On Error
        mAddFavCallbackCaptor.getValue().addError();
        verify(mAddFavCallback).onErrorAddingToFav();

    }

    @Test
    public void removeFromFavorites() {
        //No Setup Required

        //Interact
        mMovieRepo_testSub.removeFromFavorites(mRemoveFavCallback);

        //Verify
        verify(mUserPref).removeFromFavorites(eq(mCurrentMovie),mRemoveFavCallbackCaptor.capture());
        //On Success
        mRemoveFavCallbackCaptor.getValue().removeSuccess();
        verify(mRemoveFavCallback).onSuccessRemovingFav();
        //On Error
        mRemoveFavCallbackCaptor.getValue().removeError();
        verify(mRemoveFavCallback).onErrorRemovingFav();


    }


}
