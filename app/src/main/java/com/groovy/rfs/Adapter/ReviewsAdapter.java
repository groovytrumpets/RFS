package com.groovy.rfs.Adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
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
import com.groovy.rfs.model.Review;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private SparseBooleanArray expandedState = new SparseBooleanArray();
    private static final int COLLAPSED_MAX_LINES = 4;
    private Context context;
    private List<Review> reviews;

    private int currentUserId; // Biến lưu userId của app

    public interface OnReviewInteractionListener {
        void onDeleteReviewClicked(Review review, int position);
        void onEditReviewClicked(Review review, int position);
        void onUsernameClick(Review review);
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


        Glide.with(context)
                .load(review.getAvatar()) // Lấy URL avatar từ model
                .placeholder(R.mipmap.ic_user_defaut) // Ảnh chờ
                .error(R.mipmap.ic_user_defaut)       // Ảnh lỗi
                .circleCrop() // Bo tròn
                .into(holder.ivAvatar); // ImageView trong ViewHolder
        final boolean isExpanded = expandedState.get(position, false);
        if (isExpanded) {
            holder.tvComment.setMaxLines(Integer.MAX_VALUE); // Hiện hết
        } else {
            holder.tvComment.setMaxLines(COLLAPSED_MAX_LINES); // Chỉ hiện 3 dòng
        }
        holder.tvUsername.setOnClickListener(v -> {
            if (interactionListener != null) {
                interactionListener.onUsernameClick(review);
            }
        });
        holder.ivAvatar.setOnClickListener(v -> {
            if (interactionListener != null) {
                interactionListener.onUsernameClick(review);
            }
        });
        // Luôn set ellipsize để dấu "..." hiện đúng
        holder.tvComment.setEllipsize(android.text.TextUtils.TruncateAt.END);

        // Đặt OnClickListener cho comment TextView
        holder.tvComment.setOnClickListener(v -> {
            // Đảo trạng thái mở rộng
            boolean newState = !expandedState.get(position, false);
            expandedState.put(position, newState);
            // Quan trọng: Chỉ cập nhật item này, không cần notifyDataSetChanged()
            notifyItemChanged(holder.getAdapterPosition());
        });
        if (review.getIdUser() == currentUserId) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                if (interactionListener != null) {
                    interactionListener.onDeleteReviewClicked(review, holder.getAdapterPosition());
                }
            });
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> {
                if (interactionListener != null) {
                    interactionListener.onEditReviewClicked(review, holder.getAdapterPosition());
                }
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnDelete.setOnClickListener(null); // Bỏ listener cũ
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnEdit.setOnClickListener(null); // Bỏ listener cũ
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        ImageButton btnDelete,btnEdit;
        TextView tvUsername, tvComment;
        RatingBar rbRating;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvComment = itemView.findViewById(R.id.tv_comment);
            rbRating = itemView.findViewById(R.id.rb_rating_display);
            btnDelete = itemView.findViewById(R.id.btn_delete_review);
            btnEdit = itemView.findViewById(R.id.btn_edit_review);
        }
    }
}
