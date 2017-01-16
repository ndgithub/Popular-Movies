package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.popularmovies.data.MovieContract.FavoritesEntry;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MovieAdapter movieAdapter;
    private SharedPreferences sharedPref;
    private String sortPref;
    ArrayList<Movie> movieList = new ArrayList<Movie>();
    TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridView gridView = (GridView) findViewById(R.id.list_view);
        emptyView = (TextView) findViewById(R.id.empty_view);
        movieAdapter = new MovieAdapter(getApplicationContext(), movieList);
        gridView.setAdapter(movieAdapter);
        gridView.setEmptyView(emptyView);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sortPref = sharedPref.getString("sort_by", null);
        if (QueryUtils.isConnectedToInternet(this)) {
            getMovieListAndUpdateUI();
        } else {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie selectedMovie = (Movie) movieAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
                intent.putExtra("movie", Parcels.wrap(selectedMovie));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View menuItemView = findViewById(R.id.sort_by);
        final PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        sortPref = sharedPref.getString("sort_by", null);

        switch (sortPref) {
            case "popular":
                popup.getMenu().findItem(R.id.pop).setChecked(true);
                break;
            case "top_rated":
                popup.getMenu().findItem(R.id.top).setChecked(true);
                break;
            case "favorites":
                popup.getMenu().findItem(R.id.fav).setChecked(true);
        }
//        if (sortPref.equals("popular")) {
//            popup.getMenu().findItem(R.id.pop).setChecked(true);
//        } else {
//            popup.getMenu().findItem(R.id.top).setChecked(true);
//        }


        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                SharedPreferences.Editor prefEditor;
                prefEditor = sharedPref.edit();

                switch (itemId) {
                    case (R.id.pop):
                        prefEditor.putString("sort_by", "popular");
                        break;
                    case (R.id.top):
                        prefEditor.putString("sort_by", "top_rated");
                        break;
                    case (R.id.fav):
                        prefEditor.putString("sort_by", "favorite");

                }

//                if (itemId == R.id.pop) {
//                    prefEditor.putString("sort_by", "popular");
//                } else if (itemId == R.id.top) {
//                    prefEditor.putString("sort_by", "top_rated");
//                }
                prefEditor.apply();
                sortPref = sharedPref.getString("sort_by", null);
                getMovieListAndUpdateUI();
                return true;
            }
        });
        return true;
    }

    private void getMovieListAndUpdateUI() {
        if (QueryUtils.isConnectedToInternet(getApplicationContext())) {
            if (sortPref.equals("favorite")) {
                movieList = getFavoritesList();
                if (movieList != null) {
                    movieAdapter.clear();
                    movieAdapter.addAll(movieList);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_favorites, Toast.LENGTH_SHORT).show();
                }

            } else {
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET,
                                "https://api.themoviedb.org/3/movie/" + sortPref + "?api_key=" +
                                        QueryUtils.API_KEY + "&language=en-US", null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                movieList = QueryUtils.extractMovieFeaturesFromJson(response);
                                if (movieList != null) {
                                    movieAdapter.clear();
                                    movieAdapter.addAll(movieList);
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), R.string.error_retrieving_movies, Toast.LENGTH_SHORT).show();
                            }
                        });
                SingletonRequestQueue.getInstance(this).addToRequestQueue(jsObjRequest);
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<Movie> getFavoritesList() {

        String[] projection = {FavoritesEntry.COLUMN_MOVIE_ID,
                FavoritesEntry.COLUMN_BACKDROP_PATH,
                FavoritesEntry.COLUMN_OVERVIEW,
                FavoritesEntry.COLUMN_POSTER_PATH,
                FavoritesEntry.COLUMN_TITLE,
                FavoritesEntry.COLUMN_RATING,
                FavoritesEntry.COLUMN_RELEASE_DATE};

        Cursor cursor = getContentResolver().query(
                FavoritesEntry.CONTENT_URI,   // The content URI of the words table
                projection,             // The columns to return for each row
                null,                   // Selection criteria
                null,                   // Selection criteria
                null);

        int idColumnIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_ID);
        int backdropColumnIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_BACKDROP_PATH);
        int overviewColumnIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_OVERVIEW);
        int PosterPathColumnIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_POSTER_PATH);
        int titleColumnIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_TITLE);
        int ratingColumnIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_RATING);
        int dateColumnIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_RELEASE_DATE);
        ArrayList<Movie> list = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                String movieId = cursor.getString(idColumnIndex);
                String backdrop = cursor.getString(backdropColumnIndex);
                String overview = cursor.getString(overviewColumnIndex);
                String posterPath = cursor.getString(PosterPathColumnIndex);
                String title = cursor.getString(titleColumnIndex);
                String rating = cursor.getString(ratingColumnIndex);
                String date = cursor.getString(dateColumnIndex);

                list.add(new Movie(movieId, backdrop, overview, posterPath, title, rating, date));
            }
        } finally {
            cursor.close();

        }

        return list;

    }
}