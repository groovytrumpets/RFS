package com.groovy.rfs.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.groovy.rfs.R;
import com.groovy.rfs.model.User;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private Context context;
    private List<User> friendList;
    private OnFriendClickListener listener;
    public interface OnFriendClickListener {
        void onFriendClick(User friend);
    }

    public FriendListAdapter(Context context, List<User> friendList, OnFriendClickListener listener) {
        this.context = context;
        this.friendList = friendList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_friends, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.ViewHolder holder, int position) {
        User friend = friendList.get(position);
        if (friend == null) return;

        // Gán tên (dùng fullName nếu có, nếu không thì dùng username)
        String displayName = (friend.getFullName() != null && !friend.getFullName().isEmpty())
                ? friend.getFullName()
                : friend.getUsername();
        holder.tvUsername.setText(displayName);

        // Load Avatar
        Glide.with(context)
                .load(friend.getAvatar())
                .placeholder(R.mipmap.ic_user_defaut)
                .error(R.mipmap.ic_user_defaut)
                .circleCrop()
                .into(holder.ivAvatar);

        // Gán sự kiện click cho cả item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFriendClick(friend);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUsername;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.avatar);
            tvUsername = itemView.findViewById(R.id.friendName);
        }

    }
}
