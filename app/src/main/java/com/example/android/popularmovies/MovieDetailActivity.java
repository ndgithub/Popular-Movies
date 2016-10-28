package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by Nicky on 10/23/16.
 */

public class MovieDetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Bundle bundle = this.getIntent().getExtras();
        String backdropPath = bundle.getString("backdropPath");
        String posterPath = bundle.getString("posterPath");
        String date =  bundle.getString("date");
        String title = bundle.getString("title");
        String overview = bundle.getString("overview");
        String rating = bundle.getString("rating");

        ImageView backdropView = (ImageView) this.findViewById(R.id.backdrop);
        ImageView posterView = (ImageView) this.findViewById(R.id.poster);
        TextView titleView = (TextView) this.findViewById(R.id.title);
        TextView dateView = (TextView) this.findViewById(R.id.date);
        TextView ratingView = (TextView) this.findViewById(R.id.rating);
        TextView overviewView = (TextView) this.findViewById(R.id.overview);

        final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w780";
        Picasso.with(this).load(BASE_IMAGE_URL + backdropPath).fit().centerCrop().into(backdropView);
        Picasso.with(this).load(BASE_IMAGE_URL + posterPath).resize(400,0).into(posterView);
        titleView.setText(title);
        dateView.setText(date);
        titleView.setText(title);
        ratingView.setText(rating);
        overviewView.setText(overview);
        this.setTitle(title);
    }
}

