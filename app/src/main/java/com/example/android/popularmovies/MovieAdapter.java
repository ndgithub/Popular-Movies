package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by Nicky on 10/21/16.
 */

public class MovieAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList objects;

    public MovieAdapter(Context context, ArrayList objects) {
        super(context,R.layout.list_item,objects);
        this.context = context;
        this.objects = objects;
        inflater = LayoutInflater.from(context);
        Picasso.with(context).setIndicatorsEnabled(false);
    }


    @Override
    public View getView(int position, View recycled, ViewGroup parent) {
        View listItemView = recycled;
        if (listItemView == null) {
            listItemView = inflater.inflate(R.layout.list_item, parent, false);
        }
        Movie currentMovie = (Movie) getItem(position);
        PosterImageView imageView = (PosterImageView) listItemView.findViewById(R.id.image);
        String pathToImage = "https://image.tmdb.org/t/p/w500" + currentMovie.getPosterPath();
        Picasso.with(context).load(pathToImage).fit().centerCrop().into(imageView);
        //TextView captionBar = (TextView) listItemView.findViewById(R.id.movie_titulo);
        //captionBar.setText(currentMovie.getTitle());
        return listItemView;

    }
}