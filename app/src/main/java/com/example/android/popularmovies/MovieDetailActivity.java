package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Nicky on 10/23/16.
 */

public class MovieDetailActivity extends AppCompatActivity {

    CastAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ImageView posterView = (ImageView) this.findViewById(R.id.poster);
        TextView titleView = (TextView) this.findViewById(R.id.title);
        TextView dateView = (TextView) this.findViewById(R.id.date);
        TextView ratingView = (TextView) this.findViewById(R.id.rating);
        TextView overviewView = (TextView) this.findViewById(R.id.overview);

        final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w780";
        Picasso.with(this).load(BASE_IMAGE_URL + backdropPath).fit().centerCrop().into(backdropView);
        Picasso.with(this).load(BASE_IMAGE_URL + posterPath).resize(400, 0).into(posterView);
        titleView.setText(title);
        dateView.setText(date);
        titleView.setText(title);
        ratingView.setText(rating);
        overviewView.setText(overview);
        this.setTitle(title);
        GridView listView = (GridView) findViewById(R.id.cast_grid_view);
        mAdapter = new CastAdapter(this, new ArrayList<CastMember>());
        listView.setAdapter(mAdapter);
        castAsyncTask mytask = new castAsyncTask();
        mytask.execute(id);
    }

    private class castAsyncTask extends AsyncTask<String, String, ArrayList<CastMember>> {

        @Override
        protected ArrayList<CastMember> doInBackground(String... movieID) {
            String castURL = "http://api.themoviedb.org/3/movie/" + movieID[0] + "/casts?api_key=d962b00501dc49c8dfd38339a7daa32a";
            Log.v("adsf",castURL);
            return QueryUtils.fetchCastData(castURL);
        }

        @Override
        protected void onPostExecute(ArrayList<CastMember> castList) {
            for (CastMember person: castList) {
                Log.v("*****",person.getActorName());
            }
            mAdapter.clear();
            mAdapter.addAll(castList);

        }

    }


}

