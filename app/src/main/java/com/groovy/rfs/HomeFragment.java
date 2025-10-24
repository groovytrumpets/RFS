package com.groovy.rfs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groovy.rfs.Adapter.AllMoviesAdapter;
import com.groovy.rfs.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private RecyclerView allMoviesRecyclerView;
    private AllMoviesAdapter moviesAdapter;
    private List<Movie> movieListData; // Danh sách chứa dữ liệu phim

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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        allMoviesRecyclerView = view.findViewById(R.id.all_movies_recycler_view);

        // Khởi tạo danh sách (ban đầu rỗng)
        movieListData = new ArrayList<>();

        // Tạo Adapter với danh sách rỗng ban đầu
        moviesAdapter = new AllMoviesAdapter(getContext(), movieListData);

        // Gán Adapter cho RecyclerView
        allMoviesRecyclerView.setAdapter(moviesAdapter);

        // (Không cần set LayoutManager ở đây nếu đã đặt trong XML)
        // GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        // allMoviesRecyclerView.setLayoutManager(gridLayoutManager);

        // Gọi hàm để tải dữ liệu phim từ API
        fetchMoviesFromApi();

        return view;
    }

    // Hàm ví dụ để gọi API (dùng Retrofit)
    private void fetchMoviesFromApi() {
        // TODO: Thay thế bằng code gọi API thật sự của bạn
        // Ví dụ:
        /*
        ApiClient.getApiService().getAllMovies().enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movieListData.clear(); // Xóa dữ liệu cũ (nếu cần)
                    movieListData.addAll(response.body()); // Thêm dữ liệu mới
                    moviesAdapter.notifyDataSetChanged(); // Báo Adapter cập nhật
                } else {
                    // Xử lý lỗi
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                // Xử lý lỗi mạng
            }
        });
        */

        // --- Dữ liệu giả để test giao diện ---
        movieListData.add(new Movie("Phim Ma Cà Rồng", "URL_POSTER_1"));
        movieListData.add(new Movie("Hành Động Kịch Tính", "URL_POSTER_2"));
        movieListData.add(new Movie("Siêu Anh Hùng Trở Lại", "URL_POSTER_3"));
        movieListData.add(new Movie("Tình Cảm Lãng Mạn", "URL_POSTER_4"));
        movieListData.add(new Movie("Phim Hoạt Hình Vui Nhộn", "URL_POSTER_5"));
        moviesAdapter.notifyDataSetChanged(); // Cập nhật ngay với dữ liệu giả
        // --- Kết thúc dữ liệu giả ---
    }
}