package com.groovy.rfs.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.groovy.rfs.R; // Đảm bảo đúng R
import com.groovy.rfs.model.MoviePreview;

import java.util.List;
public class MoviePreviewAdapter extends RecyclerView.Adapter<MoviePreviewAdapter.ViewHolder>{
    private Context context;
    private List<MoviePreview> movies;

    public MoviePreviewAdapter(Context context, List<MoviePreview> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public MoviePreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_poster_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviePreviewAdapter.ViewHolder holder, int position) {
        MoviePreview movie = movies.get(position);
        Glide.with(context)
                .load(movie.getPoster_url())
                .placeholder(R.color.ic_logo_app_background) // Tạo màu placeholder
                .error(R.color.ic_logo_app_background)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_poster_preview);
            // KHÔNG setOnClickListener ở đây
        }
    }
}
