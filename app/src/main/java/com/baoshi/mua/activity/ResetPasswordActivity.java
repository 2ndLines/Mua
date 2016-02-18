package com.baoshi.mua.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.baoshi.mua.App;
import com.baoshi.mua.R;

/**
 * Created by ThinkPad on 2014/11/30.
 */
public class ResetPasswordActivity extends Base2Activity implements View.OnClickListener {
    public static final String KEY_PHONE_NUMBER = "phone_number";

    private ViewFlipper mFlipper;
    private EditText mPhoneView;
    private EditText mPasswordView, mPasswordRepeatView;
    private EditText mSmsCode;
    private Button mBtnPrevious, mBtnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initViews();
    }

    private void initViews() {

        mFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

        mPhoneView = (EditText) findViewById(R.id.input_phone);

        mPasswordView = (EditText) findViewById(R.id.setup_password);
        mPasswordRepeatView = (EditText) findViewById(R.id.setup_password_repeat);
        mSmsCode = (EditText)findViewById(R.id.sms_code);

        mBtnPrevious = (Button)findViewById(R.id.button_previous);
        mBtnNext = (Button)findViewById(R.id.button_next);

        mBtnPrevious.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(android.R.id.home == item.getItemId()){
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id) {
            case R.id.button_previous:
                previousPage();
                break;
            case R.id.button_next:
                onNextClick();
                break;
            default:
                break;
        }
    }

    private void updateButtonText(int pageIndex) {

        if(pageIndex == 2){
            mBtnNext.setText(R.string.text_reset_password);
        }else{
            mBtnNext.setText(R.string.text_next);
        }

        if(pageIndex == 1){
            mBtnPrevious.setVisibility(View.VISIBLE);
        }else{
            mBtnPrevious.setVisibility(View.INVISIBLE);
        }

    }

    private void previousPage() {
        mFlipper.setInAnimation(this, R.anim.slide_in_from_left);
        mFlipper.setOutAnimation(this, R.anim.slide_out_to_right);
        mFlipper.showPrevious();
        updateButtonText(mFlipper.getDisplayedChild());
    }

    private void nextPage() {
        mFlipper.setInAnimation(this, R.anim.slide_in_from_right);
        mFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
        mFlipper.showNext();
        updateButtonText(mFlipper.getDisplayedChild());
    }

    private void onNextClick(){
        if(mFlipper.getDisplayedChild() == 0){
            if(!isMobilePhoneNumber(mPhoneView.getText().toString())){
                showMessage(R.string.phone_number_text_error);
            }else {
                nextPage();
            }
        }else if(mFlipper.getDisplayedChild() == 1){
            checkPassword(mPasswordView.getText().toString());
        }else {
            verifySmsCodeAndResetPassword(mSmsCode.getText().toString());
        }
    }

    private void requestPasswordResetSmsCode(String phoneNumber) {
        showProgressDialog(getString(R.string.smscode_obtaining));
        AVUser.requestPasswordResetBySmsCodeInBackground(phoneNumber, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {

                if (e == null) {
                    //Success to request sms code
                    showProgressDialog(getString(R.string.sms_code_sended));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressDialog();
                            nextPage();
                        }

                    }, PROGRESS_DIALOG_DISMISS_DELAY);
                } else {
                    dismissProgressDialog();
                    App.getInstance().handleLeanCloudException(e);
                }
            }
        });
    }

    private void checkPassword(String password) {
        if (!isValidPassword(password)) {
            showMessage(getString(R.string.toast_password_reg_error));
            mPasswordView.requestFocus();
        } else if (mPasswordRepeatView.getText().toString().isEmpty()) {
            showMessage(getString(R.string.setup_password_repeat));
        } else if (password.equals(mPasswordRepeatView.getText().toString())) {
            requestPasswordResetSmsCode(mPhoneView.getText().toString());
        } else {
            showMessage(R.string.setup_password_dismatch);
        }
    }

    private void verifySmsCodeAndResetPassword(String smsCode) {
        if (!isValidSmsCode(smsCode)) {
            showMessage(R.string.sms_code_text_error);
        } else {
            showProgressDialog(getString(R.string.password_reseting));
            AVUser.resetPasswordBySmsCodeInBackground(smsCode, mPasswordView.getText().toString(), new UpdatePasswordCallback() {

                @Override
                public void done(AVException e) {
                    dismissProgressDialog();
                    if (e == null) {
                        //Success to reset password
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSuccessDialog();
                            }
                        });
                    } else {
                        App.getInstance().handleLeanCloudException(e);
                    }
                }
            });
        }
    }

    private void showSuccessDialog(){
        new AlertDialog.Builder(ResetPasswordActivity.this).setMessage(R.string.success_to_reset_password)
                .setCancelable(false)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(KEY_PHONE_NUMBER, mPhoneView.getText().toString());
                        setResult(Activity.RESULT_OK, intent);
                        ResetPasswordActivity.this.finish();
                    }
                }).create().show();
    }
}
