package com.example.android.popularmovies.movieList;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.movieDetails.DetailsFragment;

public class MainActivity extends AppCompatActivity {

    boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListFragmenter listFragment = new ListFragmenter();

        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment,listFragment).commit();

        if (findViewById(R.id.details_container) != null) {
            mTwoPane = true;
            getFragmentManager().beginTransaction().replace(R.id.details_container, new DetailsFragment()).commit();
        } else {
            mTwoPane = false;
        }
    }
}


















