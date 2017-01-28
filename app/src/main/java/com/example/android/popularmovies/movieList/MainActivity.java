package com.example.android.popularmovies.movieList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.movieDetails.MovieDetailActivity;
import com.example.android.popularmovies.utils.QueryUtils;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.utils.SingletonRequestQueue;
import com.example.android.popularmovies.data.MovieDbContract.FavoritesEntry;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieListContract.View {

    private MovieAdapter movieAdapter;
    ArrayList<Movie> movieList = new ArrayList<Movie>();
    TextView emptyView;
    movieListPresenter MVPpresenter;
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.list_view);
        emptyView = (TextView) findViewById(R.id.empty_view);
        gridView.setEmptyView(emptyView);

        movieAdapter = new MovieAdapter(MainActivity.this, movieList);
        gridView.setAdapter(movieAdapter);

        MVPpresenter = movieListPresenter.getInstance(getContentResolver(), MainActivity.this,this);
        MVPpresenter.start();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MVPpresenter.onMovieSelected(position);
            }
        });
        Log.v("***** - MainActivity","onCreate");
    }

    @Override
    public void onRestart() {
        super.onRestart();

        Log.v("***** - MainActivity", "onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MVPpresenter.onSortByTapped();
        return true;
    }

    @Override
    public void showMovies(ArrayList<Movie> movieList) {
        if (movieList != null) {
            movieAdapter.clear();
            movieAdapter.addAll(movieList);
            Log.v("***** - MainActivity", "showMovies, MovieList = " + movieList);
        } else {
            Toast.makeText(MainActivity.this, R.string.error_retrieving_movies, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMovieDetailsUI(int position) {
        Movie selectedMovie = (Movie) movieAdapter.getItem(position);
        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
        intent.putExtra("movie", Parcels.wrap(selectedMovie));
        startActivity(intent);
    }

    @Override
    public void inflateSortOptionsMenu(String sortPref) {
        View menuItemView = findViewById(R.id.sort_by);
        final PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        switch (sortPref) {
            case "popular":
                popup.getMenu().findItem(R.id.pop).setChecked(true);
                break;
            case "top_rated":
                popup.getMenu().findItem(R.id.top).setChecked(true);
                break;
            case "favorite":
                popup.getMenu().findItem(R.id.fav).setChecked(true);
                break;
        }

        Log.v("***** - MainActivity", "inflateSortOptionsMenu");
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                MVPpresenter.onSortChanged(menuItem);
                return true;
            }
        });
    }
}


















