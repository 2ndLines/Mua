package com.baoshi.mua.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.baoshi.mua.activity.BaseActivity;

import java.util.ArrayList;

/**
 * Created by ThinkPad on 2014/11/10.
 */
public class TabsPagerAdapter extends FragmentStatePagerAdapter implements  ViewPager.OnPageChangeListener {
    private final Context mContext;
    private final ViewPager mViewPager;
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    static final class TabInfo {
        private final Class<?> clss;
        private String title;
        private final Bundle args;

        TabInfo(Class<?> _class,String _title, Bundle _args) {
            clss = _class;
            title = _title;
            args = _args;
        }
    }

    public TabsPagerAdapter(BaseActivity activity, ViewPager pager) {
        super(activity.getSupportFragmentManager());
        mContext = activity;
        mViewPager = pager;
        mViewPager.setAdapter(this);
        mViewPager.setOnPageChangeListener(this);
    }

    public void addTab(Class<?> clss,String title, Bundle args) {
        TabInfo info = new TabInfo(clss,title, args);
        mTabs.add(info);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        return Fragment.instantiate(mContext, info.clss.getName(), info.args);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).title;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


}
