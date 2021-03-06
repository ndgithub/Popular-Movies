package com.example.android.popularmovies.movielist;

import android.content.Context;
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
import com.example.android.popularmovies.data.ReposHolder;
import com.example.android.popularmovies.data.UserPrefImpl;
import com.example.android.popularmovies.data.remote.MovieServiceApiImpl;
import com.example.android.popularmovies.utils.ActivityUtils;

import java.util.ArrayList;

public class MainFragment extends Fragment implements MovieListContract.View {

    private TextView mEmptyView;
    private GridView mGridView;
    private ListFragmentInterface mListFragmentImpl;
    private MovieAdapter mMovieAdapter;
    private ArrayList<Movie> mMovieList;
    private Context mContext;

    private MovieListPresenter mPresenter;

    public MainFragment() {
    }

    public interface ListFragmentInterface {
        void onMovieSelected();
        void onSortChanged();
        void onListFragmentShowing();
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mPresenter = new MovieListPresenter(ReposHolder.getMovieRepo(new UserPrefImpl(getActivity().getContentResolver(), mContext),
                new MovieServiceApiImpl(mContext)), this);

    }

    @Override
    public void onResume() {
        super.onResume();
        //Tell activity to set showListfragment to true; in case orientation changes on phone layout
        mListFragmentImpl.onListFragmentShowing();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);

        try {
            mListFragmentImpl = (ListFragmentInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ListFragmentInterface");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mMovieList = new ArrayList<>();
        mMovieAdapter = new MovieAdapter(getContext(), mMovieList);

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.list_view);
        mGridView.setAdapter(mMovieAdapter);
        mGridView.setEmptyView(mEmptyView);
        mEmptyView = (TextView) rootView.findViewById(R.id.empty_view);

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("***", "start Presenter");
        mPresenter.start();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.onMovieSelected(position);
            }
        });
    }

    @Override  //View Interface Method
    public void showMovieDetailsUI() {
        if (!mMovieList.isEmpty()) {
            mListFragmentImpl.onMovieSelected();
        }
    }

    @Override //View Interface Method
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
                mPresenter.onSortChanged(menuItem);
                return true;
            }
        });
    }

    @Override
    public void showMovieList(ArrayList<Movie> list) {
        mMovieAdapter.clear();
        mMovieList = list;
        mMovieAdapter.addAll(mMovieList);
//        if (ActivityUtils.isTwoPane(mContext)) {
//            if (!list.isEmpty()) {
//                //mPresenter.showFirst();
//            }
//        }
    }

    @Override //Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.overflow_menu, menu);
    }

    @Override //Fragment
    public boolean onOptionsItemSelected(MenuItem item) {
        mPresenter.onSortByTapped();
        return true;
    }

    public void onSortChanged() {
        mListFragmentImpl.onSortChanged();
    }


}
