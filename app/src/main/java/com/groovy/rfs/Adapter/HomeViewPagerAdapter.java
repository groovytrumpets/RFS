package com.groovy.rfs.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.groovy.rfs.FilmsFragment;
import com.groovy.rfs.ListsFragment;
import com.groovy.rfs.ReviewsFragment;

public class HomeViewPagerAdapter extends FragmentStateAdapter {
    public HomeViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FilmsFragment(); // Tab đầu tiên
            case 1:
                return new ReviewsFragment(); // Tab thứ hai (Tạo Fragment rỗng)
            case 2:
                return new ListsFragment(); // Tab thứ ba (Tạo Fragment rỗng)
            default:
                return new FilmsFragment(); // Mặc định
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
