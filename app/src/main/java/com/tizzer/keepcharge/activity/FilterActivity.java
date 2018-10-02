package com.tizzer.keepcharge.activity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterActivity extends AppCompatActivity implements OnBillClickedListener {
    private static final String TAG = "FilterActivity";
    private static final int STEP = 50;
    private static final int REQUEST_CODE = 1;

    @BindView(R.id.et_keyword)
    EditText mKeyword;
    @BindView(R.id.rv_searched_bills)
    RecyclerView mSearchedBills;

    private List<BillBean> billBeans;
    private boolean canScroll = false;
    private int rangeStart = 0;
    private int rangeEnd = STEP;
    private StoreBean storeBean;
    private int selectedPosition;
    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        storeBean = (StoreBean) getIntent().getSerializableExtra(ConstantsValue.STORE_BEAN_TAG);

        billBeans = new ArrayList<>();
        BillAdapter billAdapter = new BillAdapter(billBeans);
        billAdapter.setOnBillClickedListener(this);
        mSearchedBills.setLayoutManager(new LinearLayoutManager(this));
        mSearchedBills.setAdapter(billAdapter);
        mSearchedBills.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (canScroll && !mSearchedBills.canScrollVertically(1)) {
                    rangeStart += STEP;
                    rangeEnd += STEP;
                    List<BillBean> bills = OrmLiteHelper.getHelper(getApplicationContext()).getMatchedBills(storeBean.getId(), keyword, rangeStart, rangeEnd);
                    if (bills != null && !bills.isEmpty()) {
                        billBeans.addAll(bills);
                        Objects.requireNonNull(mSearchedBills.getAdapter()).notifyItemRangeInserted(rangeStart, rangeEnd);
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

    @Override
    public void onBillClicked(BillBean billBean, int index) {
        Intent intent = new Intent(getApplicationContext(), SpecBillActivity.class);
        intent.putExtra(ConstantsValue.BILL_BEAN_TAG, billBean);
        intent.putExtra(ConstantsValue.STORE_BEAN_TAG, storeBean);
        startActivityForResult(intent, REQUEST_CODE);
        selectedPosition = index;
    }

    @OnClick({R.id.iv_back, R.id.btn_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_search:
                searchBills();
                break;
        }
    }

    private void searchBills() {
        keyword = mKeyword.getText().toString().trim();
        List<BillBean> matchedBills = OrmLiteHelper.getHelper(this).getMatchedBills(storeBean.getId(), keyword, rangeStart, rangeEnd);
        billBeans.clear();
        billBeans.addAll(matchedBills);
        Objects.requireNonNull(mSearchedBills.getAdapter()).notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                BillBean billBean = (BillBean) Objects.requireNonNull(data).getSerializableExtra(ConstantsValue.BILL_BEAN_TAG);
                billBeans.set(selectedPosition, billBean);
                Objects.requireNonNull(mSearchedBills.getAdapter()).notifyItemChanged(selectedPosition);
                data.putExtra(ConstantsValue.STORE_BEAN_TAG, storeBean);
                setResult(RESULT_OK);
            }
        }
    }
}
