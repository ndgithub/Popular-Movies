package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MovieAdapter movieAdapter;
    private SharedPreferences sharedPref;
    private String sortPref;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.list_view);
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
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
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        sortPref = sharedPref.getString("sort_by", null);
        if (sortPref.equals("popular")) {
            popup.getMenu().findItem(R.id.pop).setChecked(true);
        } else {
            popup.getMenu().findItem(R.id.top).setChecked(true);
        }
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                SharedPreferences.Editor prefEditor;
                prefEditor = sharedPref.edit();
                if (itemId == R.id.pop) {
                    prefEditor.putString("sort_by", "popular");
                } else if (itemId == R.id.top) {
                    prefEditor.putString("sort_by", "top_rated");
                }
                prefEditor.apply();
                sortPref = sharedPref.getString("sort_by", null);
                getMovieListAndUpdateUI();
                return true;
            }
        });
        return true;
    }

    private void getMovieListAndUpdateUI() {
        JsonObjectRequest jsMovieRequest = new JsonObjectRequest
                (Request.Method.GET,
                        "https://api.themoviedb.org/3/movie/" + sortPref + "?api_key=" +
                                QueryUtils.API_KEY + "&language=en-US", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Movie> list = QueryUtils.extractMovieFeaturesFromJson(response);
                        if (list != null ) {
                            movieAdapter = new MovieAdapter(getApplicationContext(), list);
                            gridView.setAdapter(movieAdapter);
                        }
                        if (!QueryUtils.isConnectedToInternet(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(),R.string.no_internet,Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),R.string.error_retrieving_movies,Toast.LENGTH_SHORT).show();

                    }
                });

        SingletonRequestQueue.getInstance(this).addToRequestQueue(jsMovieRequest);


    }

}