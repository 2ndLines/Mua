package com.baoshi.mua.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.baoshi.mua.R;
import com.baoshi.mua.fragment.HomepageFragment;
import com.baoshi.mua.fragment.MeFragment;
import com.baoshi.mua.fragment.MessageFragment;
import com.baoshi.mua.model.Creator;
import com.baoshi.mua.model.avos.User;
import com.baoshi.mua.utils.Lg;

/**
 * Created by ThinkPad on 2014/11/9.
 */
public class ActivityTabHost extends BaseActivity implements TabHost.OnTabChangeListener {
    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tabhost);
        initTabHost();
        checkLoginState();
    }

    private void checkLoginState() {

        SharedPreferences sharedPreferences = getSharedPreferences(Base2Activity.USER_PROFILE, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(Base2Activity.KEY_CREATOR,null);
        if(json != null) {
            Creator creator = Creator.toJava(json);
            AVUser.loginByMobilePhoneNumberInBackground(creator.getPhoneNumber(),"123456",new LogInCallback<User>() {
                @Override
                public void done(User avUser, AVException e) {
                    if(avUser == null) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                }
            },User.class);
        }
    }

    private void initTabHost() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setOnTabChangedListener(this);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(newTabSpec(mTabHost, R.string.tab_homepage, 0), HomepageFragment.class, null);
        mTabHost.addTab(newTabSpec(mTabHost, R.string.tab_message, 0), MessageFragment.class, null);
        mTabHost.addTab(newTabSpec(mTabHost, R.string.tab_me, 0), MeFragment.class, null);

        TabWidget widget = mTabHost.getTabWidget();
        for(int i=0; i<widget.getChildCount(); i++){
            TextView title = (TextView) widget.getChildAt(i).findViewById(android.R.id.title);
            title.setTextAppearance(getApplicationContext(),R.style.TextAppearance_AppCompat_Title);
        }

    }

    private ObjectAnimator translate;

    public void moveTabWidget(long curPlayTime) {

        if (translate == null) {//create object animator
            int top = mTabHost.getTop();
            int bottom = mTabHost.getBottom();
            Lg.d(String.format("__tab top = %s, __tab bottom = %s", top, bottom));
            translate = ObjectAnimator.ofFloat(mTabHost, "y", top, bottom);
            translate.setDuration(1000);
        }
        translate.setCurrentPlayTime(curPlayTime);
    }

    private TabHost.TabSpec newTabSpec(TabHost tabHost, int tagId, int iconId) {
        String tag = tagId == 0 ? "" : getString(tagId);
        Drawable icon = iconId == 0 ? null : getResources().getDrawable(iconId);
        return setIndicator(tabHost.newTabSpec(tag), tag, icon);
    }

    private TabHost.TabSpec setIndicator(TabHost.TabSpec spec, String label, Drawable icon) {
        return spec.setIndicator(label, icon);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabChanged(String tabId) {
        getSupportActionBar().setTitle(tabId);
        Lg.d("___Host callback = " + callback);
        if (callback != null) {
            callback.callback();
        }
    }

}