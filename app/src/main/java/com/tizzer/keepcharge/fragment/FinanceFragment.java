package com.tizzer.keepcharge.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.adapter.StoreAdapter;
import com.tizzer.keepcharge.bean.Store;
import com.tizzer.keepcharge.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinanceFragment extends Fragment implements StoreAdapter.OnCardClickedListener {
    private static final String TAG = "FinanceFragment";

    @BindView(R.id.rv_store)
    RecyclerView mRVStore;

    private List<Store> mStores;
    private StoreAdapter mStoreAdapter;
    private Unbinder unbinder;

    public FinanceFragment() {
        // Required empty public constructor
    }

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
        mStores = new ArrayList<>();
        mStores.add(new Store(1, "1号店", 29, 17, 1));
        mStores.add(new Store(2, "2号店", 88, 25, 1));
        mStores.add(new Store(3, "3号店", 37, 59, 1));
        mStores.add(new Store(4, "4号店", 29, 17, 1));
        mStores.add(new Store(5, "5号店", 88, 25, 1));
        mStores.add(new Store(6, "6号店", 37, 59, 1));
        mStores.add(new Store(0));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        mRVStore.setLayoutManager(linearLayoutManager);
        mStoreAdapter = new StoreAdapter(mStores);
        mStoreAdapter.setOnCardClickedListener(this);
        mRVStore.setAdapter(mStoreAdapter);
    }

    @Override
    public void onStoreClick(int id) {
        ToastUtil.simpleToast(this.getActivity().getApplicationContext(), String.valueOf(id));
    }

    @Override
    public void onAddClick() {
        InputStoreNameFragment.newInstance().show(getFragmentManager(), TAG);
//        ToastUtil.simpleToast(this.getActivity().getApplicationContext(), "add");
    }

    public void addStore(Store store) {
        Log.e(TAG, "addStore: " + (mStores.size() - 1) + store);
        mStores.add(mStores.size() - 1, store);
        mStoreAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
