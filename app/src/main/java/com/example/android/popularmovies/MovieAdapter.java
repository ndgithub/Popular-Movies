package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        Picasso
                .with(context)
                .setIndicatorsEnabled(true);

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = inflater.inflate(R.layout.list_item, parent, false);
        }
        Movie currentMovie = (Movie) getItem(position);
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);
        String pathToImage = "https://image.tmdb.org/t/p/w500" + currentMovie.getPosterPath();
        Picasso.with(context).load(pathToImage).into(imageView);
        //titleView.setText(currentMovie.getTitle() + basePath + currentMovie.getPosterPath());
        return listItemView;
    }
}
//    https://image.tmdb.org/t/p/original/****PATH*****