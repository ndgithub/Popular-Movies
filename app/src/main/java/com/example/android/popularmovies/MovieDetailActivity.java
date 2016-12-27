package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MovieDetailActivity extends AppCompatActivity {
    CastAdapter mAdapter;
    String movieId;
    GridView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_movie_details);
        Bundle bundle = this.getIntent().getExtras();
        String backdropPath = bundle.getString("backdropPath");
        String posterPath = bundle.getString("posterPath");
        String date = bundle.getString("date");
        String title = bundle.getString("title");
        String overview = bundle.getString("overview");
        String rating = bundle.getString("rating");
        movieId = bundle.getString("id");

        ImageView backdropView = (ImageView) this.findViewById(R.id.backdrop);
        PosterImageView posterView = (PosterImageView) this.findViewById(R.id.poster);
        TextView titleView = (TextView) this.findViewById(R.id.title);
        TextView dateView = (TextView) this.findViewById(R.id.date);
        TextView ratingView = (TextView) this.findViewById(R.id.rating);
        TextView overviewView = (TextView) this.findViewById(R.id.overview);

        final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w780";
        Picasso.with(this).load(BASE_IMAGE_URL + backdropPath).fit().centerCrop().into(backdropView);
        Picasso.with(this).load(BASE_IMAGE_URL + posterPath).fit().centerInside().into(posterView);
        titleView.setText(title);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy ");
        Date startDate;
        try {
            startDate = df.parse(date);
            String newDateString = formatter.format(startDate);
            dateView.append(newDateString);
            Log.v("***",newDateString);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.v("***","You got caught");
        }

        titleView.setText(title);
        ratingView.setText(rating);
        overviewView.setText(overview);
        this.setTitle(title);
        listView = (GridView) findViewById(R.id.cast_grid_view);
        TextView castEmptyView = (TextView) findViewById(R.id.cast_empyt_view);
        listView.setEmptyView(castEmptyView);
        mAdapter = new CastAdapter(getApplicationContext(), new ArrayList<CastMember>());
        if (QueryUtils.isConnectedToInternet(this)) {
            getCastListAndUpdateUI();
        } else {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }

    }

    private void getCastListAndUpdateUI() {
        JsonObjectRequest jsCastRequest = new JsonObjectRequest
                (Request.Method.GET,
                        "http://api.themoviedb.org/3/movie/" + movieId + "/casts?api_key=" + QueryUtils.API_KEY,
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<CastMember> list = QueryUtils.extractCastFromJson(response);
                        if (list != null ) {
                            mAdapter.addAll(list);
                            listView.setAdapter(mAdapter);
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

        SingletonRequestQueue.getInstance(this).addToRequestQueue(jsCastRequest);

    }
}

