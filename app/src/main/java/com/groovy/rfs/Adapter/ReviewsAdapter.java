package com.groovy.rfs.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.groovy.rfs.R;
import com.groovy.rfs.model.Review;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private Context context;
    private List<Review> reviews;

    private int currentUserId; // Biến lưu userId của app

    public interface OnReviewInteractionListener {
        void onDeleteReviewClicked(Review review, int position);
    }
    private OnReviewInteractionListener interactionListener;
    public ReviewsAdapter(Context context, List<Review> reviews, int currentUserId, OnReviewInteractionListener interactionListener) {
        this.context = context;
        this.reviews = reviews;
        this.currentUserId = currentUserId;
        this.interactionListener = interactionListener;
    }

    @NonNull
    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.tvUsername.setText(review.getUsername());
        holder.rbRating.setRating(review.getScore());
        holder.tvComment.setText(review.getComment());
        if (review.getIdUser() == currentUserId) { // Giả sử model có getUserId()
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                if (interactionListener != null) {
                    interactionListener.onDeleteReviewClicked(review, holder.getAdapterPosition());
                }
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnDelete.setOnClickListener(null); // Bỏ listener cũ
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView ivAvatar;
        ImageButton btnDelete;
        TextView tvUsername, tvComment;
        RatingBar rbRating;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
//            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvComment = itemView.findViewById(R.id.tv_comment);
            rbRating = itemView.findViewById(R.id.rb_rating_display);
            btnDelete = itemView.findViewById(R.id.btn_delete_review);
        }
    }
}
