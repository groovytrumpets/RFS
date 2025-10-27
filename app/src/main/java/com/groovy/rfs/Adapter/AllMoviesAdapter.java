package com.groovy.rfs.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.groovy.rfs.R;
import com.groovy.rfs.model.Movie;

import java.util.List;

public class AllMoviesAdapter extends RecyclerView.Adapter<AllMoviesAdapter.MovieViewHolder>{
    private Context context;
    private List<Movie> movieList;

    private OnMovieClickListener movieClickListener; // Biến lưu trữ listener

     public interface OnMovieClickListener {
        void onMovieClick(Movie movie); // Phương thức sẽ được gọi khi item được click
    }

    public AllMoviesAdapter(Context context, OnMovieClickListener movieClickListener, List<Movie> movieList) {
        this.context = context;
        this.movieClickListener = movieClickListener;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo ViewHolder từ layout item_movie_card.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_card, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        // Lấy dữ liệu phim ở vị trí hiện tại
        Movie currentMovie = movieList.get(position);

        // Hiển thị tên phim
        holder.titleTextView.setText(currentMovie.getTitle());

        // Hiển thị ảnh poster (dùng Glide)
        Glide.with(context)
                .load(currentMovie.getPoster_url()) // URL ảnh
                .placeholder(R.drawable.placeholder_poster) // Ảnh chờ (tạo file placeholder_poster.xml trong drawable)
                .error(R.drawable.placeholder_poster) // Ảnh lỗi (dùng chung ảnh chờ)
                .into(holder.posterImageView);

        // TODO: Thêm holder.itemView.setOnClickListener(...) nếu muốn xử lý click
        holder.itemView.setOnClickListener(v -> {
            if (movieClickListener != null && currentMovie != null) {
                movieClickListener.onMovieClick(currentMovie);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Trả về tổng số phim
        return movieList != null ? movieList.size() : 0;
    }

    // ViewHolder để giữ các View con (ImageView và TextView)
    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        TextView titleTextView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.movie_poster_image);
            titleTextView = itemView.findViewById(R.id.movie_title_text);
        }
    }
    // (Tùy chọn) Hàm để cập nhật dữ liệu khi có dữ liệu mới từ API
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Movie> newMovieList) {
        this.movieList = newMovieList;
        notifyDataSetChanged(); // Báo cho RecyclerView vẽ lại
    }
}

