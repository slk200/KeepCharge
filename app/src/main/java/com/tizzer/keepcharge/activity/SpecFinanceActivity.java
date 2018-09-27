package com.tizzer.keepcharge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.adapter.BillAdapter;
import com.tizzer.keepcharge.bean.BillBean;
import com.tizzer.keepcharge.bean.StoreBean;
import com.tizzer.keepcharge.callback.OnBillRecordListener;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.db.OrmLiteHelper;
import com.tizzer.keepcharge.fragment.RecordBillFragment;
import com.tizzer.keepcharge.util.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpecFinanceActivity extends AppCompatActivity implements BillAdapter.OnBillClickedListener, OnBillRecordListener {
    private static final int STEP = 50;
    private static final int REQUEST_CODE = 1;

    @BindView(R.id.ll_bottom)
    LinearLayout mLLBottom;
    @BindView(R.id.rv_bill_list)
    RecyclerView mBillList;

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
        storeBean = (StoreBean) getIntent().getSerializableExtra(ConstantsValue.STORE_BEAN_TAG);
        billBeans = OrmLiteHelper.getHelper(getApplicationContext()).getBills(storeBean.getId(), rangeStart, rangeEnd);
        if (billBeans.isEmpty()) {
            mLLBottom.setVisibility(View.VISIBLE);
            mBillList.setVisibility(View.GONE);
        } else {
            BillAdapter billAdapter = new BillAdapter(billBeans);
            billAdapter.setOnBillClickedListener(this);
            mBillList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            mBillList.setAdapter(billAdapter);
            mBillList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (canScroll && !mBillList.canScrollVertically(1)) {
                        rangeStart += STEP;
                        rangeEnd += STEP;
                        List<BillBean> bills = OrmLiteHelper.getHelper(getApplicationContext()).getBills(storeBean.getId(), rangeStart, rangeEnd);
                        if (bills != null && !bills.isEmpty()) {
                            billBeans.addAll(bills);
                            mBillList.getAdapter().notifyItemRangeInserted(rangeStart, rangeEnd);
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
    }

    @OnClick({R.id.iv_back, R.id.iv_search, R.id.fab_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_search:
                ToastUtil.simpleToast(getApplicationContext(), "search");
                break;
            case R.id.fab_record:
                RecordBillFragment.getInstance(storeBean.getId()).show(getSupportFragmentManager(), RecordBillFragment.class.getSimpleName());
                break;
        }
    }

    @Override
    public void onBillClicked(BillBean billBean, int index) {
        Intent intent = new Intent(getApplicationContext(), SpecBillActivity.class);
        intent.putExtra(ConstantsValue.BILL_BEAN_TAG, billBean);
        startActivityForResult(intent, REQUEST_CODE);
        selectedPosition = index;
    }

    @Override
    public void onBillRecord() {
        rangeStart += 1;
        rangeEnd += 1;
        canScroll = true;
        List<BillBean> bills = OrmLiteHelper.getHelper(getApplicationContext()).getBills(storeBean.getId(), 0, rangeEnd);
        if (bills != null && !bills.isEmpty()) {
            billBeans.clear();
            billBeans.addAll(bills);
            mBillList.getAdapter().notifyDataSetChanged();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                BillBean billBean = (BillBean) data.getSerializableExtra(ConstantsValue.BILL_BEAN_TAG);
                billBeans.set(selectedPosition, billBean);
                mBillList.getAdapter().notifyItemChanged(selectedPosition);
                if (data.getIntExtra(ConstantsValue.IS_YESTERDAY_TAG, 0) == ConstantsValue.RIGHT_CODE) {
                    data.putExtra(ConstantsValue.STORE_BEAN_TAG, storeBean);
                    setResult(RESULT_OK, data);
                } else {
                    setResult(RESULT_CANCELED);
                }

            }
        }
    }
}
