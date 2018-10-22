package com.tizzer.keepcharge.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.activity.FinanceActivity;
import com.tizzer.keepcharge.adapter.StoreAdapter;
import com.tizzer.keepcharge.bean.StoreBean;
import com.tizzer.keepcharge.callback.OnCardClickedListener;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.database.OrmLiteHelper;
import com.tizzer.keepcharge.entity.Fact;

import java.util.List;

public class StoreFragment extends Fragment implements OnCardClickedListener {
    private static final int REQUEST_CODE = 0;

    private List<StoreBean> mStoreBeans;
    private StoreBean storeBean;
    private StoreAdapter mStoreAdapter;
    private int selectedPosition;
    private FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getFragmentManager();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mStoreBeans = OrmLiteHelper.getHelper(getActivity()).getSituation();
        mStoreBeans.add(new StoreBean(0));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mStoreAdapter = new StoreAdapter(mStoreBeans);
        mStoreAdapter.setListener(this);

        RecyclerView mStoreListView = view.findViewById(R.id.rv_store);
        mStoreListView.setLayoutManager(linearLayoutManager);
        mStoreListView.setAdapter(mStoreAdapter);
    }

    @Override
    public void onStoreClick(StoreBean storeBean, int position) {
        this.storeBean = storeBean;
        Intent intent = new Intent(getActivity(), FinanceActivity.class);
        intent.putExtra(ConstantsValue.STORE_BEAN_TAG, storeBean);
        startActivityForResult(intent, REQUEST_CODE);
        selectedPosition = position;
    }

    @Override
    public void onStoreLongClick(StoreBean storeBean, int position) {
        selectedPosition = position;
        DeleteStoreFragment.getInstance(storeBean).show(fragmentManager, DeleteStoreFragment.class.getSimpleName());
    }

    @Override
    public void onAddClick() {
        AddStoreFragment.getInstance().show(fragmentManager, AddStoreFragment.class.getSimpleName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Fact fact = OrmLiteHelper.getHelper(getContext()).getSituation(storeBean.getId());
                storeBean.setRetain(fact.getIncome() - fact.getPayment());
                storeBean.setIncome(fact.getIncome());
                storeBean.setPayment(fact.getPayment());
                mStoreBeans.set(selectedPosition, storeBean);
                mStoreAdapter.notifyItemChanged(selectedPosition);
            }
        }
    }

    /**
     * 增加店铺
     *
     * @param storeBean
     */
    public void addStore(StoreBean storeBean) {
        mStoreBeans.add(mStoreBeans.size() - 1, storeBean);
        mStoreAdapter.notifyDataSetChanged();
    }

    /**
     * 删除店铺
     *
     * @param storeBean
     */
    public void deleteStore(StoreBean storeBean) {
        mStoreBeans.remove(selectedPosition);
        mStoreAdapter.notifyItemRemoved(selectedPosition);
        OrmLiteHelper.getHelper(this.getContext()).destroyStore(storeBean.getId());
    }

}
