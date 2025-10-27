package com.groovy.rfs;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Use the {@link FilmsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilmsFragment extends Fragment implements AllMoviesAdapter.OnMovieClickListener {
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

    public FilmsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilmsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilmsFragment newInstance(String param1, String param2) {
        FilmsFragment fragment = new FilmsFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_films, container, false);

        allMoviesRecyclerView = view.findViewById(R.id.all_movies_recycler_view);

        // Khởi tạo danh sách (ban đầu rỗng)
        movieListData = new ArrayList<>();

        // Tạo Adapter với danh sách rỗng ban đầu
        moviesAdapter = new AllMoviesAdapter(getContext(),this ,movieListData);

        // Gán Adapter cho RecyclerView
        allMoviesRecyclerView.setAdapter(moviesAdapter);

        // (Không cần set LayoutManager ở đây nếu đã đặt trong XML)
        // GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        // allMoviesRecyclerView.setLayoutManager(gridLayoutManager);

        // Gọi hàm để tải dữ liệu phim từ API
        fetchMoviesFromApi();

        return view;
    }
    public void onMovieClick(Movie movie) {
        Log.d("FilmsFragment", "Clicked movie ID: " + movie.getIdMovie());
        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
        // Truyền ID phim qua Intent
        intent.putExtra("MOVIE_ID", movie.getIdMovie());
        startActivity(intent);
    }

    @Override
    public void onMovieLongClick(Movie movie, int position) {

    }

    private void fetchMoviesFromApi() {
        // TODO: Thay thế bằng code gọi API thật sự của bạn
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        Call<SerResMovies> call = apiService.getAllMovies();

        Log.d("API_FETCH", "Đang gọi API lấy danh sách phim...");

        call.enqueue(new Callback<SerResMovies>() {
            @Override
            public void onResponse(Call<SerResMovies> call, Response<SerResMovies> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SerResMovies serverResponse = response.body();
                    Log.d("API_FETCH", "Gọi API thành công. Success flag: " + serverResponse.getSuccess());

                    if (serverResponse.getSuccess() == 1) {
                        List<Movie> fetchedMovies = serverResponse.getMovies();
                        if (fetchedMovies != null && !fetchedMovies.isEmpty()) {
                            Log.d("API_FETCH", "Đã nhận được " + fetchedMovies.size() + " phim.");
                            Log.d("API_FETCH", "URL:  " + fetchedMovies.get(0).getPoster_url() + ".");

                            // ✅ CẬP NHẬT DỮ LIỆU VÀO ADAPTER
                            movieListData.clear(); // Xóa dữ liệu cũ
                            movieListData.addAll(fetchedMovies); // Thêm dữ liệu mới
                            moviesAdapter.notifyDataSetChanged(); // Báo RecyclerView vẽ lại
                        } else {
                            Log.d("API_FETCH", "Danh sách phim rỗng hoặc null.");
                            Toast.makeText(getContext(), "Không có phim nào.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // API báo lỗi logic (success = 0)
                        String message = serverResponse.getMessage() != null ? serverResponse.getMessage() : "Lỗi không xác định từ server.";
                        Log.e("API_FETCH", "API báo lỗi: " + message);
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Lỗi HTTP (404, 500...) hoặc response body rỗng
                    Log.e("API_FETCH", "Lỗi HTTP hoặc response rỗng. Code: " + response.code());
                    Toast.makeText(getContext(), "Lỗi tải dữ liệu phim (Code: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResMovies> call, Throwable t) {
                // Lỗi mạng hoặc lỗi khi parse JSON
                Log.e("API_FETCH", "Lỗi kết nối mạng: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // --- Dữ liệu giả để test giao diện ---
        movieListData.add(new Movie("null", "loading..."));
        movieListData.add(new Movie("null", "loading..."));
        movieListData.add(new Movie("Siêu Anh Hùng Trở Lại", "loading..."));
        moviesAdapter.notifyDataSetChanged(); // Cập nhật ngay với dữ liệu giả
        // --- Kết thúc dữ liệu giả ---
    }
}