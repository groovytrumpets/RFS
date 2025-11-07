package com.groovy.rfs.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.groovy.rfs.R;
import com.groovy.rfs.model.Reports;

import java.util.List;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ViewHolder> {
    private Context context;
    private List<Reports> reportList;

    public ReportListAdapter(Context context, List<Reports> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_report, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReportListAdapter.ViewHolder holder, int position) {
        Reports report = reportList.get(position);
        if (report == null) return;
        holder.tvStatus.setText(report.getReason()+": "+"\"" + report.getReported_comment() + "\"");

        // Đặt text và màu cho Status
        if ("pending".equalsIgnoreCase(report.getStatus())) {
            holder.tvUsername.setText(report.getMovie_title()+": Pending");
            holder.tvUsername.setTextColor(Color.parseColor("#FFC107")); // Màu Vàng
        } else {
            holder.tvUsername.setText(report.getMovie_title()+": Approved");
            holder.tvUsername.setTextColor(Color.parseColor("#4CAF50")); // Màu Xanh lá
        }
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUsername;
        TextView tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.avatar);
            tvUsername = itemView.findViewById(R.id.friendName);
            tvStatus = itemView.findViewById(R.id.tv_notification_time);

        }
    }
}
