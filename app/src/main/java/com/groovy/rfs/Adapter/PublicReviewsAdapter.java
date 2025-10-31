package com.groovy.rfs.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.groovy.rfs.R;
import com.groovy.rfs.model.PublicReview;

import java.util.List;

public class PublicReviewsAdapter extends RecyclerView.Adapter<PublicReviewsAdapter.ViewHolder>{
    private Context context;
    private List<PublicReview> reviews;
    private int currentUserId;
    private OnPublicReviewInteractionListener listener;
    public interface OnPublicReviewInteractionListener {
        void onItemClick(PublicReview review); // Click cả item
        void onEditClick(PublicReview review, int position); // Click nút sửa
        void onDeleteClick(PublicReview review, int position); // Click nút xóa
    }

    public PublicReviewsAdapter(Context context, int currentUserId, OnPublicReviewInteractionListener listener, List<PublicReview> reviews) {
        this.context = context;
        this.currentUserId = currentUserId;
        this.listener = listener;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public PublicReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_public_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicReviewsAdapter.ViewHolder holder, int position) {
        PublicReview review = reviews.get(position);

        // Gán dữ liệu
        holder.tvMovieTitleYear.setText(review.getMovie_title() + " (" + (review.getRelease_year() != null ? review.getRelease_year() : "N/A") + ")");
        holder.tvUsername.setText(review.getUsername());
        holder.rbScore.setRating(review.getScore());
        holder.tvComment.setText(review.getComment());

        Glide.with(context)
                .load(review.getAvatar()) // Lấy URL avatar từ model
                .placeholder(R.mipmap.ic_user_defaut) // Ảnh chờ
                .error(R.mipmap.ic_user_defaut)       // Ảnh lỗi
                .circleCrop() // Bo tròn
                .into(holder.ivAvatar); // ImageView trong ViewHolder
        Glide.with(context).load(review.getMovie_poster_url()).placeholder(R.drawable.placeholder_poster).into(holder.ivPoster);

        // Hiển thị nút sửa/xóa nếu là review của user hiện tại
        if (review.getUser_idUser() == currentUserId) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);

            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEditClick(review, holder.getAdapterPosition());
            });
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(review, holder.getAdapterPosition());
            });
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnEdit.setOnClickListener(null);
            holder.btnDelete.setOnClickListener(null);
        }

        // Click vào cả item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(review);
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitleYear, tvUsername, tvComment;
        ImageView ivAvatar, ivPoster;
        RatingBar rbScore;
        ImageButton btnEdit, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieTitleYear = itemView.findViewById(R.id.tv_movie_title_year);
            ivAvatar = itemView.findViewById(R.id.iv_user_avatar);
            tvUsername = itemView.findViewById(R.id.tv_review_username);
            rbScore = itemView.findViewById(R.id.rb_review_score);
            ivPoster = itemView.findViewById(R.id.iv_movie_poster);
            tvComment = itemView.findViewById(R.id.tv_review_comment);
            btnEdit = itemView.findViewById(R.id.btn_edit_public_review);
            btnDelete = itemView.findViewById(R.id.btn_delete_public_review);
        }
    }
}
