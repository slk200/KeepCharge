package com.tizzer.keepcharge.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.bean.BillBean;
import com.tizzer.keepcharge.callback.OnBillClickedListener;

import java.util.List;
import java.util.Locale;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private Context context;
    private List<BillBean> billBeans;
    private OnBillClickedListener listener;

    public BillAdapter(List<BillBean> billBeans) {
        this.billBeans = billBeans;
    }

    public void setOnBillClickedListener(OnBillClickedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        return new BillViewHolder(LayoutInflater.from(context).inflate(R.layout.bill_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder billViewHolder, int position) {
        final int index = position;
        final BillBean billBean = billBeans.get(position);
        if (billBean.getType()) {
            billViewHolder.logoView.setBackground(context.getResources().getDrawable(R.drawable.income_paint));
            billViewHolder.logoView.setText(context.getText(R.string.logo_in));
            billViewHolder.moneyView.setText(String.format(Locale.CHINA, "+%.2f", billBean.getMoney()));
            billViewHolder.moneyView.setTextColor(context.getResources().getColor(R.color.colorHoloRedLight));
        } else {
            billViewHolder.logoView.setBackground(context.getResources().getDrawable(R.drawable.payment_paint));
            billViewHolder.logoView.setText(context.getText(R.string.logo_out));
            billViewHolder.moneyView.setText(String.format(Locale.CHINA, "-%.2f", billBean.getMoney()));
            billViewHolder.moneyView.setTextColor(context.getResources().getColor(R.color.colorBlack));
        }
        billViewHolder.noteView.setText(billBean.getNote());
        billViewHolder.timeView.setText(billBean.getTime());
        billViewHolder.billView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBillClicked(billBean, index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return billBeans.size();
    }

    class BillViewHolder extends RecyclerView.ViewHolder {

        LinearLayout billView;
        TextView logoView;
        TextView moneyView;
        TextView timeView;
        TextView noteView;

        BillViewHolder(View itemView) {
            super(itemView);
            billView = itemView.findViewById(R.id.ll_bill);
            logoView = itemView.findViewById(R.id.tv_logo);
            moneyView = itemView.findViewById(R.id.tv_money);
            timeView = itemView.findViewById(R.id.tv_time);
            noteView = itemView.findViewById(R.id.tv_note);
        }
    }

}
