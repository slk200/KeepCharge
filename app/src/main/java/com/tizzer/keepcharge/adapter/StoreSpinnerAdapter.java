package com.tizzer.keepcharge.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tizzer.keepcharge.entity.Store;

import java.util.List;

public class StoreSpinnerAdapter extends ArrayAdapter<Store> {

    private Context context;
    private int resource;
    private List<Store> stores;

    public StoreSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Store> stores) {
        super(context, resource, stores);
        this.context = context;
        this.resource = resource;
        this.stores = stores;
    }

    @Override
    public int getCount() {
        return stores.size();
    }

    @Override
    public Store getItem(int position) {
        return stores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return stores.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Store store = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (store != null) {
            viewHolder.textView.setText(store.getName());
        }
        return convertView;
    }

    class ViewHolder {

        TextView textView;

        ViewHolder(View view) {
            textView = view.findViewById(android.R.id.text1);
        }
    }
}
