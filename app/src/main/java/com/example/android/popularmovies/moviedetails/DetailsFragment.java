package com.example.android.popularmovies.moviedetails;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
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

import com.example.android.popularmovies.data.MovieRepo;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.UserPrefImpl;
import com.example.android.popularmovies.data.Video;

import com.example.android.popularmovies.data.remote.MovieServiceApiImpl;
import com.example.android.popularmovies.movielist.MainActivity;
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
    ListView mReviewListView;
    ImageView mFavButton;

    Movie mSelectedMovie;
    final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w780";
    View mRootView;
    MovieDetailsContract.UserActionsListener mDetailsPresenter;
    onGoToFavoritesListener mCallback;

    public DetailsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("***", "DetailsFragment: onCreate");
        mDetailsPresenter = new MovieDetailsPresenter(new MovieRepo(new UserPrefImpl(getActivity().getContentResolver(), getActivity()), new MovieServiceApiImpl(getActivity())), this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (ActivityUtils.isTwoPane(getActivity())) {
            try {
                mCallback = (onGoToFavoritesListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement Ongotofaveslistener");

            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("movie", Parcels.wrap(mSelectedMovie));
    }

    public interface onGoToFavoritesListener {
        void onGoToFavoritesList();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("***", "onStart details fragment");
        mDetailsPresenter.start(mSelectedMovie);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_details, container, false);
        if (getArguments() != null) {
            mSelectedMovie = Parcels.unwrap(getArguments().getParcelable("movie"));
        }

        mFavButton = (ImageView) mRootView.findViewById(R.id.fav_button);
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDetailsPresenter.onFavoriteButtonClicked();
            }
        });


        return mRootView;
    }


    @Override
    public void showActivityTitle(String title) {
        if (!ActivityUtils.isTwoPane(getActivity())) {
            getActivity().setTitle(mSelectedMovie.getTitle());
        }
    }

    @Override
    public void showTitle(String title) {
        TextView titleView = (TextView) mRootView.findViewById(R.id.title);
        titleView.setText(title);
    }

    @Override
    public void showBackdrop(String path) {
        ImageView backdropView = (ImageView) mRootView.findViewById(R.id.backdrop);
        Picasso.with(getActivity()).load(BASE_IMAGE_URL + path).fit().centerCrop().into(backdropView);
    }

    @Override
    public void showPoster(String path) {
        PosterImageView posterView = (PosterImageView) mRootView.findViewById(R.id.poster);
        Picasso.with(getActivity()).load(BASE_IMAGE_URL + mSelectedMovie.getPosterPath()).fit().centerInside().into(posterView);
    }

    @Override
    public void showRating(String rating) {
        TextView ratingView = (TextView) mRootView.findViewById(R.id.rating);
        ratingView.setText(rating);
    }

    @Override
    public void showOverview(String overview) {
        TextView overviewView = (TextView) mRootView.findViewById(R.id.overview);
        overviewView.setText(overview);
    }

    @Override
    public void showDate(String date) {
        TextView dateView = (TextView) mRootView.findViewById(R.id.date);
        dateView.setText(getString(R.string.release_date) + formatDate(date));
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

    @Override
    public void showCastList(ArrayList<CastMember> castList) {

        mCastGridView = (GridView) mRootView.findViewById(R.id.cast_grid_view);
        mCastList = new ArrayList<>();
        mCastAdapter = new CastAdapter(getActivity(), mCastList);

        mCastGridView.setAdapter(mCastAdapter);
        TextView castEmptyView = (TextView) mRootView.findViewById(R.id.cast_empyt_view);
        mCastGridView.setEmptyView(castEmptyView);

        if (castList != null) {
            mCastAdapter.clear();
            mCastAdapter.addAll(castList);
        }
    }

    @Override
    public void showReviewList(ArrayList<Review> mReviewList) {

        mReviewListView = (ListView) mRootView.findViewById(R.id.review_list_view);
        mReviewList = new ArrayList<>();
        mReviewAdapter = new ReviewAdapter(getActivity(), mReviewList);
        mReviewListView.setAdapter(mReviewAdapter);
        TextView reviewEmptyView = (TextView) mRootView.findViewById(R.id.review_empty_view);
        mReviewListView.setEmptyView(reviewEmptyView);


        if (mReviewList != null) {
            mReviewAdapter.clear();
            mReviewAdapter.addAll(mReviewList);
        }
    }

    @Override
    public void showTrailersList(ArrayList<Video> videoList) {

        mVideoGridView = (GridView) mRootView.findViewById(R.id.video_grid_view);
        mVideoList = new ArrayList<>();
        mVideoAdapter = new VideoAdapter(getActivity(), mVideoList);
        mVideoGridView.setAdapter(mVideoAdapter);
        TextView videoEmptyView = (TextView) mRootView.findViewById(R.id.video_empty_view);
        mVideoGridView.setEmptyView(videoEmptyView);

        mVideoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Video selectedVideo = (Video) mVideoAdapter.getItem(position);
                mDetailsPresenter.onTrailerClicked(selectedVideo);
            }
        });


        if (videoList != null) {
            mVideoAdapter.clear();
            mVideoAdapter.addAll(videoList);
        }
    }


    @Override
    public void showTrailer(String vidKey) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + vidKey));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + vidKey));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    @Override
    public void goToFavorites() {
        if (ActivityUtils.isTwoPane(getActivity())) {
            mCallback.onGoToFavoritesList();
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }


    public void updateFavButtonImage(boolean isFavorite) {
        if (isFavorite) {
            mFavButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            mFavButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }


    public void notifyUserFavStatusChanged(boolean isFavorite) {
        if (isFavorite) {
            showSnackbar(getString(R.string.add_favorites));
        } else {
            showSnackbar(getString(R.string.remove_favorites));
        }
    }

    @Override
    public void notifyUserErrorAddingFav() {
        Toast.makeText(getActivity(), getString(R.string.error_adding_fav), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyUserErrorRemovingFav() {
        Toast.makeText(getActivity(), getString(R.string.error_removing_fav), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyUserErrorLoadingCast() {
        Toast.makeText(getActivity(), R.string.error_retrieving_cast_data, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void notifyUserErrorLoadingTrailers() {
        Toast.makeText(getActivity(), R.string.videos_unavailable, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyUserErrorLoadingReviews() {
        Toast.makeText(getActivity(), R.string.reviews_unavailable, Toast.LENGTH_SHORT).show();

    }

}
