package com.groovy.rfs.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.groovy.rfs.R;
import com.groovy.rfs.User.UserProfileActivity;
import com.groovy.rfs.model.PublicList;

import java.util.List;

public class PublicListAdapter extends RecyclerView.Adapter<PublicListAdapter.ViewHolder>{
    private Context context;
    private List<PublicList> lists;
    private OnPublicListClickListener listener;
    public interface OnPublicListClickListener {
        void onListClick(PublicList list);
        void onUserNameClick(PublicList list);
    }

    public PublicListAdapter(Context context, OnPublicListClickListener listener, List<PublicList> lists) {
        this.context = context;
        this.listener = listener;
        this.lists = lists;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_public, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicListAdapter.ViewHolder holder, int position) {
        PublicList currentList = lists.get(position);

        holder.tvListName.setText(currentList.getList_name());
        holder.tvCreatorName.setText(currentList.getCreator_name());
        holder.tvDescription.setText(currentList.getDescription());

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.rvMoviesPreview.setLayoutManager(layoutManager);
        MoviePreviewAdapter previewAdapter = new MoviePreviewAdapter(context, currentList.getMovies_preview());
        holder.rvMoviesPreview.setAdapter(previewAdapter);

        if (currentList.getAvatar() != null && !currentList.getAvatar().isEmpty()) {
            Glide.with(context)
                    .load(currentList.getAvatar())
                    .circleCrop()
                    .into(holder.avatar);
        } else {
            holder.avatar.setImageResource(R.mipmap.ic_user_defaut);
        }
        holder.avatar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserNameClick(currentList);
            }
        });
        holder.tvCreatorName.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserNameClick(currentList);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onListClick(currentList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvListName, tvCreatorName, tvDescription;
        RecyclerView rvMoviesPreview;
        ImageView avatar;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvListName = itemView.findViewById(R.id.tv_public_list_name);
            tvCreatorName = itemView.findViewById(R.id.tv_creator_name);
            tvDescription = itemView.findViewById(R.id.tv_public_list_description);
            rvMoviesPreview = itemView.findViewById(R.id.rv_movies_preview);
            avatar = itemView.findViewById(R.id.iv_user_avatar);
        }
    }
}
