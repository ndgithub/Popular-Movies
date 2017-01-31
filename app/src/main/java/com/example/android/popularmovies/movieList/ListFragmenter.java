package com.example.android.popularmovies.movieList;


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
import android.widget.GridView;
import android.widget.TextView;

import com.example.android.popularmovies.R;

public class ListFragmenter extends Fragment implements MovieListContract.View  {

    TextView emptyView;
    movieListPresenter MVPpresenter;
    GridView gridView;
    View rootView;

    public ListFragmenter() {
    }

    @Override //Fragment
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override //Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_list, container, false);

        gridView = (GridView) rootView.findViewById(R.id.list_view);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        gridView.setEmptyView(emptyView);


        Log.v("***** - ListFragment", "onCreate");
        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MVPpresenter = new movieListPresenter(getActivity().getContentResolver(),getActivity(), this, gridView);
        MVPpresenter.start();
        Log.v("***** - ListFragment", "onActivityCreated");

    }

    @Override  //Contract
    public void showMovieDetailsUI(Intent intent) {
        startActivity(intent);
    }

    @Override //Contract
    public void inflateSortOptionsMenu(String sortPref) {
        View menuItemView = getActivity().findViewById(R.id.sort_by);
        final PopupMenu popup = new PopupMenu(getActivity(),menuItemView);
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

        Log.v("***** - ListFragment", "inflateSortOptionsMenu");
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
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        inflater.inflate(R.menu.overflow_menu, menu);
    }
    @Override //Fragment
    public boolean onOptionsItemSelected(MenuItem item) {
        MVPpresenter.onSortByTapped();
        return true;
    }
}
