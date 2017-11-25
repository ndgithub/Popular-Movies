package com.example.android.popularmovies.movielist;

import android.view.MenuItem;

import com.example.android.popularmovies.data.MVPmodel;
import com.example.android.popularmovies.data.ModelInterface;
import com.example.android.popularmovies.data.Movie;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Nicky on 11/17/17.
 */

public class MovieListPresenterTest {

    // Mock the dependencies of the presenter

    private static ArrayList<Movie> movieList = new ArrayList<>();

    @Mock
    MovieListContract.View mView;

    @Mock
    ModelInterface mModel;

    @Captor
    ArgumentCaptor<ModelInterface.LoadMoviesCallback> mLoadMoviesCallbackCaptor;

    @Captor
    ArgumentCaptor<MenuItem> mMenuItemCaptor;


    private MovieListPresenter mPresenter;

    @Before
    public void setupPresenter() {
        MockitoAnnotations.initMocks(this);
        movieList.add(new Movie("346364","/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg","overview","/tcheoA2nPATCm2vvXw2hVQoaEFD.jpg","It","7.3","2017-09-05"));
        mPresenter = new MovieListPresenter(mModel,mView);
    }

    @Test
    public void showMovieList_test() {
        mPresenter.showMovieList();
        verify(mModel).getMovieList(mLoadMoviesCallbackCaptor.capture());
        mLoadMoviesCallbackCaptor.getValue().onMoviesLoaded(movieList);
        verify(mView).showMovieList(movieList);
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






}
