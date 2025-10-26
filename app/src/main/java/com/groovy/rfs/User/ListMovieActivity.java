package com.groovy.rfs.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.groovy.rfs.Adapter.AllMoviesAdapter;
import com.groovy.rfs.Movie.MovieDetailActivity;
import com.groovy.rfs.R;
import com.groovy.rfs.model.Movie;
import java.util.List;

public class ListMovieActivity extends AppCompatActivity implements AllMoviesAdapter.OnMovieClickListener {
    private RecyclerView allMoviesRecyclerView;
    private AllMoviesAdapter moviesAdapter;
    private List<Movie> movieListData; // Danh sách chứa dữ liệu phim
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
        // 1. Nhận dữ liệu từ Intent (Trang ListUserActivity gửi qua)
        listId = getIntent().getIntExtra("LIST_ID", -1);
        listName = getIntent().getStringExtra("LIST_NAME");

        // 2. Cài đặt Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
//            fetchMoviesInList(listId);
        }

    }

    @Override
    public void onMovieClick(Movie movie) {
        Log.d("FilmsFragment", "Clicked movie ID: " + movie.getIdMovie());
        Intent intent = new Intent(this, MovieDetailActivity.class);
        // Truyền ID phim qua Intent
        intent.putExtra("MOVIE_ID", movie.getIdMovie());
        startActivity(intent);
    }
}