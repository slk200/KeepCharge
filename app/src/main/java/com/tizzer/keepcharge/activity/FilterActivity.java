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
    //加载数据的跨度
    private static final int STEP = 50;
    //活动请求码
    private static final int REQUEST_CODE = 1;

    @BindView(R.id.et_keyword)
    EditText mKeyword;
    @BindView(R.id.rv_searched_bills)
    RecyclerView mSearchedBills;

    //账单列表数据集
    private List<BillBean> billBeans;
    //是否还有可加载数据
    private boolean canScroll = true;
    //加载数据的初始索引
    private int rangeStart = 0;
    //加载数据的结束索引
    private int rangeEnd = STEP;
    //当前店铺的原始数据
    private StoreBean storeBean;
    //选中的账单在列表中的位置
    private int selectedPosition;
    //筛选关键字
    private String keyword;
    private BillAdapter mBillAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        storeBean = (StoreBean) getIntent().getSerializableExtra(ConstantsValue.STORE_BEAN_TAG);

        billBeans = new ArrayList<>();
        mBillAdapter = new BillAdapter(billBeans);
        mBillAdapter.setOnBillClickedListener(this);
        mSearchedBills.setLayoutManager(new LinearLayoutManager(this));
        mSearchedBills.setAdapter(mBillAdapter);
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
                        mBillAdapter.notifyItemRangeInserted(rangeStart, rangeEnd);
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
        mBillAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                BillBean billBean = (BillBean) data.getSerializableExtra(ConstantsValue.BILL_BEAN_TAG);
                billBeans.set(selectedPosition, billBean);
                mBillAdapter.notifyItemChanged(selectedPosition);
                data.putExtra(ConstantsValue.STORE_BEAN_TAG, storeBean);
                setResult(RESULT_OK);
            }
        }
    }
}
