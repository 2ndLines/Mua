package com.baoshi.mua.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoshi.mua.R;
import com.baoshi.mua.activity.BaseActivity;

/**
 * Created by ThinkPad on 2014/11/10.
 */
public class RefreshListFragment extends BaseFragment<BaseActivity> {
    protected SwipeRefreshLayout refreshLayout;
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(layoutManager == null){
            layoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(refreshLayout == null){
            refreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.layout_swiperefresh_recycler_list,container, false);
            recyclerView = (RecyclerView) refreshLayout.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(layoutManager);
        }

        removeFromParent(refreshLayout);
        return refreshLayout;
//        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void setLayoutManager(RecyclerView.LayoutManager lm){
        layoutManager = lm;
    }

    protected RecyclerView.LayoutManager getLayoutManager(){
        return layoutManager;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(refreshLayout != null){
            refreshLayout.setRefreshing(false);
        }
    }
}
