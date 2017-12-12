package com.example.android.popularmovies.movielist;

import android.test.AndroidTestCase;
import android.view.MenuItem;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieRepo;
import com.example.android.popularmovies.data.MovieRepoInterface;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Nicky on 11/17/17.
 */

public class MovieListPresenterTest extends TestCase {

    @Mock
    MovieListContract.View mView;

    @Mock
    MovieRepoInterface mModel;

    @Captor
    ArgumentCaptor<MovieRepo.LoadMoviesCallback> mLoadMoviesCallbackCaptor;

    @Captor
    ArgumentCaptor<MenuItem> mMenuItemCaptor;

    @Mock
    MenuItem menuItem;

    private MovieListPresenter mPresenter;
    private static ArrayList<Movie> movieList;

    @Before
    public void setupPresenter() {
        MockitoAnnotations.initMocks(this);
        movieList = new ArrayList<>();
        movieList.add(new Movie("346364", "/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg", "overview", "/tcheoA2nPATCm2vvXw2hVQoaEFD.jpg", "It", "7.3", "2017-09-05"));
        mPresenter = new MovieListPresenter(mModel, mView);
    }

    @Test
    public void onSortChanged_test() {
        //Call method under test
        mPresenter.onSortChanged(menuItem);

        //Verify that model updates sharedPref
        verify(mModel).changeSortPreference(anyString());
    }


    @Test
    public void showMovieList_test() {
        mPresenter.showMovieList();
        verify(mModel).loadMovieList(mLoadMoviesCallbackCaptor.capture());
        mLoadMoviesCallbackCaptor.getValue().onMoviesLoaded(movieList);
        verify(mView).showMovieList(movieList);
        verify(mView).onSortChanged();
    }

    @Test
    public void onSortByTapped_test() {
        //Setup and stub
        String pref = "favorite";
        when(mModel.getSortPref()).thenReturn(pref);

        //Call method under test
        mPresenter.onSortByTapped();

        //Verify
        verify(mModel).getSortPref();
        verify(mView).inflateSortOptionsMenu(pref);
    }



    @Test
    public void onMovieSelected_test() {
        mPresenter.onMovieSelected(anyInt());
         verify(mView).showMovieDetailsUI();

    }
}
