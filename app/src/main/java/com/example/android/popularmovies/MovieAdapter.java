package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;
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
    private LayoutInflater mInflater;
    private ArrayList objects;

    public MovieAdapter(Context context, ArrayList objects) {
        super(context,R.layout.list_item,objects);
        this.context = context;
        this.objects = objects;
        mInflater = LayoutInflater.from(context);
        Picasso.with(context).setIndicatorsEnabled(false);
    }

    static class ViewHolder {
        private PosterImageView poster;
    }

    @Override
    public View getView(int position, View recycled, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        View listItemView = recycled;
        if (listItemView == null) {
            listItemView = mInflater.inflate(R.layout.list_item, parent, false);
            holder.poster = (PosterImageView) listItemView.findViewById(R.id.image);
            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder) listItemView.getTag();
        }
        Movie currentMovie = (Movie) getItem(position);
        Log.v("**",currentMovie.getTitle());
        String pathToImage = "https://image.tmdb.org/t/p/w500" + currentMovie.getPosterPath();
        Picasso.with(context).load(pathToImage).fit().centerCrop().into(holder.poster);
        return listItemView;
    }
}