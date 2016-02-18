package com.baoshi.mua.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by ThinkPad on 2014/11/10.
 */
public abstract class BaseFragment<T extends ActionBarActivity> extends Fragment /*implements BaseActivity.HostCallback*/{
    private T activity;
    private ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (T) super.getActivity();
        initOptionMenu(activity);
    }

    private void initOptionMenu(ActionBarActivity activity ){
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        setHasOptionsMenu(true);
    }

    protected void hideIMM(View focusedView){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(focusedView != null){
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected ActionBar getActionBar(){
        return actionBar;
    }

    protected T getHostActivity(){
        return activity;
    }

    protected void removeFromParent(View view){

        if(view != null){
            ViewGroup vg = (ViewGroup) view.getParent();
            if(vg != null){
                vg.removeView(view);
            }
        }
    }

}
