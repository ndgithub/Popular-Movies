package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MovieDetailActivity extends AppCompatActivity {

    CastAdapter mAdapter;

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
        String id = bundle.getString("id");

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
        GridView listView = (GridView) findViewById(R.id.cast_grid_view);
        mAdapter = new CastAdapter(this, new ArrayList<CastMember>());
        listView.setAdapter(mAdapter);
        TextView castEmptyView = (TextView) findViewById(R.id.cast_empyt_view);
        listView.setEmptyView(castEmptyView);
        castAsyncTask mytask = new castAsyncTask();
        mytask.execute(id);
    }

    private class castAsyncTask extends AsyncTask<String, String, ArrayList<CastMember>> {

        @Override
        protected ArrayList<CastMember> doInBackground(String... movieID) {
            String castURL = "http://api.themoviedb.org/3/movie/" + movieID[0] + "/casts?api_key=" + QueryUtils.API_KEY;
            return QueryUtils.fetchCastData(castURL);
        }

        @Override
        protected void onPostExecute(ArrayList<CastMember> castList) {
            mAdapter.clear();
            mAdapter.addAll(castList);
        }
    }
}

