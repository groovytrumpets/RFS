package com.groovy.rfs.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.groovy.rfs.R;
import com.groovy.rfs.model.Notification;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder>{
    private Context context;
    private OnNotificationActionClickListener listener;
    private List<Notification> notifications;

    public interface OnNotificationActionClickListener {
        void onAcceptClick(Notification notification, int position);
        void onDeclineClick(Notification notification, int position);
        void onNotificationClick(Notification notification, int position);
    }

    public NotificationsAdapter(Context context, OnNotificationActionClickListener listener, List<Notification> notifications) {
        this.context = context;
        this.listener = listener;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        Glide.with(context)
                .load(notification.getSender_avatar()) // Lấy URL avatar từ model
                .placeholder(R.mipmap.ic_user_defaut) // Ảnh chờ
                .error(R.mipmap.ic_user_defaut)       // Ảnh lỗi
                .circleCrop() // Bo tròn
                .into(holder.avatar);
        holder.time.setText(notification.getCreate_date());

        String notificationText = "";
        switch (notification.getType()){
            case "friend_request":
                notificationText = "<b>" + notification.getSender_username() + "</b> sent you a friend request.";
                break;
            case "friend_accept":
                notificationText = "<b>" + notification.getSender_username() + "</b> accepted your invitation.";
                break;
            case "new_review":
                // (Bạn cần JOIN thêm Movie để lấy tên phim nếu muốn hiển thị ở đây)
                notificationText = "<b>" + notification.getSender_username() + "</b> just reviewed a movie.";
                break;
            default:
                notificationText = "Bạn có thông báo mới.";
        }
        holder.notification.setText(android.text.Html.fromHtml(notificationText));

        if ("friend_request".equals(notification.getType())){
            holder.actionBtns.setVisibility(View.VISIBLE);
            holder.accept.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAcceptClick(notification, holder.getAdapterPosition());
                }
            });
            holder.decline.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeclineClick(notification, holder.getAdapterPosition());
                }
            });
        }else holder.actionBtns.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(notification, holder.getAdapterPosition());
            }
        });
        //readed? handle UI
        if (notification.getIs_read() == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#303133")); // Màu nền chưa đọc
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT); // Màu nền đã đọc
        }




    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView notification;
        TextView time;
        Button accept,decline;
        LinearLayout actionBtns;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.iv_notification_avatar);
            notification = itemView.findViewById(R.id.tv_notification_text);
            time = itemView.findViewById(R.id.tv_notification_time);
            accept = itemView.findViewById(R.id.btn_accept_friend);
            decline = itemView.findViewById(R.id.btn_decline_friend);
            actionBtns = itemView.findViewById(R.id.ll_friend_request_actions);
        }
    }
}
