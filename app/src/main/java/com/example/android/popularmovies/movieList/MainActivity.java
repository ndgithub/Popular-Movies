package com.example.android.popularmovies.movieList;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.movieDetails.DetailsFragment;
import com.example.android.popularmovies.movieDetails.MovieDetailActivity;
import com.example.android.popularmovies.utils.ActivityUtils;


public class MainActivity extends AppCompatActivity implements
        MainFragment.onMovieSelectedListener, DetailsFragment.onGoToFavoritesListener {

    DetailsFragment detailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment, mainFragment).commit();
    }

    @Override //Interface method in ListFragment
    public void onMovieSelected(Bundle bundle) {

        if (ActivityUtils.isTwoPane(getApplicationContext())) {
            detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.details_fragment, detailsFragment).addToBackStack("deetFrag").commit();
        } else {
            Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
            intent.putExtra("movieBundle", bundle);
            startActivity(intent);
        }
    }

    @Override //Interface Method
    public void onGoToFavoritesList() {
        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment, mainFragment).commit();
    }

}




















