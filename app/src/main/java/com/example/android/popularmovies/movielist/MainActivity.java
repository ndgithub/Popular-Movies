package com.example.android.popularmovies.movielist;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieRepoInterface;
import com.example.android.popularmovies.data.ReposHolder;
import com.example.android.popularmovies.data.UserPrefImpl;
import com.example.android.popularmovies.data.remote.MovieServiceApiImpl;
import com.example.android.popularmovies.moviedetails.DetailsFragment;
import com.example.android.popularmovies.utils.ActivityUtils;

public class MainActivity extends AppCompatActivity implements MainFragment.ListFragmentInterface, DetailsFragment.onGoToFavoritesListener {

    MainFragment mListFragment;
    DetailsFragment mDetailsFragment;

    Context mContext;
    boolean mShowListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mShowListFragment = true;
        if (savedInstanceState != null) {
            mShowListFragment = savedInstanceState.getBoolean("showListFragment");
        }

        //Initialize the repo and load up a movie list before the first fragment is loaded.
        MovieRepoInterface movieRepo = ReposHolder.getMovieRepo(new UserPrefImpl(getContentResolver(), getApplicationContext()), new MovieServiceApiImpl(getApplicationContext()));
        movieRepo.initializeMovieList(new MovieRepoInterface.InitMoviesCallback() {
            @Override
            public void onMoviesLoaded() {
                loadFragments();
            }
        });

    }

    private void loadFragments() {
        //If tablet mode, load both fragments.
        if (ActivityUtils.isTwoPane(mContext)) {
            mListFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_1, mListFragment).commit();
            mDetailsFragment = new DetailsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_2, mDetailsFragment).commit();
        }
        //  Load List Fragment
        else if (mShowListFragment) {
            mListFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_1, mListFragment).commit();
        }
        // If orientation changed while showing detail fragment, load detail fragment.
        else {
            getSupportFragmentManager().popBackStack();
            mDetailsFragment = new DetailsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_1, mDetailsFragment).addToBackStack("deetFrag").commit();
        }
    }

    @Override //Interface method in ListFragment
    public void onMovieSelected() {
        if (ActivityUtils.isTwoPane(mContext)) {
            mDetailsFragment = new DetailsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_2, mDetailsFragment).addToBackStack("deetFrag").commit();
        } else {
            mDetailsFragment = new DetailsFragment();
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_1, mDetailsFragment).addToBackStack("deetFrag").commit();
            mShowListFragment = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("showListFragment", mShowListFragment);
    }

    @Override //Interface Method
    public void onGoToFavoritesList() {
        mListFragment = new MainFragment();
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_1, mListFragment).commit();
    }

    public void onSortChanged() {
        if (ActivityUtils.isTwoPane(mContext)) {
            mDetailsFragment = new DetailsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_2, mDetailsFragment).addToBackStack("deetFrag").commit();
        }

    }

    @Override
    public void onListFragmentShowing() {
        mShowListFragment = true;
    }
}


//----------------------- Notes -----------------------//


//Working on Test for MovieRepo. Don't need to test its dependecnies.

// Create Injection class for mock source set and production.
//what happens if no wi-fi

// empty view to favorites list

//Espresso testing
//Dagger
//RetroFit
//RxJava
//Room



















