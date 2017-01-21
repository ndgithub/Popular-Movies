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

public class MovieDetailActivity extends AppCompatActivity {

    CastAdapter castAdapter;
    ArrayList<CastMember> castList;
    GridView castGridView;

    VideoAdapter videoAdapter;
    ArrayList<Video> videoList;
    GridView videoGridView;

    ReviewAdapter reviewAdapter;
    ArrayList<Review> reviewList;
    ListView reviewListView;
    ImageView favButton;

    Movie selectedMovie;
    boolean favorite;
    String snackbarText;
    final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w780";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_movie_details);

        selectedMovie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));

        this.setTitle(selectedMovie.getTitle());

        ImageView backdropView = (ImageView) this.findViewById(R.id.backdrop);
        Picasso.with(this).load(BASE_IMAGE_URL + selectedMovie.getBackdropPath()).fit().centerCrop().into(backdropView);

        PosterImageView posterView = (PosterImageView) this.findViewById(R.id.poster);
        Picasso.with(this).load(BASE_IMAGE_URL + selectedMovie.getPosterPath()).fit().centerInside().into(posterView);

        TextView titleView = (TextView) this.findViewById(R.id.title);
        titleView.setText(selectedMovie.getTitle());

        TextView ratingView = (TextView) this.findViewById(R.id.rating);
        ratingView.setText(selectedMovie.getRating());

        TextView overviewView = (TextView) this.findViewById(R.id.overview);
        overviewView.setText(selectedMovie.getOverview());

        TextView dateView = (TextView) this.findViewById(R.id.date);
        dateView.setText("Release Date: " + formatDate(selectedMovie.getDate()));

        favButton = (ImageView) findViewById(R.id.fav_button);
        favorite = isFavorite();
        if (favorite) {
            favButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        }
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favorite) {
                    removeFromFavoritesDb();
                } else {
                    addToFavoritesDb();
                }
            }
        });





        castGridView = (GridView) findViewById(R.id.cast_grid_view);
        castList = new ArrayList<>();
        castAdapter = new CastAdapter(getApplicationContext(), castList);
        castGridView.setAdapter(castAdapter);
        TextView castEmptyView = (TextView) findViewById(R.id.cast_empyt_view);
        castGridView.setEmptyView(castEmptyView);

        reviewListView = (ListView) findViewById(R.id.review_list_view);
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(getApplicationContext(), reviewList);
        reviewListView.setAdapter(reviewAdapter);
        TextView reviewEmptyView = (TextView) findViewById(R.id.review_empty_view);
        reviewListView.setEmptyView(reviewEmptyView);

        videoGridView = (GridView) findViewById(R.id.video_grid_view);
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(getApplicationContext(), videoList);
        videoGridView.setAdapter(videoAdapter);
        TextView videoEmptyView = (TextView) findViewById(R.id.video_empty_view);
        videoGridView.setEmptyView(videoEmptyView);
        videoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Video selectedVideo = (Video) videoAdapter.getItem(position);
                String vidKey = selectedVideo.getKey();
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + vidKey));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + vidKey));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            }
        });


        getCastListAndUpdateUI();
        getTrailersAndUpdateUI();
        getReviewsAndUpdateUI();

    }
    private String formatDate(String inputDate) {
        Date releaseDate;
        String newDateString;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy ");
        try {
            releaseDate = df.parse(inputDate);
            newDateString = formatter.format(releaseDate);
            return newDateString;
        } catch (ParseException e) {
            e.printStackTrace();
            newDateString = null;
            return newDateString;
        }

    }

    private void getCastListAndUpdateUI() {
        if (QueryUtils.isConnectedToInternet(getApplicationContext())) {
            JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                    (Request.Method.GET,
                            "http://api.themoviedb.org/3/movie/" + selectedMovie.getId() +
                                    "/casts?api_key=" + QueryUtils.API_KEY, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            castList = QueryUtils.extractCastFromJson(response);
                            if (castList != null) {
                                castAdapter.clear();
                                castAdapter.addAll(castList);
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (QueryUtils.isConnectedToInternet(getApplicationContext())) {
                                Toast.makeText(getApplicationContext(), R.string.error_retrieving_cast_data, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            SingletonRequestQueue.getInstance(this).addToRequestQueue(jsonObjRequest);

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }

    }

    private void getTrailersAndUpdateUI() {
        if (QueryUtils.isConnectedToInternet(getApplicationContext())) {
            JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                    (Request.Method.GET,
                            "http://api.themoviedb.org/3/movie/" + selectedMovie.getId() +
                                    "/videos?api_key=" + QueryUtils.API_KEY, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            videoList = QueryUtils.getVideosFromJson(response);
                            if (videoList != null) {
                                videoAdapter.clear();
                                videoAdapter.addAll(videoList);
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (QueryUtils.isConnectedToInternet(getApplicationContext())) {
                                Toast.makeText(getApplicationContext(), R.string.videos_unavailable, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            SingletonRequestQueue.getInstance(this).addToRequestQueue(jsonObjRequest);

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }

    }

    //TODO: Reviews get cutoff (also videos)
    private void getReviewsAndUpdateUI() {
        if (QueryUtils.isConnectedToInternet(getApplicationContext())) {
            JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                    (Request.Method.GET,
                            "http://api.themoviedb.org/3/movie/" + selectedMovie.getId() +
                                    "/reviews?api_key=" + QueryUtils.API_KEY, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            reviewList = QueryUtils.getReviewsFromJson(response);
                            if (reviewList != null) {
                                reviewAdapter.clear();
                                reviewAdapter.addAll(reviewList);
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (QueryUtils.isConnectedToInternet(getApplicationContext())) {
                                Toast.makeText(getApplicationContext(), R.string.reviews_unavailable, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            SingletonRequestQueue.getInstance(this).addToRequestQueue(jsonObjRequest);

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }

    }

    private void addToFavoritesDb() {

        ContentValues cv = new ContentValues();
        cv.put(FavoritesEntry.COLUMN_MOVIE_ID, selectedMovie.getId());
        cv.put(FavoritesEntry.COLUMN_BACKDROP_PATH, selectedMovie.getBackdropPath());
        cv.put(FavoritesEntry.COLUMN_OVERVIEW, selectedMovie.getOverview());
        cv.put(FavoritesEntry.COLUMN_POSTER_PATH, selectedMovie.getPosterPath());
        cv.put(FavoritesEntry.COLUMN_TITLE, selectedMovie.getTitle());
        cv.put(FavoritesEntry.COLUMN_RATING, selectedMovie.getRating());
        cv.put(FavoritesEntry.COLUMN_RELEASE_DATE, selectedMovie.getDate());

        Uri newRowUri = getContentResolver().insert(FavoritesEntry.CONTENT_URI, cv);

        if (newRowUri == null) {
            Toast.makeText(this, "Didn't work! Movie not added to favorites", Toast.LENGTH_SHORT).show();
        } else {
            favButton.setImageResource(R.drawable.ic_favorite_black_24dp);
            favorite = true;
            snackbarText = "Added to Favorites";
            showSnackbar(snackbarText);
        }
    }

    private void removeFromFavoritesDb() {
        int rowsDel = getContentResolver().delete(FavoritesEntry.CONTENT_URI,
                FavoritesEntry.COLUMN_MOVIE_ID + " = " + selectedMovie.getId(), null);
        if (rowsDel == 1) {
            favButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            favorite = false;
            snackbarText = "Removed From Favorites";
            showSnackbar(snackbarText);
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isFavorite() {
        String thisMovieId = selectedMovie.getId();
        String[] projection = {FavoritesEntry.COLUMN_MOVIE_ID};
        Cursor cursor = getContentResolver().query(FavoritesEntry.CONTENT_URI, projection,
                FavoritesEntry.COLUMN_MOVIE_ID + " = " + thisMovieId, null, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;

        } else {
            cursor.close();
            return true;
        }


    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.scroll_view), message, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.go_to_favorites, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                prefEditor.putString("sort_by", "favorite");
                prefEditor.apply();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        snackbar.show();
    }


}