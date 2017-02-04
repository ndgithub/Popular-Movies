package com.example.android.popularmovies.movieDetails;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.popularmovies.data.CastMember;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Video;
import com.example.android.popularmovies.utils.PosterImageView;
import com.example.android.popularmovies.utils.QueryUtils;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.utils.SingletonRequestQueue;
import com.example.android.popularmovies.data.MovieDbContract.FavoritesEntry;
import com.example.android.popularmovies.movieList.MainActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MovieDetailActivity extends AppCompatActivity  {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_movie_details);
        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle bundie = getIntent().getBundleExtra("movi");
        detailsFragment.setArguments(bundie);
        getFragmentManager().beginTransaction().replace(R.id.details_container,detailsFragment).commit();
    }
}