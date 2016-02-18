package com.baoshi.mua.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabWidget;
import android.widget.TextView;

import com.baoshi.mua.App;
import com.baoshi.mua.R;
import com.baoshi.mua.adapter.PublicationViewHolder;
import com.baoshi.mua.fragment.CommentFragment;
import com.baoshi.mua.fragment.RelatedFragment;
import com.baoshi.mua.fragment.WhoseFollowFragment;
import com.baoshi.mua.model.orm.OrmPublication;

/**
 * Created by ThinkPad on 2014/12/11.
 */
public class PublicationDetail extends BaseActivity implements FragmentTabHost.OnTabChangeListener {
    private OrmPublication ormPublication;
    private FragmentTabHost tabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication_detail);
        initViews();
    }

    private void initViews(){
        FragmentTabHost tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this,getSupportFragmentManager(),R.id.realtabcontent);
        tabHost.setOnTabChangedListener(this);
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.tab_whose_follow)).setIndicator(getString(R.string.tab_whose_follow)),
                WhoseFollowFragment.class,null);
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.tab_related)).setIndicator(getString(R.string.tab_related)),
                RelatedFragment.class,null);
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.tab_comment)).setIndicator(getString(R.string.tab_comment)),
                CommentFragment.class,null);
        this.tabHost = tabHost;
        TabWidget widget = tabHost.getTabWidget();

       for(int i=0; i < widget.getChildCount(); i++){
           View view = tabHost.getTabWidget().getChildAt(i);
            view.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.detail_tab_height);
           TextView titleView = (TextView) view.findViewById(android.R.id.title);
           titleView.setTextAppearance(this,R.style.Base_TextAppearance_AppCompat_Caption);
       }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bindPublicationView(App.getInstance().getOrmPublication());
    }

    private void bindPublicationView(OrmPublication publication){
        if(publication != null) {
            View itemView = findViewById(R.id.pub_detail);
            PublicationViewHolder viewHolder = new PublicationViewHolder(itemView);
            viewHolder.bindView(publication);
            //Clear cached publication data.
            App.getInstance().setOrmPublication(null);
        }
        ormPublication = publication;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(android.R.id.home == item.getItemId()) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabChanged(String tabId) {

    }

    public void onBottomBarItemClick(View view){
        final int id = view.getId();
        switch (id) {
            case R.id.bottom_bar_thumb_up:
                break;
            case R.id.bottom_bar_follow:
                break;
            case R.id.bottom_bar_share:
                break;
            case R.id.bottom_bar_comment:
                break;
            case R.id.bottom_bar_more:
                break;
        }
    }
}
