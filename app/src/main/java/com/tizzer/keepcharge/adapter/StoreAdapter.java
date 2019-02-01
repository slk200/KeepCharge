package com.tizzer.keepcharge.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.bean.StoreBean;
import com.tizzer.keepcharge.callback.OnCardClickedListener;

import java.util.List;
import java.util.Locale;

public class StoreAdapter extends RecyclerView.Adapter {
    private List<StoreBean> storeBeans;
    private OnCardClickedListener listener;

    public StoreAdapter(List<StoreBean> storeBeans) {
        this.storeBeans = storeBeans;
    }

    public void setListener(OnCardClickedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_add, parent, false);
                return new AddViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item, parent, false);
                return new StoreViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 1) {
            final int index = position;
            final StoreBean storeBean = storeBeans.get(position);
            StoreViewHolder storeViewHolder = (StoreViewHolder) holder;
            storeViewHolder.mStoreName.setText(storeBean.getName());
            storeViewHolder.mRetain.setText(String.format(Locale.CHINA, "%.2f", storeBean.getRetain()));
            storeViewHolder.mIncome.setText(String.format(Locale.CHINA, "%.2f", storeBean.getIncome()));
            storeViewHolder.mPayment.setText(String.format(Locale.CHINA, "%.2f", storeBean.getPayment()));
            storeViewHolder.mStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onStoreClick(storeBean, index);
                }
            });
            storeViewHolder.mStore.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onStoreLongClick(storeBean, index);
                    return true;
                }
            });
        } else {
            AddViewHolder addViewHolder = (AddViewHolder) holder;
            addViewHolder.mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAddClick();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return storeBeans.size();
    }

    @Override
    public int getItemViewType(int position) {
        return storeBeans.get(position).getType();
    }

    class StoreViewHolder extends RecyclerView.ViewHolder {

        CardView mStore;
        TextView mStoreName;
        TextView mRetain;
        TextView mIncome;
        TextView mPayment;

        StoreViewHolder(View itemView) {
            super(itemView);
            mStore = itemView.findViewById(R.id.card_store);
            mStoreName = itemView.findViewById(R.id.tv_name);
            mRetain = itemView.findViewById(R.id.tv_retain);
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
