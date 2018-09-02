package com.tizzer.keepcharge.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.bean.Store;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter {
    private static final String TAG = "StoreAdapter";
    private List<Store> stores;
    private Context context;
    private OnCardClickedListener onCardClickedListener;

    public StoreAdapter(List<Store> stores) {
        this.stores = stores;
    }

    public void setOnCardClickedListener(OnCardClickedListener onCardClickedListener) {
        this.onCardClickedListener = onCardClickedListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view;
        switch (viewType) {
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.store_item, parent, false);
                return new StoreViewHolder(view);
            case 0:
                view = LayoutInflater.from(context).inflate(R.layout.store_add, parent, false);
                return new AddViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 1) {
            final Store store = stores.get(position);
            StoreViewHolder storeViewHolder = (StoreViewHolder) holder;
            storeViewHolder.mStoreName.setText(store.getName());
            storeViewHolder.mIncome.setText(String.valueOf(store.getIncome()));
            storeViewHolder.mPayment.setText(String.valueOf(store.getPayment()));
            storeViewHolder.mStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "onStoreClick: " + store);
                    onCardClickedListener.onStoreClick(store.getId());
                }
            });
        } else {
            AddViewHolder addViewHolder = (AddViewHolder) holder;
            addViewHolder.mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCardClickedListener.onAddClick();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    @Override
    public int getItemViewType(int position) {
        return stores.get(position).getType();
    }

    public interface OnCardClickedListener {
        void onStoreClick(int id);

        void onAddClick();
    }

    class StoreViewHolder extends RecyclerView.ViewHolder {

        CardView mStore;
        TextView mStoreName;
        TextView mIncome;
        TextView mPayment;

        StoreViewHolder(View itemView) {
            super(itemView);
            mStore = itemView.findViewById(R.id.card_store);
            mStoreName = itemView.findViewById(R.id.tv_name);
            mIncome = itemView.findViewById(R.id.tv_income);
            mPayment = itemView.findViewById(R.id.tv_payment);
        }
    }

    class AddViewHolder extends RecyclerView.ViewHolder {

        CardView mAdd;

        AddViewHolder(View itemView) {
            super(itemView);
            mAdd = itemView.findViewById(R.id.card_add);
        }
    }
}
