package com.example.android.popularmovies.moviedetails;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.CastMember;
import com.example.android.popularmovies.data.Movie;

import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Video;

import com.example.android.popularmovies.utils.ActivityUtils;
import com.example.android.popularmovies.utils.PosterImageView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetailsFragment extends Fragment implements MovieDetailsContract.View {
    CastAdapter mCastAdapter;
    ArrayList<CastMember> mCastList;
    GridView mCastGridView;

    VideoAdapter mVideoAdapter;
    ArrayList<Video> mVideoList;
    GridView mVideoGridView;

    ReviewAdapter mReviewAdapter;
    ArrayList<Review> mReviewList;
    ListView mReviewListView;
    ImageView mFavButton;

    Movie mSelectedMovie;
    boolean mFavorite;
    final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w780";
    View mRootView;
    MovieDetailsPresenter mDetailsPresenter;
    onGoToFavoritesListener mCallback;

    public DetailsFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    //TODO: Why using deprecated method
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (ActivityUtils.isTwoPane(getActivity())) {
            try {
                mCallback = (onGoToFavoritesListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement Ongotofaveslistent");

            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("movie",Parcels.wrap(mSelectedMovie));
    }

    public interface onGoToFavoritesListener {
        void onGoToFavoritesList();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        mRootView = inflater.inflate(R.layout.fragment_details, container, false);
        mDetailsPresenter = new MovieDetailsPresenter(getActivity().getContentResolver(), getActivity(), this);

        mSelectedMovie = Parcels.unwrap(getArguments().getParcelable("movie"));

            if (!ActivityUtils.isTwoPane(getActivity())) {
                getActivity().setTitle(mSelectedMovie.getTitle());
            }
            ImageView backdropView = (ImageView) mRootView.findViewById(R.id.backdrop);
            Picasso.with(getActivity()).load(BASE_IMAGE_URL + mSelectedMovie.getBackdropPath()).fit().centerCrop().into(backdropView);

            PosterImageView posterView = (PosterImageView) mRootView.findViewById(R.id.poster);
            Picasso.with(getActivity()).load(BASE_IMAGE_URL + mSelectedMovie.getPosterPath()).fit().centerInside().into(posterView);

            TextView titleView = (TextView) mRootView.findViewById(R.id.title);
            titleView.setText(mSelectedMovie.getTitle());

            TextView ratingView = (TextView) mRootView.findViewById(R.id.rating);
            ratingView.setText(mSelectedMovie.getRating());

            TextView overviewView = (TextView) mRootView.findViewById(R.id.overview);
            overviewView.setText(mSelectedMovie.getOverview());

            TextView dateView = (TextView) mRootView.findViewById(R.id.date);
            dateView.setText("Release Date: " + formatDate(mSelectedMovie.getDate()));

            mFavorite = mDetailsPresenter.isFavorite(mSelectedMovie);
            showFavButton();


        mCastGridView = (GridView) mRootView.findViewById(R.id.cast_grid_view);
        mCastList = new ArrayList<>();
            mCastAdapter = new CastAdapter(getActivity(), mCastList);
        mCastGridView.setAdapter(mCastAdapter);
            TextView castEmptyView = (TextView) mRootView.findViewById(R.id.cast_empyt_view);
        mCastGridView.setEmptyView(castEmptyView);
            showCastList(mCastList);

            mReviewListView = (ListView) mRootView.findViewById(R.id.review_list_view);
            mReviewList = new ArrayList<>();
            mReviewAdapter = new ReviewAdapter(getActivity(), mReviewList);
            mReviewListView.setAdapter(mReviewAdapter);
            TextView reviewEmptyView = (TextView) mRootView.findViewById(R.id.review_empty_view);
            mReviewListView.setEmptyView(reviewEmptyView);
            showReviewList(mReviewList);


            mVideoGridView = (GridView) mRootView.findViewById(R.id.video_grid_view);
        mVideoList = new ArrayList<>();
            mVideoAdapter = new VideoAdapter(getActivity(), mVideoList);
            mVideoGridView.setAdapter(mVideoAdapter);
            TextView videoEmptyView = (TextView) mRootView.findViewById(R.id.video_empty_view);
            mVideoGridView.setEmptyView(videoEmptyView);
            showVideoList(mVideoList);


            mVideoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Video selectedVideo = (Video) mVideoAdapter.getItem(position);
                    mDetailsPresenter.onTrailerClicked(selectedVideo);
                }
            });

        mDetailsPresenter.start(mSelectedMovie);
        return mRootView;
        }




    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(mRootView.findViewById(R.id.scroll_view), message, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.go_to_favorites, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDetailsPresenter.onGoToFavorites();
            }
        });
        snackbar.show();
    }

    private String formatDate(String inputDate) {
        Date releaseDate;
        String newDateString;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy ");
        try {
            releaseDate = df.parse(inputDate);
            newDateString = formatter.format(releaseDate);
            return newDateString;
        } catch (ParseException e) {
            e.printStackTrace();
            newDateString = null;
            return newDateString;
        }
    }


    @Override
    public void showCastList(ArrayList<CastMember> castList) {
        if (castList != null) {
            mCastAdapter.clear();
            mCastAdapter.addAll(castList);
        }
    }

    @Override
    public void showReviewList(ArrayList<Review> mReviewList) {
        if (mReviewList != null) {
            mReviewAdapter.clear();
            mReviewAdapter.addAll(mReviewList);
        }
    }

    @Override
    public void showVideoList(ArrayList<Video> videoList) {
        if (videoList != null) {
            mVideoAdapter.clear();
            mVideoAdapter.addAll(videoList);
        }
    }

    @Override
    public void showTrailer(Intent appIntent, Intent webIntent) {
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    @Override
    public void showFavorites(Intent intent) {
        if (ActivityUtils.isTwoPane(getActivity())) {
            mCallback.onGoToFavoritesList();
        } else {
            startActivity(intent);
        }
    }

    private void showFavButton() {
        mFavButton = (ImageView) mRootView.findViewById(R.id.fav_button);
        if (mFavorite) {
            mFavButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            mFavButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDetailsPresenter.onFavoriteButtonClicked(mFavorite, mSelectedMovie);

            }
        });
    }

    public void updateFavorite(boolean fav) {
        if (fav) {
            mFavorite = true;
            showSnackbar("Added to Favorites");
        } else {
            mFavorite = false;
            showSnackbar("Removed From Favorites");
        }
        showFavButton();
    }

    @Override
    public void showAddError() {
        Toast.makeText(getActivity(), "Didn't work! Movie not added to favorites", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRemoveError() {
        Toast.makeText(getActivity(), "Didn't work! Movie not removed from favorites", Toast.LENGTH_SHORT).show();
    }


}
