package com.example.android.popularmovies.movieDetails;

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

import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Video;

import com.example.android.popularmovies.utils.PosterImageView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetailsFragment extends Fragment implements MovieDetailsContract.View {
    CastAdapter mCastAdapter;
    ArrayList<CastMember> castList;
    GridView castGridView;

    VideoAdapter mVideoAdapter;
    ArrayList<Video> videoList;
    GridView videoGridView;

    ReviewAdapter mReviewAdapter;
    ArrayList<Review> reviewList;
    ListView reviewListView;
    ImageView favButton;

    Movie selectedMovie;
    boolean favorite;
    String snackbarText;
    final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w780";
    View rootView;
    MovieDetailsPresenter detailsPresenter;

    public DetailsFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Bundle bundle = getArguments();
       //  Movie selectedMovie Parcels.unwrap(bundle);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        selectedMovie = Parcels.unwrap(getArguments().getParcelable("movie"));
Log.v("***** - DetailsFragment","" + selectedMovie.getTitle());
        detailsPresenter = new MovieDetailsPresenter(getActivity().getContentResolver(), getActivity(), this);
        //this.setTitle(selectedMovie.getTitle());

        ImageView backdropView = (ImageView) rootView.findViewById(R.id.backdrop);
        Picasso.with(getActivity()).load(BASE_IMAGE_URL + selectedMovie.getBackdropPath()).fit().centerCrop().into(backdropView);

        PosterImageView posterView = (PosterImageView) rootView.findViewById(R.id.poster);
        Picasso.with(getActivity()).load(BASE_IMAGE_URL + selectedMovie.getPosterPath()).fit().centerInside().into(posterView);

        TextView titleView = (TextView) rootView.findViewById(R.id.title);
        titleView.setText(selectedMovie.getTitle());

        TextView ratingView = (TextView) rootView.findViewById(R.id.rating);
        ratingView.setText(selectedMovie.getRating());

        TextView overviewView = (TextView) rootView.findViewById(R.id.overview);
        overviewView.setText(selectedMovie.getOverview());

        TextView dateView = (TextView) rootView.findViewById(R.id.date);
        dateView.setText("Release Date: " + formatDate(selectedMovie.getDate()));


        Log.v("***** - Detairagment2","" + selectedMovie.getTitle());

        if (detailsPresenter == null) {
            Log.v("***** - Detairagment2","detailsPresenter ess null");
        }
        Log.v("***** - Detairagment2","detailsPresenter: " + detailsPresenter);
        favorite = detailsPresenter.isFavorite(selectedMovie);
        showFavButton();


        castGridView = (GridView) rootView.findViewById(R.id.cast_grid_view);
        castList = new ArrayList<>();
        mCastAdapter = new CastAdapter(getActivity(), castList);
        castGridView.setAdapter(mCastAdapter);
        TextView castEmptyView = (TextView) rootView.findViewById(R.id.cast_empyt_view);
        castGridView.setEmptyView(castEmptyView);
        showCastList(castList);

        reviewListView = (ListView) rootView.findViewById(R.id.review_list_view);
        reviewList = new ArrayList<>();
        mReviewAdapter = new ReviewAdapter(getActivity(), reviewList);
        reviewListView.setAdapter(mReviewAdapter);
        TextView reviewEmptyView = (TextView) rootView.findViewById(R.id.review_empty_view);
        reviewListView.setEmptyView(reviewEmptyView);
        showReviewList(reviewList);


        videoGridView = (GridView) rootView.findViewById(R.id.video_grid_view);
        videoList = new ArrayList<>();
        mVideoAdapter = new VideoAdapter(getActivity(), videoList);
        videoGridView.setAdapter(mVideoAdapter);
        TextView videoEmptyView = (TextView) rootView.findViewById(R.id.video_empty_view);
        videoGridView.setEmptyView(videoEmptyView);
        showVideoList(videoList);


        videoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Video selectedVideo = (Video) mVideoAdapter.getItem(position);
                detailsPresenter.onTrailerClicked(selectedVideo);
            }
        });

        detailsPresenter.start(selectedMovie);
        return rootView;

    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(rootView.findViewById(R.id.scroll_view), message, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.go_to_favorites, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsPresenter.onGoToFavorites();
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
    public void showReviewList(ArrayList<Review> reviewList) {
        if (reviewList != null) {
            mReviewAdapter.clear();
            mReviewAdapter.addAll(reviewList);
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
        startActivity(intent);
    }

    private void showFavButton() {
        favButton = (ImageView) rootView.findViewById(R.id.fav_button);
        if (favorite) {
            favButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            favButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsPresenter.onFavoriteButtonClicked(favorite, selectedMovie);

            }
        });
    }

    public void updateFavorite(boolean fav) {
        if (fav) {
            favorite = true;
            showSnackbar("Added to Favorites");
        } else {
            favorite = false;
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
