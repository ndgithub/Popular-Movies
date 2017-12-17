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
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ndesai on 12/16/17.
 */

public class MovieRepoTest {

    @Mock
    MovieServiceAPI mMovieServiceAPI;

    @Mock
    UserPrefInterface mUserPref;

    @Mock
    MovieRepoInterface.InitMoviesCallback mInitMoviesCallback;

    @Captor
    ArgumentCaptor<MovieServiceAPI.LoadMoviesCallback> mLoadMoviesCallback;


    MovieRepo mMovieRepo;


    @Before
    public void testSetup() {
        MockitoAnnotations.initMocks(this);
        mMovieRepo = new MovieRepo(mUserPref, mMovieServiceAPI);

    }

    @Test
    public void initializeMovieList_test_WhenSortPrefIsFav() {
        //Setup
        mMovieRepo.mMovieList = null;
        //Setup for sort preference is favorite
        when(mUserPref.getSortPref()).thenReturn("favorite");
        //Interact
        mMovieRepo.initializeMovieList(mInitMoviesCallback);
        //Verify
        verify(mUserPref).getFavoritesList();
        verify(mInitMoviesCallback).onMoviesLoaded();


    }

    @Test
    public void initializeMovieList_test_WhenSortPrefIsPopOrTop() {
        //Setup
        mMovieRepo.mMovieList = null;
        ArrayList<Movie> movieList = new ArrayList<>();
        movieList.add(new Movie("346364", "/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg", "overview", "/tcheoA2nPATCm2vvXw2hVQoaEFD.jpg", "It", "7.3", "2017-09-05"));

        //Setup for sort preference is favorite
        when(mUserPref.getSortPref()).thenReturn("popular");
        //Interact
        mMovieRepo.initializeMovieList(mInitMoviesCallback);
        //Verify
        verify(mMovieServiceAPI).getMovieList(eq("popular"), mLoadMoviesCallback.capture());
        mLoadMoviesCallback.getValue().onMoviesLoaded(movieList);
        verify(mInitMoviesCallback).onMoviesLoaded();
        assertEquals(mMovieRepo.mMovieList,movieList);

    }
}
