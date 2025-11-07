package com.groovy.rfs.Adapter;

import static android.view.View.GONE;

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
import com.groovy.rfs.model.User;

import java.util.List;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder> {
    private Context context;
    private List<User> userList;
    private OnUserClickListener listener;
    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public UserSearchAdapter(Context context, OnUserClickListener listener, List<User> userList) {
        this.context = context;
        this.listener = listener;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_friends, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserSearchAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);
        if (user == null) return;
        holder.friendStatus.setVisibility(GONE);
        holder.username.setText(user.getUsername());
        Glide.with(context)
                .load(user.getAvatar())
                .placeholder(R.mipmap.ic_user_defaut)
                .error(R.mipmap.ic_user_defaut)
                .circleCrop()
                .into(holder.avatar);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView username,friendStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            username = itemView.findViewById(R.id.friendName);
            friendStatus = itemView.findViewById(R.id.tv_notification_time);

        }
    }


}
