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


public class MainActivity extends AppCompatActivity implements
        ListFragmenter.onMovieSelectedListener, DetailsFragment.onGoToFavoritesListener {

    boolean mIsTwoPane;
    DetailsFragment detailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = new Bundle();

        ListFragmenter listFragmenter = new ListFragmenter();
        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment, listFragmenter).commit();
        Log.v("*** - MainActivity", "onCreate");
        if (detailsFragment == null) {
            Log.v("*** MainAct create","detaila framents is null");
        } else {
            Log.v("*** MainAct create","detaila framents is NOT null");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (detailsFragment == null) {
            Log.v("*** MainAct stop","detaila framents is null");
        } else {
            Log.v("*** MainAct stop","detaila framents is NOT null");
        }


    }

    @Override //Interface method in ListFragment
    public void onMovieSelected(Bundle bundle) {

        if (ActivityUtils.isTwoPane(getApplicationContext())) {
            detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.details_fragment, detailsFragment).commit();
        } else {
            Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
            intent.putExtra("movieBundle", bundle);
            startActivity(intent);
        }
        Log.v("*** - MainActivity","onMovieSelected");

    }

    @Override //Interface Method
    public void onGoToFavoritesList() {
        ListFragmenter listFragmenter = new ListFragmenter();
        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment, listFragmenter).commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("*** - MainActivity","onDestroy");
    }

}




















