package com.groovy.rfs.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.groovy.rfs.Movie.CastFragment;
import com.groovy.rfs.Movie.DetailsFragment;
import com.groovy.rfs.Movie.MovieDetailActivity;
import com.groovy.rfs.Movie.ReviewFragment;

public class DetalMovieViewAdapter extends FragmentStateAdapter {
    private int movieId;
    public DetalMovieViewAdapter(@NonNull MovieDetailActivity fragment, int movieId) {

        super(fragment);
        this.movieId = movieId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ReviewFragment.newInstance(movieId); // Tab đầu tiên
            case 1:
                return new DetailsFragment(); // Tab thứ hai
            case 2:
                return new CastFragment(); // Tab thứ ba
            default:
                return null; // Không nên xảy ra
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
