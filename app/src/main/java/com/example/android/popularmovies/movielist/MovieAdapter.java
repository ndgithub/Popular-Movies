package com.example.android.popularmovies.movielist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utils.PosterImageView;
import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MovieAdapter extends ArrayAdapter {
    private Context mContext;
    private LayoutInflater mInflater;

    public MovieAdapter(Context context, ArrayList objects) {
        super(context, R.layout.list_item,objects);
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        Picasso.with(context).setIndicatorsEnabled(false);
    }

    static class ViewHolder {
        private PosterImageView poster;
    }

    @Override
    public View getView(int position, View recycled, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (recycled == null) {
            recycled = mInflater.inflate(R.layout.list_item, parent, false);
            holder.poster = (PosterImageView) recycled.findViewById(R.id.image);
            recycled.setTag(holder);
        } else {
            holder = (ViewHolder) recycled.getTag();
        }
        Movie currentMovie = (Movie) getItem(position);
        String pathToImage = "https://image.tmdb.org/t/p/w500" + currentMovie.getPosterPath();
        Picasso.with(mContext).load(pathToImage).fit().centerCrop().into(holder.poster);
        return recycled;
    }
}