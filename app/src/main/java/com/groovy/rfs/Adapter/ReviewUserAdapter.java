package com.groovy.rfs.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.groovy.rfs.R;

public class ReviewUserAdapter extends RecyclerView.Adapter<ReviewUserAdapter.ViewHolder> {
    @NonNull
    @Override
    public ReviewUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewUserAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
