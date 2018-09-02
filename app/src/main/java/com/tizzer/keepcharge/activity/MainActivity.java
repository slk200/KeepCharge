package com.tizzer.keepcharge.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.adapter.FragmentAdapter;
import com.tizzer.keepcharge.fragment.FinanceFragment;
import com.tizzer.keepcharge.fragment.PersonalFragment;
import com.yinglan.alphatabs.AlphaTabsIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.vp_container)
    ViewPager mVPContainer;
    @BindView(R.id.bottom_bar)
    AlphaTabsIndicator mBottomBar;

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
        mFinanceFragment = new FinanceFragment();
        mPersonalFragment = new PersonalFragment();
        mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), new Fragment[]{mFinanceFragment, mPersonalFragment});
        mVPContainer.setAdapter(mFragmentAdapter);
        mBottomBar.setViewPager(mVPContainer);
    }
}
