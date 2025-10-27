package com.groovy.rfs.Movie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.groovy.rfs.API.MovieApiService;
import com.groovy.rfs.R;
import com.groovy.rfs.model.Movie;
import com.groovy.rfs.model.SerResMovieDetail;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailActivity extends AppCompatActivity {
    private ImageView detailWallpaper;
    private ImageView detailPoster; // Đổi tên từ imageView2 cho rõ
    private TextView detailTitle;
    private TextView detailYear;
    private TextView detailDirector;
    private TextView detailDuration;
    private Button detailTrailerBtn;
    private TextView detailDescription;
    private TextView detailRatingAvg;
    private TextView detailGenres;

    private ImageButton btn_cancel;

    private int movieId = -1; // Biến lưu ID phim
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        detailWallpaper = findViewById(R.id.detail_wallpaper);
        detailPoster = findViewById(R.id.detail_poster); // Sử dụng ID bạn đặt trong XML
        detailTitle = findViewById(R.id.detail_title);
        detailYear = findViewById(R.id.detail_year);
        detailDirector = findViewById(R.id.detail_director);
        detailDuration = findViewById(R.id.detail_duration);
        detailTrailerBtn = findViewById(R.id.detail_trailer_btn);
        detailDescription = findViewById(R.id.detail_description);
        detailRatingAvg = findViewById(R.id.detail_rating_avg);
        detailGenres = findViewById(R.id.detail_genres);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(v -> {
            finish();
        });


        // Lấy ID phim từ Intent
        movieId = getIntent().getIntExtra("MOVIE_ID", -1);
        Log.d("DETAIL_DEBUG", "Received Movie ID: " + movieId);
        if (movieId != -1) {
            fetchMovieDetails();
        }else{
            Toast.makeText(this, "Lỗi: Không tìm thấy ID phim.", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu không có ID
        }

    }

    private void fetchMovieDetails() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://khanhnnhe181337.id.vn/RFS/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        Log.d("DETAIL_DEBUG", "Calling fetchMovieDetails for ID: " + movieId);
        Call<SerResMovieDetail> call = apiService.getMovieDetails(movieId);

        call.enqueue(new Callback<SerResMovieDetail>() {
            @Override
            public void onResponse(Call<SerResMovieDetail> call, Response<SerResMovieDetail> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess() == 1) {
                    Movie movie = (Movie) response.body().getMovie();
                    if (movie != null) {
                        // Hiển thị dữ liệu lên các View đã ánh xạ
                        detailTitle.setText(movie.getTitle());
                        detailYear.setText(String.valueOf(movie.getRelease_year())); // Chuyển int thành String
                        detailDirector.setText(movie.getDirector());
                        detailDuration.setText(movie.getDuration());
                        detailDescription.setText(movie.getDescription());
                        detailRatingAvg.setText(String.valueOf(movie.getRatingAvg())); // Chuyển float thành String
                        detailGenres.setText(movie.getGenres());
                        detailRatingAvg.setText(String.valueOf(movie.getRatingAvg())); // Chuyển float thành String

                        detailTrailerBtn.setOnClickListener(v -> {
                            try {

                            String url = movie.getTrailer_url();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);
                            }catch (Exception e) {
                                // Xử lý nếu link bị lỗi hoặc không có app ( trình duyệt, youtube)
                                Toast.makeText(MovieDetailActivity.this, "Không thể mở trailer", Toast.LENGTH_SHORT).show();
                            }
                        });


                        // Dùng Glide để tải ảnh
                        Glide.with(MovieDetailActivity.this).load(movie.getPoster_url()).into(detailPoster);
                        Glide.with(MovieDetailActivity.this).load(movie.getWallpaper_url()).into(detailWallpaper);

                        // TODO: Hiển thị các thông tin khác (genres, country...)
                    }
                } else {
                    Toast.makeText(MovieDetailActivity.this, "Không thể tải chi tiết phim.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResMovieDetail> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}