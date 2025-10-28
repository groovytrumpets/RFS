package com.groovy.rfs.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.groovy.rfs.R;
import com.groovy.rfs.model.PublicList;

import java.util.List;

public class PublicListAdapter extends RecyclerView.Adapter<PublicListAdapter.ViewHolder>{
    private Context context;
    private List<PublicList> lists;
    private OnPublicListClickListener listener;
    public interface OnPublicListClickListener {
        void onListClick(PublicList list);
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
        holder.tvCreatorName.setText("by " + currentList.getCreator_name());
        holder.tvDescription.setText(currentList.getDescription());

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
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvListName = itemView.findViewById(R.id.tv_public_list_name);
            tvCreatorName = itemView.findViewById(R.id.tv_creator_name);
            tvDescription = itemView.findViewById(R.id.tv_public_list_description);
        }
    }
}
