package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {
    private final String LOG_TAG = "MainActivity";
    MovieAdapter movieAdapter;
    SharedPreferences sharedPref;
    String sortPref;
    SharedPreferences.Editor prefEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        GridView gridView = (GridView) findViewById(R.id.list_view);
        movieAdapter = new MovieAdapter(this, new ArrayList<Movie>());
        gridView.setAdapter(movieAdapter);
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        gridView.setEmptyView(emptyView);
        if (isConnectedToInternet()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(0, null, this);
        } else {
            emptyView.setText("No Internet");
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie selectedMovie = (Movie) movieAdapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("id", selectedMovie.getId());
                bundle.putString("date", selectedMovie.getDate());
                bundle.putString("title", selectedMovie.getTitle());
                bundle.putString("backdropPath", selectedMovie.getBackdropPath());
                bundle.putString("overview", selectedMovie.getOverview());
                bundle.putString("rating", selectedMovie.getRating());
                bundle.putString("posterPath", selectedMovie.getPosterPath());
                Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
                intent.putExtras(bundle);
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
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.pop) {
                        Toast.makeText(getApplicationContext(),"pop",Toast.LENGTH_SHORT).show();
                        sharedPref = getPreferences(MODE_PRIVATE);
                        prefEditor = sharedPref.edit();
                        prefEditor.putString("sort_by","popular");
                        prefEditor.commit();
                        sortPref = sharedPref.getString("sort_by",null);
                        Log.v("**pop"," " + sortPref);

                    } else if (itemId == R.id.top) {
                        Toast.makeText(getApplicationContext(),"top",Toast.LENGTH_SHORT).show();
                        sharedPref = getPreferences(MODE_PRIVATE);
                        prefEditor = sharedPref.edit();
                        prefEditor.putString("sort_by","top_rated");
                        prefEditor.commit();
                        sortPref = sharedPref.getString("sort_by",null);
                        Log.v("**top"," " + sortPref);
                        recreate();
                    }
                    return true;
                }
            });

            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.popup_menu, popup.getMenu());
            popup.show();

        return true;
    }



    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        sharedPref = getPreferences(MODE_PRIVATE);
        sortPref = sharedPref.getString("sort_by","popular");
        Log.v("**OnCreateLoader",sortPref + "");
        String url = "https://api.themoviedb.org/3/movie/" + sortPref + "?api_key=d962b00501dc49c8dfd38339a7daa32a&language=en-US";
        return new MovieLoader(this, url);
    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader loader) {
        movieAdapter.clear();
    }


    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> list) {
        movieAdapter.clear();
        movieAdapter.addAll(list);
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.v(LOG_TAG, "Connected to internet");
            return true;
        } else {
            return false;
        }
    }


}