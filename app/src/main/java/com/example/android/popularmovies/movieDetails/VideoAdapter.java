package com.example.android.popularmovies.movieDetails;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Nicky on 1/9/17.
 */

public class VideoAdapter extends ArrayAdapter {
    private LayoutInflater mInflater;

    public VideoAdapter(Context context, ArrayList<Video> objects) {
        super(context, 0, objects);
        mInflater = LayoutInflater.from(context);
    }

    static class ViewHolder {
        TextView videoNameHolder;
        ImageView thumbnailHolder;
    }

    @NonNull
    @Override
    public View getView(int position, View recycled, ViewGroup parent) {
        View listItemView = recycled;
        VideoAdapter.ViewHolder holder = new VideoAdapter.ViewHolder();
        if (listItemView == null) {
            listItemView = mInflater.inflate(R.layout.video_list_item, parent, false);
            holder.videoNameHolder = (TextView) listItemView.findViewById(R.id.video_name);
            holder.thumbnailHolder = (ImageView) listItemView.findViewById(R.id.video_image);
            listItemView.setTag(holder);
        } else {
            holder = (VideoAdapter.ViewHolder) listItemView.getTag();
        }
        Video currentVideo = (Video) getItem(position);
        holder.videoNameHolder.setText(currentVideo.getVideoTitle());

        Picasso.with(getContext())
                .load("http://img.youtube.com/vi/" + currentVideo.getKey() + "/default.jpg")
                .resize(300,0)
                .into(holder.thumbnailHolder);

        return listItemView;

    }
}
