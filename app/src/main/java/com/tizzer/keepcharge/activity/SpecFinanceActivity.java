package com.tizzer.keepcharge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpecFinanceActivity extends AppCompatActivity implements OnBillClickedListener, OnBillRecordListener {
    private static final String TAG = "SpecFinanceActivity";
    private static final int STEP = 50;
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE2 = 2;

    @BindView(R.id.ll_bottom)
    LinearLayout mLLBottom;
    @BindView(R.id.rv_bill_list)
    RecyclerView mBillList;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private int rangeStart = 0;
    private int rangeEnd = STEP;
    private boolean canScroll = true;
    private List<BillBean> billBeans;
    private int selectedPosition;
    private StoreBean storeBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spec_finance);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        storeBean = (StoreBean) getIntent().getSerializableExtra(ConstantsValue.STORE_BEAN_TAG);
        billBeans = OrmLiteHelper.getHelper(getApplicationContext()).getBills(storeBean.getId(), rangeStart, rangeEnd);
        if (billBeans.isEmpty()) {
            mLLBottom.setVisibility(View.VISIBLE);
        }
        BillAdapter billAdapter = new BillAdapter(billBeans);
        billAdapter.setOnBillClickedListener(this);
        mBillList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mBillList.setAdapter(billAdapter);
        mBillList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (canScroll && !mBillList.canScrollVertically(1)) {
                    rangeStart += STEP;
                    rangeEnd += STEP;
                    List<BillBean> bills = OrmLiteHelper.getHelper(getApplicationContext()).getBills(storeBean.getId(), rangeStart, rangeEnd);
                    if (bills != null && !bills.isEmpty()) {
                        billBeans.addAll(bills);
                        Objects.requireNonNull(mBillList.getAdapter()).notifyItemRangeInserted(rangeStart, rangeEnd);
                        if (bills.size() < STEP) {
                            canScroll = false;
                        }
                    } else {
                        canScroll = false;
                    }
                }
            }
        });
    }

    @OnClick(R.id.fab_record)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab_record:
                RecordBillFragment.getInstance(storeBean.getId()).show(getSupportFragmentManager(), RecordBillFragment.class.getSimpleName());
                break;
        }
    }

    @Override
    public void onBillClicked(BillBean billBean, int index) {
        Intent intent = new Intent(getApplicationContext(), SpecBillActivity.class);
        intent.putExtra(ConstantsValue.BILL_BEAN_TAG, billBean);
        intent.putExtra(ConstantsValue.STORE_BEAN_TAG, storeBean);
        startActivityForResult(intent, REQUEST_CODE);
        selectedPosition = index;
    }

    @Override
    public void onBillRecord() {
        rangeStart += 1;
        rangeEnd += 1;
        canScroll = true;
        List<BillBean> bills = OrmLiteHelper.getHelper(getApplicationContext()).getBills(storeBean.getId(), 0, rangeEnd);
        Log.e(TAG, "onBillRecord: " + bills);
        if (mLLBottom.isShown()) {
            mLLBottom.setVisibility(View.GONE);
        }
        if (bills != null && !bills.isEmpty()) {
            Log.e(TAG, "onBillRecord: do it");
            billBeans.clear();
            billBeans.addAll(bills);
            Objects.requireNonNull(mBillList.getAdapter()).notifyDataSetChanged();
        }
        setResult(RESULT_OK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    BillBean billBean = (BillBean) data.getSerializableExtra(ConstantsValue.BILL_BEAN_TAG);
                    billBeans.set(selectedPosition, billBean);
                    Objects.requireNonNull(mBillList.getAdapter()).notifyItemChanged(selectedPosition);
                    data.putExtra(ConstantsValue.STORE_BEAN_TAG, storeBean);
                    setResult(RESULT_OK);
                }
                break;
            case REQUEST_CODE2:
                if (resultCode == RESULT_OK) {
                    List<BillBean> bills = OrmLiteHelper.getHelper(getApplicationContext()).getBills(storeBean.getId(), 0, rangeEnd);
                    billBeans.clear();
                    billBeans.addAll(bills);
                    Objects.requireNonNull(mBillList.getAdapter()).notifyDataSetChanged();
                    setResult(RESULT_OK);
                }
                break;
        }
    }
}
