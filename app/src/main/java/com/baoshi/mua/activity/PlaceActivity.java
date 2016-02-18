package com.baoshi.mua.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.baoshi.mua.fragment.PlaceChooseFragment;
import com.baoshi.mua.utils.Lg;

/**
 * Created by ThinkPad on 2014/11/21.
 */
public class PlaceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new PlaceChooseFragment()).commit();
        Lg.d("____saveInstanceState is null ? " + (savedInstanceState == null));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Lg.d("____save instance state__");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
