package com.tizzer.keepcharge.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.activity.SpecFinanceActivity;
import com.tizzer.keepcharge.adapter.StoreAdapter;
import com.tizzer.keepcharge.bean.BillBean;
import com.tizzer.keepcharge.bean.StoreBean;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.db.OrmLiteHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FinanceFragment extends Fragment implements StoreAdapter.OnCardClickedListener {
    private static final int REQUEST_CODE = 0;

    @BindView(R.id.rv_store)
    RecyclerView mRVStore;

    private List<StoreBean> mStoreBeans;
    private StoreAdapter mStoreAdapter;
    private Unbinder unbinder;
    private int selectedPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finance, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mStoreBeans = OrmLiteHelper.getHelper(getActivity()).getAllStoreBean();
        mStoreBeans.add(new StoreBean(0));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        mRVStore.setLayoutManager(linearLayoutManager);
        mStoreAdapter = new StoreAdapter(mStoreBeans);
        mStoreAdapter.setListener(this);
        mRVStore.setAdapter(mStoreAdapter);
    }

    @Override
    public void onStoreClick(StoreBean storeBean, int position) {
        Intent intent = new Intent(getActivity(), SpecFinanceActivity.class);
        intent.putExtra(ConstantsValue.STORE_BEAN_TAG, storeBean);
        getActivity().startActivityForResult(intent, REQUEST_CODE);
        selectedPosition = position;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                StoreBean storeBean = (StoreBean) data.getSerializableExtra(ConstantsValue.STORE_BEAN_TAG);
                BillBean billBean = (BillBean) data.getSerializableExtra(ConstantsValue.BILL_BEAN_TAG);
                if (data.getIntExtra(ConstantsValue.IS_CHANGE_TYPE_TAG, 0) == ConstantsValue.RIGHT_CODE) {
                    if (billBean.getType()) {
                        storeBean.setIncome(storeBean.getIncome() + billBean.getMoney());
                        storeBean.setPayment(storeBean.getPayment() - billBean.getMoney());
                    } else {
                        storeBean.setIncome(storeBean.getIncome() - billBean.getMoney());
                        storeBean.setPayment(storeBean.getPayment() + billBean.getMoney());
                    }
                } else {
                    double value = data.getDoubleExtra(ConstantsValue.D_VALUE_TAG, 0);
                    if (billBean.getType()) {
                        storeBean.setIncome(storeBean.getIncome() + value);
                    } else {
                        storeBean.setPayment(storeBean.getPayment() + value);
                    }
                }
                mStoreBeans.set(selectedPosition, storeBean);
                mRVStore.getAdapter().notifyItemChanged(selectedPosition);
            }
        }
    }

    @Override
    public void onAddClick() {
        InputStoreNameFragment.getInstance().show(getFragmentManager(), InputStoreNameFragment.class.getSimpleName());
    }

    public void addStore(StoreBean storeBean) {
        mStoreBeans.add(mStoreBeans.size() - 1, storeBean);
        mStoreAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
