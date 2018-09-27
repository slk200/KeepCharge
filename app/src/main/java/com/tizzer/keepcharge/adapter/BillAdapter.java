package com.tizzer.keepcharge.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.bean.BillBean;

import java.util.List;

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

    @Override
    public BillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        return new BillViewHolder(LayoutInflater.from(context).inflate(R.layout.bill_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BillViewHolder holder, int position) {
        final BillBean billBean = billBeans.get(position);
        if (billBean.getType()) {
            holder.billIconView.setImageResource(R.drawable.ic_bill_in);
            holder.moneyView.setText("+" + billBean.getMoney());
            holder.moneyView.setTextColor(context.getResources().getColor(R.color.colorHoloRed));
        } else {
            holder.billIconView.setImageResource(R.drawable.ic_bill_out);
            holder.moneyView.setText("-" + billBean.getMoney());
            holder.moneyView.setTextColor(context.getResources().getColor(R.color.colorBlack));
        }
        holder.noteView.setText(billBean.getNote());
        holder.timeView.setText(billBean.getTime());
        final int index = position;
        holder.billView.setOnClickListener(new View.OnClickListener() {
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

    public interface OnBillClickedListener {
        void onBillClicked(BillBean billBean, int index);
    }

    class BillViewHolder extends RecyclerView.ViewHolder {

        LinearLayout billView;
        ImageView billIconView;
        TextView moneyView;
        TextView timeView;
        TextView noteView;

        BillViewHolder(View itemView) {
            super(itemView);
            billView = itemView.findViewById(R.id.rl_bill);
            billIconView = itemView.findViewById(R.id.iv_bill);
            moneyView = itemView.findViewById(R.id.tv_money);
            timeView = itemView.findViewById(R.id.tv_time);
            noteView = itemView.findViewById(R.id.tv_note);
        }
    }
}
