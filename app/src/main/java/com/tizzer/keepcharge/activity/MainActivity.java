package com.tizzer.keepcharge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.adapter.FragmentAdapter;
import com.tizzer.keepcharge.bean.StoreBean;
import com.tizzer.keepcharge.callback.OnStoreAddedListener;
import com.tizzer.keepcharge.fragment.FinanceFragment;
import com.tizzer.keepcharge.fragment.PersonalFragment;
import com.yinglan.alphatabs.AlphaTabsIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnStoreAddedListener {

    @BindView(R.id.vp_container)
    ViewPager mVPContainer;
    @BindView(R.id.bottom_bar)
    AlphaTabsIndicator mBottomBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    FinanceFragment mFinanceFragment;
    PersonalFragment mPersonalFragment;
    FragmentAdapter mFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        mFinanceFragment = new FinanceFragment();
        mPersonalFragment = new PersonalFragment();
        mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), new Fragment[]{mFinanceFragment, mPersonalFragment});
        mVPContainer.setAdapter(mFragmentAdapter);
        mBottomBar.setViewPager(mVPContainer);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFinanceFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStoreAdded(StoreBean storeBean) {
        mFinanceFragment.addStore(storeBean);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.output_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_output:
                startActivity(new Intent(getApplicationContext(), OutputActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
