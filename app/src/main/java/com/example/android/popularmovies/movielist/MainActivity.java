package com.example.android.popularmovies.movielist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.moviedetails.DetailsFragment;
import com.example.android.popularmovies.moviedetails.MovieDetailActivity;
import com.example.android.popularmovies.utils.ActivityUtils;


public class MainActivity extends AppCompatActivity implements
        MainFragment.onMovieSelectedListener, DetailsFragment.onGoToFavoritesListener {

    DetailsFragment detailsFragment;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment, mainFragment).commit();

    }

    @Override //Interface method in ListFragment
    public void onMovieSelected() {

        if (ActivityUtils.isTwoPane(mContext)) {
            detailsFragment = new DetailsFragment();
            getFragmentManager().beginTransaction().replace(R.id.details_fragment, detailsFragment).addToBackStack("deetFrag").commit();
        } else {
            Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
            startActivity(intent);
        }
    }

    @Override //Interface Method
    public void onGoToFavoritesList() {
        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment, mainFragment).commit();
    }

}


//----------------------- Notes -----------------------//

// Create Injection class for mock source set and produ.
//    use this in presenter constructor. This will allow not moving selected movie around with bndle.



// move checking if fav to presenter.

//Don't worry about testing DB, just test repo class calls right things, and gets
// lists back.




//Add empty view to favorites list

//Espresso testing
//Dagger
//RetroFit
//RxJava
//Room



















