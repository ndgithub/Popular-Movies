package com.example.android.popularmovies.movieList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;

public class ListFragmenter extends Fragment implements MovieListContract.View {

    TextView emptyView;
    movieListPresenter MVPpresenter;
    GridView gridView;
    boolean mIsTwoPane;
    onMovieSelectedListener mCallback;

    public ListFragmenter() {

    }

    public interface onMovieSelectedListener {
         void onMovieSelected(Bundle bundle);
    }



    @Override //Fragment
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("*** - ListFragment","onCreate");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);

        try {
            mCallback = (onMovieSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }



    }

    @Nullable
    @Override //Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        gridView = (GridView) rootView.findViewById(R.id.list_view);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        gridView.setEmptyView(emptyView);

        Log.v("**** - ListFragment", "onCreateView");
        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MVPpresenter = new movieListPresenter(getActivity().getContentResolver(), getActivity(), this, gridView);
        MVPpresenter.start();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MVPpresenter.onMovieSelected(position);
            }
        });
    }

    @Override  //Contract
    public void showMovieDetailsUI(Bundle movieBundle) {
        mCallback.onMovieSelected(movieBundle);

    }

    @Override //Contract
    public void inflateSortOptionsMenu(String sortPref) {
        View menuItemView = getActivity().findViewById(R.id.sort_by);
        final PopupMenu popup = new PopupMenu(getActivity(), menuItemView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        switch (sortPref) {
            case "popular":
                popup.getMenu().findItem(R.id.pop).setChecked(true);
                break;
            case "top_rated":
                popup.getMenu().findItem(R.id.top).setChecked(true);
                break;
            case "favorite":
                popup.getMenu().findItem(R.id.fav).setChecked(true);
                break;
        }

        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                MVPpresenter.onSortChanged(menuItem);
                return true;
            }
        });
    }

    @Override //Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.overflow_menu, menu);
    }

    @Override //Fragment
    public boolean onOptionsItemSelected(MenuItem item) {
        MVPpresenter.onSortByTapped();
        return true;
    }


}
