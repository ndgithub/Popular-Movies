package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nicky on 10/21/16.
 */

public class MovieAdapter extends ArrayAdapter {

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param \resource The resource ID for a layout file containing a TextView to use when
     */
    public MovieAdapter(Context context, ArrayList objects) {
        super(context,0,objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Find the earthquake at the given position in the list of earthquakes
        Movie currentMovie = (Movie) getItem(position);

        // Find the TextView with view ID magnitude
        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        titleView.setText(currentMovie.getTitle());
        return listItemView;
    }
}
