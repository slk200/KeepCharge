package com.tizzer.keepcharge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.adapter.BillAdapter;
import com.tizzer.keepcharge.bean.BillBean;
import com.tizzer.keepcharge.bean.StoreBean;
import com.tizzer.keepcharge.callback.OnBillClickedListener;
import com.tizzer.keepcharge.callback.OnBillRecordListener;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.database.OrmLiteHelper;
import com.tizzer.keepcharge.fragment.RecordBillFragment;

import java.lang.ref.WeakReference;
import java.util.List;

public class FinanceActivity extends AppCompatActivity
        implements OnBillClickedListener, OnBillRecordListener, View.OnClickListener {

    public static final int LOAD = 0;
    //请求码
    private static final int REQUEST_CODE = 0;
    private static final int REQUEST_CODE2 = 1;

    private LinearLayout mDefaultView;
    private RecyclerView mBillListView;

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

    private BillAdapter mBillAdapter;
    private LoadHandler loadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);
        initView();
    }

    private void initView() {
        storeBean = (StoreBean) getIntent().getSerializableExtra(ConstantsValue.STORE_BEAN_TAG);
        loadHandler = new LoadHandler(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mDefaultView = findViewById(R.id.ll_bottom);
        billBeans = OrmLiteHelper.getHelper(this).getBills(storeBean.getId(), rangeStart, step);
        if (billBeans.isEmpty()) {
            mDefaultView.setVisibility(View.VISIBLE);
        }

        mBillAdapter = new BillAdapter(billBeans);
        mBillAdapter.setOnBillClickedListener(this);

        mBillListView = findViewById(R.id.rv_bill_list);
        mBillListView.setLayoutManager(new LinearLayoutManager(this));
        mBillListView.setAdapter(mBillAdapter);
        mBillListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (canScroll && !mBillListView.canScrollVertically(1)) {
                    loadHandler.sendEmptyMessage(LOAD);
                }
            }
        });

        findViewById(R.id.fab_record).setOnClickListener(this);
    }

    /**
     * 加载数据
     */
    public void loadData() {
        rangeStart += step;
        List<BillBean> bills = OrmLiteHelper.getHelper(this).getBills(storeBean.getId(), rangeStart, step);
        if (bills != null && !bills.isEmpty()) {
            billBeans.addAll(bills);
            if (bills.size() < step) {
                mBillAdapter.notifyItemRangeInserted(rangeStart, rangeStart + bills.size() - 1);
                canScroll = false;
            } else {
                mBillAdapter.notifyItemRangeInserted(rangeStart, step);
            }
        } else {
            canScroll = false;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_record:
                RecordBillFragment.getInstance(storeBean.getId()).show(getSupportFragmentManager(), RecordBillFragment.class.getSimpleName());
                break;
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
        Intent intent = new Intent(this, BillActivity.class);
        intent.putExtra(ConstantsValue.BILL_BEAN_TAG, billBean);
        intent.putExtra(ConstantsValue.STORE_BEAN_TAG, storeBean);
        startActivityForResult(intent, REQUEST_CODE);
        selectedPosition = index;
    }

    /**
     * 账单录入事件
     */
    @Override
    public void onBillRecord() {
        rangeStart += 1;
        step += 1;
        canScroll = true;
        List<BillBean> bills = OrmLiteHelper.getHelper(this).getBills(storeBean.getId(), 0, step);
        if (mDefaultView.isShown()) {
            mDefaultView.setVisibility(View.GONE);
        }
        if (bills != null && !bills.isEmpty()) {
            billBeans.clear();
            billBeans.addAll(bills);
            mBillAdapter.notifyDataSetChanged();
        }
        setResult(RESULT_OK);
    }

    /**
     * 菜单创建
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 菜单点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                Intent intent = new Intent(this, FilterActivity.class);
                intent.putExtra(ConstantsValue.STORE_BEAN_TAG, storeBean);
                startActivityForResult(intent, REQUEST_CODE2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 活动回传数据处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                onBillUpdateResult(resultCode, data);
                break;
            case REQUEST_CODE2:
                onBillFilterResult(resultCode);
                break;
        }
    }

    /**
     * 账单修改回传
     *
     * @param resultCode
     */
    private void onBillFilterResult(int resultCode) {
        if (resultCode == RESULT_OK) {
            List<BillBean> bills = OrmLiteHelper.getHelper(this).getBills(storeBean.getId(), 0, step);
            billBeans.clear();
            billBeans.addAll(bills);
            mBillAdapter.notifyDataSetChanged();
            setResult(RESULT_OK);
        }
    }

    /**
     * 账单筛选回传
     *
     * @param resultCode
     * @param data
     */
    private void onBillUpdateResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            BillBean billBean = (BillBean) data.getSerializableExtra(ConstantsValue.BILL_BEAN_TAG);
            billBeans.set(selectedPosition, billBean);
            mBillAdapter.notifyItemChanged(selectedPosition);
            data.putExtra(ConstantsValue.STORE_BEAN_TAG, storeBean);
            setResult(RESULT_OK);
        }
    }

    static class LoadHandler extends Handler {

        WeakReference<FinanceActivity> financeActivityWeakReference;

        LoadHandler(FinanceActivity financeActivity) {
            this.financeActivityWeakReference = new WeakReference<>(financeActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD:
                    financeActivityWeakReference.get().loadData();
            }
        }
    }
}
