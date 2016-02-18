package com.baoshi.mua.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.baoshi.mua.App;
import com.baoshi.mua.R;
import com.baoshi.mua.model.avos.User;

/**
 * Created by ThinkPad on 2014/11/30.
 */
public class LoginBySmsCodeActivity extends Base2Activity implements View.OnClickListener {

    private ViewFlipper mFlipper;

    private EditText mPhoneView;
    private EditText mSmsCodeView;

    private Button mBtnNext, mBtnPrevious;
//    private EditText mPasswordView, mPasswordRepeatView;
//    private boolean isResetPasswordAction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_smscode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        mFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

        mPhoneView = (EditText) findViewById(R.id.input_phone);
        mSmsCodeView = (EditText) findViewById(R.id.sms_code);

        mBtnPrevious = (Button) findViewById(R.id.button_previous);
        mBtnNext = (Button) findViewById(R.id.button_next);

//        mBtnPrevious.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
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

    private void onNextClick() {
        if (mFlipper.getDisplayedChild() == 0) {
            if (mPhoneView.getText().toString().isEmpty()) {
                showMessage(getString(R.string.toast_typed_empty, getString(R.string.text_phone)));
            } else if (isMobilePhoneNumber(mPhoneView.getText().toString())) {
                requestLoginSmsCode(mPhoneView.getText().toString());
            }else{
                showMessage(getString(R.string.toast_typed_error, getString(R.string.text_phone)));
            }
        } else if (mFlipper.getDisplayedChild() == 1) {
            verifySmsCodeAndLogin(mSmsCodeView.getText().toString());

        }

    }

    private void updateButtonText(int pageIndex) {
        if(pageIndex == 1){
            mBtnNext.setText(R.string.text_login);
        }else{
            mBtnNext.setText(R.string.text_next);
        }
    }

    private void previousPage() {
        mFlipper.setInAnimation(this, R.anim.slide_in_from_left);
        mFlipper.setOutAnimation(this, R.anim.slide_out_to_right);
        mFlipper.showPrevious();
        updateButtonText(mFlipper.getDisplayedChild());
    }

    private void showNext() {
        mFlipper.setInAnimation(this, R.anim.slide_in_from_right);
        mFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
        mFlipper.showNext();
        updateButtonText(mFlipper.getDisplayedChild());
    }

    private void requestLoginSmsCode(String phoneNumber) {
        showProgressDialog(getString(R.string.smscode_obtaining));
        AVUser.requestLoginSmsCodeInBackground(phoneNumber, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    showProgressDialog(getString(R.string.notify_smscode_sended));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressDialog();
                            SystemClock.sleep(100);
                            showNext();
                        }
                    }, PROGRESS_DIALOG_DISMISS_DELAY);
                } else {
                    dismissProgressDialog();
                    App.getInstance().handleLeanCloudException(e);
                }
            }
        });
    }

    private void verifySmsCodeAndLogin(String smsCode){
        if (!isValidSmsCode(smsCode)) {
            showMessage(R.string.sms_code_text_error);
        } else {
            showProgressDialog(getString(R.string.user_loading));
            AVUser.loginBySMSCodeInBackground(mPhoneView.getText().toString(), smsCode, new LogInCallback<User>() {
                @Override
                public void done(User avUser, AVException e) {
                    dismissProgressDialog();
                    if (avUser != null) {
                        gotoMainInterface(avUser);
                        setResult(RESULT_OK);
                        LoginBySmsCodeActivity.this.finish();
                    } else {
                        App.getInstance().handleLeanCloudException(e);
                    }
                }
            },User.class);
        }
    }
}
