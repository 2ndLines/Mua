package com.baoshi.mua.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import com.baoshi.mua.model.Creator;
import com.baoshi.mua.model.avos.User;
import com.baoshi.mua.utils.Lg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility basic class for register system.
 * Created by ThinkPad on 2014/12/1.
 */
public class Base2Activity extends ActionBarActivity {
    protected static final int PROGRESS_DIALOG_DISMISS_DELAY = 1000;
    private ProgressDialog progressDialog;
    public static final String USER_PROFILE = "user_info";
    public static final String KEY_CREATOR = "creator";

    protected Creator getCreator(){
        SharedPreferences sp = getSharedPreferences(USER_PROFILE, Context.MODE_PRIVATE);
        String creatorJson = sp.getString(KEY_CREATOR, null);
        if(creatorJson != null && !creatorJson.isEmpty()){
            return Creator.toJava(creatorJson);
        }else{
            return null;
        }
    }

    protected void cacheCurrentCreator(User user){
        Creator creator = new Creator(user);
        cacheCurrentCreator(creator);
    }

    protected void cacheCurrentCreator(Creator creator){
        SharedPreferences sp = getSharedPreferences(USER_PROFILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(KEY_CREATOR, creator.toString());
        editor.commit();
    }

    protected void gotoMainInterface(User user){
        cacheCurrentCreator(user);

        Intent intent = new Intent(getApplicationContext(),ActivityTabHost.class);
        startActivity(intent);

    }

    protected boolean isMobilePhoneNumber(String phoneNumber){
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(phoneNumber);

        return m.matches();
    }

    protected boolean isValidSmsCode(String smsCode){
        Pattern p = Pattern.compile("^\\d{6}$");
        Matcher m = p.matcher(smsCode);

        return m.matches();
    }

    protected boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }

        String reg = "^[0-9a-zA-Z]{6,16}$";

        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(password);

        return m.matches();
    }

    protected void showProgressDialog(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progressDialog == null){
                    progressDialog = new ProgressDialog(Base2Activity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                }

                progressDialog.setMessage(message);
                if(!progressDialog.isShowing()){
                    progressDialog.show();
                }
            }
        });
    }

    protected void dismissProgressDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });
    }

    protected void showMessage(int resId){
        showMessage(getString(resId));
    }

    protected void  showMessage(String message){
        Lg.toast(this,message);
    }
}
