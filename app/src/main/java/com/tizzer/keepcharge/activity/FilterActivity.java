package com.tizzer.keepcharge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.adapter.BillAdapter;
import com.tizzer.keepcharge.bean.BillBean;
import com.tizzer.keepcharge.bean.StoreBean;
import com.tizzer.keepcharge.callback.OnBillClickedListener;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.database.OrmLiteHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity
        implements OnBillClickedListener, View.OnClickListener {
    //请求码
    private static final int REQUEST_CODE = 0;
    //what加载消息
    private static final int LOAD = 0;

    private EditText mKeyword;
    private RecyclerView mSearchedBillListView;

    //账单列表数据集
    private List<BillBean> billBeans;
    //是否还有可加载数据
    private boolean canScroll = true;
    //加载数据的初始索引
    private int rangeStart = 0;
    //账单加载跨度
    private int step = 30;
    //当前店铺的原始数据
    private StoreBean storeBean;
    //选中的账单在列表中的位置
    private int selectedPosition;
    //筛选关键字
    private String keyword;
    private BillAdapter mBillAdapter;
    private LoadHandler loadHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        storeBean = (StoreBean) getIntent().getSerializableExtra(ConstantsValue.STORE_BEAN_TAG);
        loadHandler = new LoadHandler(this);

        billBeans = new ArrayList<>();
        mBillAdapter = new BillAdapter(billBeans);
        mBillAdapter.setOnBillClickedListener(this);

        mKeyword = findViewById(R.id.et_keyword);

        mSearchedBillListView = findViewById(R.id.rv_searched_bills);
        mSearchedBillListView.setLayoutManager(new LinearLayoutManager(this));
        mSearchedBillListView.setAdapter(mBillAdapter);
        mSearchedBillListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (canScroll && !mSearchedBillListView.canScrollVertically(1)) {
                    loadHandler.sendEmptyMessage(LOAD);
                }

            }
        });

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.btn_search).setOnClickListener(this);
    }

    /**
     * 加载数据
     */
    public void loadData() {
        rangeStart += step;
        List<BillBean> bills = OrmLiteHelper.getHelper(getApplicationContext()).getMatchedBills(storeBean.getId(), keyword, rangeStart, step);
        if (bills != null && !bills.isEmpty()) {
            billBeans.addAll(bills);
            if (bills.size() < step) {
                mBillAdapter.notifyItemRangeInserted(rangeStart, rangeStart + bills.size());
                canScroll = false;
            } else {
                mBillAdapter.notifyItemRangeInserted(rangeStart, step);
            }
        } else {
            canScroll = false;
        }
    }

    /**
     * 账单点击事件
     *
     * @param billBean
     * @param index
     */
    @Override
    public void onBillClicked(BillBean billBean, int index) {
        Intent intent = new Intent(getApplicationContext(), BillActivity.class);
        intent.putExtra(ConstantsValue.BILL_BEAN_TAG, billBean);
        intent.putExtra(ConstantsValue.STORE_BEAN_TAG, storeBean);
        startActivityForResult(intent, REQUEST_CODE);
        selectedPosition = index;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_search:
                searchBills();
                break;
        }
    }

    /**
     * 查询账单
     */
    private void searchBills() {
        keyword = mKeyword.getText().toString().trim();
        List<BillBean> matchedBills = OrmLiteHelper.getHelper(this).getMatchedBills(storeBean.getId(), keyword, rangeStart, step);
        billBeans.clear();
        billBeans.addAll(matchedBills);
        mBillAdapter.notifyDataSetChanged();
    }

    /**
     * 活动返回数据处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    BillBean billBean = (BillBean) data.getSerializableExtra(ConstantsValue.BILL_BEAN_TAG);
                    billBeans.set(selectedPosition, billBean);
                    mBillAdapter.notifyItemChanged(selectedPosition);
                    data.putExtra(ConstantsValue.STORE_BEAN_TAG, storeBean);
                    setResult(RESULT_OK);
                }
            }
        }
    }

    private static class LoadHandler extends Handler {

        private WeakReference<FilterActivity> filterActivityWeakReference;

        private LoadHandler(FilterActivity filterActivity) {
            this.filterActivityWeakReference = new WeakReference<>(filterActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD:
                    filterActivityWeakReference.get().loadData();
            }
        }
    }
}
