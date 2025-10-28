package com.groovy.rfs.Public;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
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
import com.groovy.rfs.Movie.MovieDetailActivity;
import com.groovy.rfs.R;
import com.groovy.rfs.User.ListMovieActivity;
import com.groovy.rfs.User.SearchMovieActivity;
import com.groovy.rfs.authentication.AuthUtils;
import com.groovy.rfs.model.Movie;
import com.groovy.rfs.model.SerResMovies;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListMoviePublicActivity extends AppCompatActivity implements AllMoviesAdapter.OnMovieClickListener {
    private RecyclerView allMoviesRecyclerView;
    private AllMoviesAdapter moviesAdapter;
    private TextView list_name_tv;
    private ImageButton btn_cancel;
    private List<Movie> movieListData = new ArrayList<>();; // Danh sách chứa dữ liệu phim
    private int listId;
    private String listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_movie_public);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        list_name_tv = findViewById(R.id.list_name_tv);
        btn_cancel = findViewById(R.id.btn_cancel);
        // 1. Nhận dữ liệu từ Intent (Trang ListUserActivity gửi qua)
        listId = getIntent().getIntExtra("LIST_ID", -1);
        listName = getIntent().getStringExtra("LIST_NAME");
        //Toast.makeText(this, "Đang mở list: " + listName, Toast.LENGTH_LONG).show();
        list_name_tv.setText(listName);
        btn_cancel.setOnClickListener(v -> {
            finish();
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
        Log.e("API_FETCH","listId: "+listId+"");
        Call<SerResMovies> call = apiService.getMoviesInListPublic(listId);

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
                            Toast.makeText(ListMoviePublicActivity.this, "Không có phim nào.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // API báo lỗi logic (success = 0)
                        String message = serverResponse.getMessage() != null ? serverResponse.getMessage() : "Lỗi không xác định từ server.";
                        Log.e("API_FETCH", "API báo lỗi: " + message);
                        Toast.makeText(ListMoviePublicActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    // Lỗi HTTP (404, 500...) hoặc response body rỗng
                    Log.e("API_FETCH", "Lỗi HTTP hoặc response rỗng. Code: " + response.code());
                    Toast.makeText(ListMoviePublicActivity.this, "Lỗi tải dữ liệu phim (Code: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResMovies> call, Throwable t) {
                Log.e("API_FETCH", "Lỗi kết nối mạng: " + t.getMessage(), t);
                Toast.makeText(ListMoviePublicActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    }
}