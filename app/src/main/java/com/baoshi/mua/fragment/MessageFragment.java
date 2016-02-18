package com.baoshi.mua.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baoshi.mua.R;
import com.baoshi.mua.activity.ActivityTabHost;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by ThinkPad on 2014/11/9.
 */
public class MessageFragment extends BaseFragment<ActivityTabHost>
        implements PullToRefreshBase.OnRefreshListener2<ListView>, AdapterView.OnItemClickListener{

    PullToRefreshListView refreshWrapper;
    ListView messageListView;
    View searchBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(refreshWrapper == null) {
            refreshWrapper = (PullToRefreshListView) inflater.inflate(R.layout.fragment_message,container,false);
            searchBar = inflater.inflate(R.layout.search_bar,null, false);
            refreshWrapper.setOnRefreshListener(this);
            messageListView = refreshWrapper.getRefreshableView();
            initMessageListView();
        }
        removeFromParent(refreshWrapper);

        return refreshWrapper;
    }

    private void initMessageListView(){
        if(messageListView != null) {
            messageListView.addHeaderView(searchBar);
            messageListView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
