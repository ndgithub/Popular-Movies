package com.example.android.popularmovies.movieList;

import android.content.Intent;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.movieDetails.DetailsFragment;
import com.example.android.popularmovies.movieDetails.MovieDetailActivity;
import com.example.android.popularmovies.utils.ActivityUtils;

import org.parceler.Parcels;

public class MainActivity extends AppCompatActivity implements ListFragmenter.onMovieSelectedListener, DetailsFragment.onGoToFavoritesListener {

    boolean mIsTwoPane;
    DetailsFragment detailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = new Bundle();

        if (findViewById(R.id.details_fragment) != null) {
            mIsTwoPane = true;
            detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.details_fragment, detailsFragment).commit();
        } else {
            mIsTwoPane = false;
        }

        ListFragmenter listFragmenter = new ListFragmenter();
        listFragmenter.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment,listFragmenter).commit();

    }

   @Override //Interface method
   public void onMovieSelected(Bundle bundle) {
       if (ActivityUtils.isTwoPane(getApplicationContext())) {
           detailsFragment = new DetailsFragment();
           detailsFragment.setArguments(bundle);
           getFragmentManager().beginTransaction().replace(R.id.details_fragment, detailsFragment).commit();
       } else {
           Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
           intent.putExtra("movieBundle",bundle);
           startActivity(intent);
       }
    }

    @Override //Interface Method
    public void onGoToFavoritesList() {
        Log.v("###","mIsTwoPane: " + mIsTwoPane);
        ListFragmenter listFragmenter = new ListFragmenter();
        Bundle bundle = new Bundle();
        bundle.putBoolean("twoPane",mIsTwoPane);
        listFragmenter.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment,listFragmenter).commit();
    }

}


















