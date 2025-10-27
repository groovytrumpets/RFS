package com.groovy.rfs.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

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
import com.groovy.rfs.Movie.MovieDetailActivity;
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
import retrofit2.converter.gson.GsonConverterFactory;

public class ListMovieActivity extends AppCompatActivity implements AllMoviesAdapter.OnMovieClickListener {
    private RecyclerView allMoviesRecyclerView;
    private AllMoviesAdapter moviesAdapter;
    private TextView list_name_tv;
    private ImageButton btn_cancel,btn_add;
    private List<Movie> movieListData = new ArrayList<>();; // Danh sách chứa dữ liệu phim
    private int listId;
    private String listName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_movie);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        list_name_tv = findViewById(R.id.list_name_tv);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_add = findViewById(R.id.btn_add_list);
        // 1. Nhận dữ liệu từ Intent (Trang ListUserActivity gửi qua)
        listId = getIntent().getIntExtra("LIST_ID", -1);
        listName = getIntent().getStringExtra("LIST_NAME");
        //Toast.makeText(this, "Đang mở list: " + listName, Toast.LENGTH_LONG).show();
        list_name_tv.setText(listName);
        btn_cancel.setOnClickListener(v -> {
            finish();
        });
        btn_add.setOnClickListener(v -> {
            // Mở trang SearchMovieActivity
            Intent intent = new Intent(ListMovieActivity.this, SearchMovieActivity.class);

            // Gửi ID của list HIỆN TẠI sang trang Search
            // Trang Search sẽ cần ID này để biết phải thêm phim vào list nào
            intent.putExtra("LIST_ID_TO_ADD_TO", listId);

            startActivity(intent);
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(listName); // Gán tiêu đề động
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút "Back"
        }
        // 3. Cài đặt RecyclerView (Y hệt trang Home/FilmsFragment)
        allMoviesRecyclerView = findViewById(R.id.movies_in_list_recyclerview);
        // Tái sử dụng Adapter, truyền "this" vì Activity này implement OnMovieClickListener
        moviesAdapter = new AllMoviesAdapter(this, this, movieListData);
        allMoviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // Hiển thị 3 cột
        allMoviesRecyclerView.setAdapter(moviesAdapter);

        // 4. Kiểm tra ID và gọi API
        if (listId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID danh sách", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu không có ID
        } else {
            fetchMoviesInList(listId);
        }

    }

    private void fetchMoviesInList(int listId) {
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        Call<SerResMovies> call = apiService.getMoviesInList(AuthUtils.getToken(this),listId);

        call.enqueue(new Callback<SerResMovies>() {
            @Override
            public void onResponse(Call<SerResMovies> call, Response<SerResMovies> response) {
                if (response.isSuccessful()&&response.body()!=null){
                    SerResMovies serverResponse = response.body();
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
                            Toast.makeText(ListMovieActivity.this, "Không có phim nào.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // API báo lỗi logic (success = 0)
                        String message = serverResponse.getMessage() != null ? serverResponse.getMessage() : "Lỗi không xác định từ server.";
                        Log.e("API_FETCH", "API báo lỗi: " + message);
                        Toast.makeText(ListMovieActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    // Lỗi HTTP (404, 500...) hoặc response body rỗng
                    Log.e("API_FETCH", "Lỗi HTTP hoặc response rỗng. Code: " + response.code());
                    Toast.makeText(ListMovieActivity.this, "Lỗi tải dữ liệu phim (Code: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResMovies> call, Throwable t) {
                Log.e("API_FETCH", "Lỗi kết nối mạng: " + t.getMessage(), t);
                Toast.makeText(ListMovieActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMovieClick(Movie movie) {
        Log.d("FilmsFragment", "Clicked movie ID: " + movie.getIdMovie());
        Intent intent = new Intent(this, MovieDetailActivity.class);
        // Truyền ID phim qua Intent
        intent.putExtra("MOVIE_ID", movie.getIdMovie());
        startActivity(intent);
    }

    @Override
    public void onMovieLongClick(Movie movie, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa '" + movie.getTitle() + "' khỏi danh sách này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Nếu người dùng bấm "Xóa" -> gọi API
                    performDeleteMovie(movie, position);
                })
                .setNegativeButton("Hủy", null) // "Hủy" thì không làm gì cả
                .show();
    }

    private void performDeleteMovie(Movie movie, int position) {
        String token = AuthUtils.getToken(this);
        if (token == null) { /* ... (xử lý lỗi token) ... */ return; }

        int movieId = movie.getIdMovie();
        // (listId đã được lấy trong onCreate)
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        Call<SerResBasic> call = apiService.deleteMovieFromList(token, listId, movieId);

        call.enqueue(new Callback<SerResBasic>() {
            @Override
            public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    Toast.makeText(ListMovieActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();

                    // (QUAN TRỌNG) Xóa phim khỏi danh sách (List) và cập nhật UI
                    movieListData.remove(position);
                    moviesAdapter.notifyItemRemoved(position);
                    moviesAdapter.notifyItemRangeChanged(position, movieListData.size());

                } else {
                    Toast.makeText(ListMovieActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<SerResBasic> call, Throwable t) {
                Toast.makeText(ListMovieActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}