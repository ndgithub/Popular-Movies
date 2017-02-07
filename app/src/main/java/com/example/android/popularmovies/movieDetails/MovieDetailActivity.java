package com.example.android.popularmovies.movieDetails;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.popularmovies.R;

public class MovieDetailActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_movie_details);
        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle bundie = getIntent().getBundleExtra("movieBundle");
        detailsFragment.setArguments(bundie);
        getFragmentManager().beginTransaction().replace(R.id.details_container,detailsFragment).commit();

        Log.v("***** - DetailActivity","Activity onCreate");
    }
}