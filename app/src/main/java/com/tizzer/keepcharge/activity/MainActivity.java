package com.tizzer.keepcharge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.adapter.FragmentAdapter;
import com.tizzer.keepcharge.bean.StoreBean;
import com.tizzer.keepcharge.callback.OnStoreAddedListener;
import com.tizzer.keepcharge.callback.OnStoreDeleteListener;
import com.tizzer.keepcharge.fragment.StatisticsFragment;
import com.tizzer.keepcharge.fragment.StoreFragment;
import com.yinglan.alphatabs.AlphaTabsIndicator;

public class MainActivity extends AppCompatActivity
        implements OnStoreAddedListener, OnStoreDeleteListener {

    private StoreFragment mStoreFragment;
    private StatisticsFragment mStatisticsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        mStoreFragment = new StoreFragment();
        mStatisticsFragment = new StatisticsFragment();
        FragmentAdapter mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), new Fragment[]{mStoreFragment, mStatisticsFragment});

        ViewPager viewPager = findViewById(R.id.vp_container);
        viewPager.setAdapter(mFragmentAdapter);
        ((AlphaTabsIndicator) findViewById(R.id.bottom_bar)).setViewPager(viewPager);
    }

    /**
     * 活动返回数据处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mStoreFragment.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mStatisticsFragment.loadData();
        }
    }

    /**
     * 店铺添加事件
     *
     * @param storeBean
     */
    @Override
    public void onStoreAdded(StoreBean storeBean) {
        mStoreFragment.addStore(storeBean);
    }

    @Override
    public void onStoreDelete(StoreBean storeBean) {
        mStoreFragment.deleteStore(storeBean);
        mStatisticsFragment.loadData();
    }

}
