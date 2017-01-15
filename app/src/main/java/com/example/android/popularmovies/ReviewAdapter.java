package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Nicky on 1/10/17.
 */



public class ReviewAdapter extends ArrayAdapter<Review> {
    private Context context;

    private LayoutInflater mInflater;

    public ReviewAdapter(Context context, ArrayList<Review> objects) {
        super(context, 0, objects);
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    static class ViewHolder {
        TextView authorHolder;
        TextView contentHolder;
        TextView urlHolder;
    }

    @NonNull
    @Override
    public View getView(int position, final View recycled, ViewGroup parent) {
        View listItemView = recycled;
        ReviewAdapter.ViewHolder holder = new ReviewAdapter.ViewHolder();
        if (listItemView != null) {
            holder = (ReviewAdapter.ViewHolder) listItemView.getTag();
        } else {
            listItemView = mInflater.inflate(R.layout.review_list_item, parent, false);
            holder.authorHolder = (TextView) listItemView.findViewById(R.id.author_view);
            holder.contentHolder = (TextView) listItemView.findViewById(R.id.content_view);
            holder.urlHolder = (TextView) listItemView.findViewById(R.id.url_view);
            listItemView.setTag(holder);
        }
        final Review currentReview = getItem(position);
        String byLine = getContext().getResources().getText(R.string.by) + currentReview.getAuthor();
        holder.authorHolder.setText(byLine);
        holder.contentHolder.setText(currentReview.getContent());

        holder.urlHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(currentReview.getURL()));
                webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(webIntent);
            }
        });

        return listItemView;

    }
}
