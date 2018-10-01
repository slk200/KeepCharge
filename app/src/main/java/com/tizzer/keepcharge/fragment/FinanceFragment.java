package com.tizzer.keepcharge.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.activity.SpecFinanceActivity;
import com.tizzer.keepcharge.adapter.StoreAdapter;
import com.tizzer.keepcharge.bean.StoreBean;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.db.OrmLiteHelper;
import com.tizzer.keepcharge.entity.Fact;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FinanceFragment extends Fragment implements StoreAdapter.OnCardClickedListener {
    private static final String TAG = "FinanceFragment";
    private static final int REQUEST_CODE = 0;

    @BindView(R.id.rv_store)
    RecyclerView mRVStore;

    private List<StoreBean> mStoreBeans;
    private StoreBean storeBean;
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
        mStoreBeans = OrmLiteHelper.getHelper(getActivity()).getSituation();
        mStoreBeans.add(new StoreBean(0));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        mRVStore.setLayoutManager(linearLayoutManager);
        mStoreAdapter = new StoreAdapter(mStoreBeans);
        mStoreAdapter.setListener(this);
        mRVStore.setAdapter(mStoreAdapter);
    }

    @Override
    public void onStoreClick(StoreBean storeBean, int position) {
        this.storeBean = storeBean;
        Intent intent = new Intent(getActivity(), SpecFinanceActivity.class);
        intent.putExtra(ConstantsValue.STORE_BEAN_TAG, storeBean);
        getActivity().startActivityForResult(intent, REQUEST_CODE);
        selectedPosition = position;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Fact fact = OrmLiteHelper.getHelper(getContext()).getSituation(storeBean.getId());
                Log.e(TAG, "onActivityResult: " + fact);
                storeBean.setRetain(fact.getIncome() - fact.getPayment());
                storeBean.setIncome(fact.getIncome());
                storeBean.setPayment(fact.getPayment());
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
