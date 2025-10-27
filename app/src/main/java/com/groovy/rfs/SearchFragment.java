package com.groovy.rfs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.groovy.rfs.API.MovieApiService;
import com.groovy.rfs.API.RetrofitUtils;
import com.groovy.rfs.Adapter.AllMoviesAdapter;
import com.groovy.rfs.Movie.MovieDetailActivity;
import com.groovy.rfs.model.Movie;
import com.groovy.rfs.model.SerResMovies;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements AllMoviesAdapter.OnMovieClickListener {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProgressBar loadingSpinner;
    private TextView tvNoResults;

    private AllMoviesAdapter moviesAdapter;
    private List<Movie> movieListData = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // 1. Ánh xạ Views
        searchView = view.findViewById(R.id.search_view);
        recyclerView = view.findViewById(R.id.search_results_recyclerview);
        loadingSpinner = view.findViewById(R.id.loading_spinner);
        tvNoResults = view.findViewById(R.id.tv_search_result);

        // 2. Cài đặt RecyclerView (Tái sử dụng AllMoviesAdapter)
        moviesAdapter = new AllMoviesAdapter(getContext(), this, movieListData);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 cột
        recyclerView.setAdapter(moviesAdapter);

        // 3. Cài đặt Listener cho SearchView
        setupSearchListener();
    }

    private void setupSearchListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            // Hàm này chạy khi người dùng nhấn "Enter" hoặc nút Tìm
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    performSearch(query.trim());
                    searchView.clearFocus(); // Ẩn bàn phím đi
                }
                return true; // Đã xử lý
            }



            // Hàm này chạy mỗi khi người dùng gõ 1 chữ
            @Override
            public boolean onQueryTextChange(String newText) {
                // (Bạn có thể tìm kiếm real-time ở đây,
                // nhưng onQueryTextSubmit dễ làm hơn cho người mới)
                return false; // Chưa xử lý
            }
        });

    }
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
        intent.putExtra("MOVIE_ID", movie.getIdMovie());
        startActivity(intent);
    }
    private void performSearch(String query) {
        // Hiển thị loading, ẩn kết quả cũ
        loadingSpinner.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvNoResults.setVisibility(View.GONE);
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        // Lấy ApiService
        MovieApiService apiService = retrofit.create(MovieApiService.class);

        // Gọi API (Không cần token)
        Call<SerResMovies> call = apiService.searchMovies(query);

        call.enqueue(new Callback<SerResMovies>() {
            @Override
            public void onResponse(Call<SerResMovies> call, Response<SerResMovies> response) {
                // Tắt loading
                loadingSpinner.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().getSuccess() == 1) {
                    List<Movie> results = response.body().getMovies();

                    if (results != null && !results.isEmpty()) {
                        // Tìm thấy kết quả
                        recyclerView.setVisibility(View.VISIBLE); // Hiện RecyclerView
                        movieListData.clear();
                        movieListData.addAll(results);
                        moviesAdapter.notifyDataSetChanged();
                    } else {
                        // Không tìm thấy kết quả
                        tvNoResults.setVisibility(View.VISIBLE); // Hiện "Không tìm thấy"
                        movieListData.clear();
                        moviesAdapter.notifyDataSetChanged(); // Xóa kết quả cũ
                    }
                } else {
                    // API trả về lỗi
                    Toast.makeText(getContext(), "Lỗi khi tìm kiếm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResMovies> call, Throwable t) {
                loadingSpinner.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}