package com.example.android.popularmovies.data;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Created by Nicky on 12/3/17.
 */

public interface UserPrefInterface {

    interface RemoveFavoriteCallback {

        void removeSuccess();

        void removeError();
    }


    interface AddFavoriteCallback {

        void addSuccess();

        void addError();
    }

    boolean isFavorite(Movie selectedMovie);

    void removeFromFavorites(Movie selectedMovie, UserPrefInterface.RemoveFavoriteCallback callback);

    void addToFavorites(Movie selectedMovie, UserPrefInterface.AddFavoriteCallback callback);

    String getSortPref();

    void changeSortPreference(String sortPref);

    ArrayList<Movie> getFavoritesList();

}
