package com.groovy.rfs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayoutMediator;
import com.groovy.rfs.API.MovieApiService;
import com.groovy.rfs.API.RetrofitUtils;
import com.groovy.rfs.API.UserApiService;
import com.groovy.rfs.Adapter.AllMoviesAdapter;
import com.groovy.rfs.Adapter.HomeViewPagerAdapter;
import com.groovy.rfs.User.NotificationsActivity;
import com.groovy.rfs.authentication.AuthUtils;
import com.groovy.rfs.databinding.ActivityMainBinding;
import com.groovy.rfs.databinding.FragmentHomeBinding;
import com.groovy.rfs.model.Movie;
import com.groovy.rfs.model.SerResBasic;
import com.groovy.rfs.model.SerResMovies;
import com.groovy.rfs.model.SerResNorify;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private HomeViewPagerAdapter viewPagerAdapter;
    private FragmentHomeBinding binding;
    private TextView notificationBadge;
    private View notification_btn;

    private Handler notificationHandler = new Handler(Looper.getMainLooper());
    private Runnable notificationRunnable;
    private final int POLLING_INTERVAL = 1000;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        View view = binding.getRoot();



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Tạo và gán Adapter cho ViewPager2
        viewPagerAdapter = new HomeViewPagerAdapter(this); // Hoặc requireActivity()
        binding.viewPagerHome.setAdapter(viewPagerAdapter); // Giả sử ID là viewPagerHome

        notification_btn = view.findViewById(R.id.notification_button);
        notificationBadge = notification_btn.findViewById(R.id.tv_notification_badge);

        notification_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationsActivity.class);
            startActivity(intent);
        });
        notificationRunnable = new Runnable() {
            @Override
            public void run() {
                if (AuthUtils.isLoggedIn(getContext())){
        checkUnreadNotifications();

                }
                notificationHandler.postDelayed(this,POLLING_INTERVAL);
            }
        };
        // Kết nối TabLayout (giả sử ID là tabLayoutHome) với ViewPager2
        new TabLayoutMediator(binding.tabLayoutHome, binding.viewPagerHome, (tab, position) -> {
            // Đặt tên cho các tab
            switch (position) {
                case 0:
                    tab.setText("Films");
                    break;
                case 1:
                    tab.setText("Reviews");
                    break;
                case 2:
                    tab.setText("Lists");
                    break;
            }
        }).attach();
    }

    private void checkUnreadNotifications() {
        String token = AuthUtils.getToken(getContext());
        if (token == null) return;
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        UserApiService apiService = retrofit.create(UserApiService.class);
        Call<SerResNorify> call = apiService.getUnreadNotificationCount(token);

        call.enqueue(new Callback<SerResNorify>() {
            @Override
            public void onResponse(Call<SerResNorify> call, Response<SerResNorify> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    updateBadgeUI(response.body().getUnread_count());
                }
            }

            @Override
            public void onFailure(Call<SerResNorify> call, Throwable t) {

            }
        });


//        int unreadCount = 3;
//
//        if (getContext() == null) return;
//
//        if (unreadCount > 0) {
//            notificationBadge.setText(String.valueOf(unreadCount));
//            notificationBadge.setVisibility(View.VISIBLE);
//        } else {
//            notificationBadge.setVisibility(View.GONE);
//        }
    }

    private void updateBadgeUI(int count) {
        if (notificationBadge == null) return;
        //Log.d("API_TEST", "count: " + count + "");
        if (count > 0) {
            notificationBadge.setText(String.valueOf(count));
            notificationBadge.setVisibility(View.VISIBLE);
        } else {
            notificationBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        notificationHandler.post(notificationRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        notificationHandler.removeCallbacks(notificationRunnable);
    }
}