package com.example.android.popularmovies.movielist;

import com.example.android.popularmovies.data.MVPmodel;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Created by Nicky on 11/17/17.
 */

public class MovieListPresenterTest {

    // Mock the dependencies of the presenter

    @Mock
    MovieListContract.View mView;

    @Mock
    MVPmodel mModel;

    public MovieListPresenter mPresenter;

    @Before
    public void setupPresenter() {
        mPresenter = new MovieListPresenter(mModel,mView);
    }





}
