package com.groovy.rfs.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.groovy.rfs.API.MovieApiService;
import com.groovy.rfs.API.RetrofitUtils;
import com.groovy.rfs.Adapter.AllMoviesAdapter;
import com.groovy.rfs.R;
import com.groovy.rfs.authentication.AuthUtils;
import com.groovy.rfs.model.Movie;
import com.groovy.rfs.model.SerResBasic;
import com.groovy.rfs.model.SerResMovies;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchMovieActivity extends AppCompatActivity implements AllMoviesAdapter.OnMovieClickListener{
    private SearchView searchView;
    private ImageButton btnCancel, btnSave;
    private RecyclerView recyclerView;
    private AllMoviesAdapter moviesAdapter;
    private List<Movie> movieListData = new ArrayList<>();

    private int listIdToAdd;
    private ProgressBar loadingSpinner;
    private TextView tvNoResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_movie);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // 1. (QUAN TRỌNG) Lấy ID của List
        listIdToAdd = getIntent().getIntExtra("LIST_ID_TO_ADD_TO", -1);
        if (listIdToAdd == -1) {
            Toast.makeText(this, "Lỗi: Không có ID danh sách", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 2. Cài đặt (Giống SearchFragment)
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.search_results_recyclerview);
        loadingSpinner = findViewById(R.id.loading_spinner);
        tvNoResults = findViewById(R.id.tv_search_result);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        moviesAdapter = new AllMoviesAdapter(this, this, movieListData); // 'this' là listener
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(moviesAdapter);
        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListMovieActivity.class);
            intent.putExtra("LIST_ID", listIdToAdd);
            startActivity(intent);
            finish();
        });
        btnSave.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListMovieActivity.class);
            intent.putExtra("LIST_ID", listIdToAdd);
            startActivity(intent);
            finish();

        });
        setupSearchListener(); // (Code hàm này y hệt SearchFragment)
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
                    Toast.makeText(SearchMovieActivity.this, "Lỗi khi tìm kiếm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResMovies> call, Throwable t) {
                loadingSpinner.setVisibility(View.GONE);
                Toast.makeText(SearchMovieActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMovieClick(Movie movie) {
        String token = AuthUtils.getToken(this);
        if (token == null) { /* ... (xử lý lỗi token) ... */ return; }

        int movieId = movie.getIdMovie();

        // (Bạn cần thêm API này vào MovieApiService.java)
        // @POST("add_movie_to_list.php"), @Field("list_id"), @Field("movie_id")
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        Call<SerResBasic> call = apiService.addMovieToList(token, listIdToAdd, movieId);

        call.enqueue(new Callback<SerResBasic>() {
            @Override
            public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    Toast.makeText(SearchMovieActivity.this, "Đã thêm: " + movie.getTitle(), Toast.LENGTH_SHORT).show();

                    // THÊM THÀNH CÔNG (success == 1)
                    // Bạn có thể giữ người dùng ở lại trang Search để họ thêm phim khác
                } else {
                    SerResBasic res = response.body();
                    // THÊM THẤT BẠI (success == 0)
                    String message =res.getMessage() ;

                    // --- KIỂM TRA MESSAGE Ở ĐÂY ---
                    if ("already_exists".equals(message)) {
                        // LỖI DO ĐÃ TỒN TẠI
                        Toast.makeText(SearchMovieActivity.this, "Phim này đã có trong danh sách", Toast.LENGTH_SHORT).show();
                    } else {
                        // LỖI KHÁC (ví dụ: lỗi CSDL, sai token...)
                        Toast.makeText(SearchMovieActivity.this, "Thêm thất bại: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SerResBasic> call, Throwable t) {
                Toast.makeText(SearchMovieActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMovieLongClick(Movie movie, int position) {

    }
}