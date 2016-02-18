package com.baoshi.mua.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.avos.avoscloud.AVUser;
import com.baoshi.mua.R;

/**
 * Created by ThinkPad on 2014/12/2.
 */
public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        overridePendingTransition(0, R.anim.abc_fade_out);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(AVUser.getCurrentUser() != null){
                    gotoMainActivity();
                }else{
                    gotoLogin();
                }
            }
        }, 10000);
    }

    private void gotoMainActivity(){
        Intent intent = new Intent(getApplicationContext(), ActivityTabHost.class);
        startActivity(intent);
    }

    private void gotoLogin(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}
