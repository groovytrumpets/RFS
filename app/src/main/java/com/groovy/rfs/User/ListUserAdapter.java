package com.groovy.rfs.User;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.groovy.rfs.R;
import com.groovy.rfs.model.MovieList;

import java.util.List;

public class ListUserAdapter extends BaseAdapter {

    private List<MovieList> mvList;
    private Context context;

    public ListUserAdapter(Context context, List<MovieList> mvList) {
        this.context = context;
        this.mvList = mvList;
    }

    @Override
    public int getCount() {
        return mvList.size();
    }

    @Override
    public Object getItem(int position) {
        return mvList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mvList.get(position).getIdMovie_collections();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        listUserHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_list_user, null);
            holder = new listUserHolder();

            holder.tv_list_name = convertView.findViewById(R.id.tv_list_name);
            holder.tv_list_description = convertView.findViewById(R.id.tv_list_description);
            convertView.setTag(holder);
        } else {
            holder = (listUserHolder) convertView.getTag();
        }
        MovieList modelMovie = mvList.get(position);
        holder.tv_list_name.setText(modelMovie.getList_name());
        holder.tv_list_description.setText(modelMovie.getDescription());
        return convertView;
    }
    static class listUserHolder {
        TextView tv_list_name;
        TextView tv_list_description;
    }
}
