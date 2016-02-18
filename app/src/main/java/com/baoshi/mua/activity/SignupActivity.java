package com.baoshi.mua.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.baoshi.mua.App;
import com.baoshi.mua.R;
import com.baoshi.mua.model.avos.User;

public class SignupActivity extends Base2Activity implements View.OnClickListener {

    private static final String TAG = "SignupActivity";
    private static final String DATE_SEPERATOR = "-";
    protected static final int RC_SUCCESS_LOGIN = 1000;
    public static final String KEY_USERNAME = "username";
    private ViewFlipper mViewFlipper;
    private EditText mPasswordView, mPasswordRepeatView;
    private EditText mNickNameView, mPhoneView, mSmsCodeView;
    private Button mBtnPrivious, mBtnNext;
//    private ImageButton mContentClearer;
    private boolean isLastStep;
    private User mLocalUser;
    private boolean isVerified;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLocalUser = new User();
        setContentView(R.layout.activity_signup);
        setupViews();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViews() {
        mViewFlipper = (ViewFlipper) findViewById(R.id.signup_switcher);
        mNickNameView = (EditText) findViewById(R.id.setup_nickname);

        mPasswordView = (EditText) findViewById(R.id.setup_password);

        mPasswordRepeatView = (EditText) findViewById(R.id.setup_password_repeat);

        mPhoneView = (EditText) findViewById(R.id.setup_phone);

        mSmsCodeView = (EditText) findViewById(R.id.sms_code);

        mBtnPrivious = (Button) findViewById(R.id.btn_previous);
        mBtnPrivious.setOnClickListener(this);
        mBtnNext = (Button) findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(this);

    }

    private void showPrevious() {
        mViewFlipper.setInAnimation(SignupActivity.this, R.anim.slide_in_from_left);
        mViewFlipper.setOutAnimation(SignupActivity.this, R.anim.slide_out_to_right);
        mViewFlipper.showPrevious();
        updateButtonState();
    }

    private void showNext() {
        mViewFlipper.setInAnimation(SignupActivity.this, R.anim.slide_in_from_right);
        mViewFlipper.setOutAnimation(SignupActivity.this, R.anim.slide_out_to_left);
        mViewFlipper.showNext();
        updateButtonState();
    }

    private void updateButtonState() {
        final int index = mViewFlipper.getDisplayedChild();
        final int childCount = mViewFlipper.getChildCount();

        if (index == 0) {// first one
            mBtnPrivious.setVisibility(View.INVISIBLE);
        } else {
            mBtnPrivious.setVisibility(View.VISIBLE);
        }

        if (index == childCount - 1) { // last one
            mBtnNext.setText(R.string.finish_signup);
            mBtnPrivious.setVisibility(View.INVISIBLE);
            isLastStep = true;

        } else {
            mBtnNext.setText(R.string.text_next);
            isLastStep = false;
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.btn_previous) {
            showPrevious();
        } else if (v.getId() == R.id.btn_next) {
            if (isLastStep) {
                finishSignup(mSmsCodeView.getEditableText().toString());

            } else {
                if (getTypedInfoAndCheck(mViewFlipper.getDisplayedChild())) {
                    showNext();
                }
            }
        }
    }

    private void finishSignup(String smsCode) {
        showProgressDialog(getString(R.string.signuping));

        if (isValidSmsCode(smsCode)) {
            verifySmscode(smsCode);

        } else {
            dismissProgressDialog();
            showMessage(getString(R.string.toast_typed_error, getString(R.string.text_verifycode)));
            mSmsCodeView.setText(null);
            mSmsCodeView.requestFocus();
        }
    }

    private void verifySmscode(String smscode) {
        isVerified = false;
        AVUser.verifyMobilePhoneInBackground(smscode, new AVMobilePhoneVerifyCallback() {

            @Override
            public void done(AVException ex) {
                // TODO Auto-generated method stub
                if (ex == null) {
                    isVerified = true;
                    showProgressDialog(getString(R.string.signup_done));
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            dismissProgressDialog();

                            gotoMainInterface(mLocalUser);
                            setResult(Activity.RESULT_OK);
                            SignupActivity.this.finish();
                        }
                    }, PROGRESS_DIALOG_DISMISS_DELAY);
                } else {
                    isVerified = false;
                    dismissProgressDialog();
//                    showErrorMsg(ex.getMessage());
                    App.getInstance().handleLeanCloudException(ex);
                }
            }
        });
    }

    private boolean getTypedInfoAndCheck(int childIndex) {

        switch (childIndex) {
            case 0:
                if (mNickNameView.getText().toString().isEmpty()) {
                    mNickNameView.requestFocus();
                    showMessage(getString(R.string.toast_typed_empty, getString(R.string.text_nickname)));
                    return false;
                } else {
                    mLocalUser.setNickname(mNickNameView.getText().toString());
                    return true;
                }
            case 1:
                if (mPasswordView.getText().toString().isEmpty()) {
                    mPasswordView.requestFocus();
                    showMessage(getString(R.string.toast_typed_empty, getString(R.string.text_password)));
                    return false;
                }else if(isValidPassword(mPasswordView.getText().toString())){
                    if (mPasswordRepeatView.getText().toString().isEmpty()) {
                        mPasswordRepeatView.requestFocus();
                        showMessage(getString(R.string.toast_typed_empty, getString(R.string.setup_password_repeat)));
                        return false;
                    } else if (mPasswordView.getText().toString().equals(mPasswordRepeatView.getText().toString())) {
                        mLocalUser.setPassword(mPasswordView.getText().toString());
                        return true;
                    } else {
                        showMessage(getString(R.string.setup_password_dismatch));
                        return false;
                    }
                }else{
                    return false;
                }

            case 2:
                String phone = mPhoneView.getEditableText().toString();
                phone = phone.trim().replaceAll(" ", "");
                Log.d(TAG, ">>>>phone = " + phone);
                if (TextUtils.isEmpty(phone)) {
                    mPhoneView.requestFocus();
                    showMessage(getString(R.string.toast_typed_empty, getString(R.string.text_phone)));
                } else if (!isMobilePhoneNumber(phone)) {
                    mPhoneView.requestFocus();
                    showMessage(getString(R.string.toast_typed_error, getString(R.string.text_phone)));
                } else {
                    //check the phone number has been used or not.
                    //TODO
                    preSignup(phone);
                }

                break;
            default:
                break;
        }

        return false;
    }

    private void preSignup(final String phoneNumber) {
        showProgressDialog(getString(R.string.phone_verifing));
        mLocalUser.setUsername(phoneNumber);
        mLocalUser.setMobilePhoneNumber(phoneNumber);
        mLocalUser.signUpInBackground(new SignUpCallback() {

            @Override
            public void done(AVException ex) {
                // TODO Auto-generated method stub
                if (ex == null) {
                    //TODO  success to signup.
                    showProgressDialog(getString(R.string.phone_verify_ok));
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            dismissProgressDialog();
                            showNext();
                        }
                    }, PROGRESS_DIALOG_DISMISS_DELAY);

                } else {
                    dismissProgressDialog();
//                    showErrorMsg(ex.getMessage());
                    App.getInstance().handleLeanCloudException(ex);
                }
            }
        });
    }

    private void showQuitTips(){
        new AlertDialog.Builder(this).setMessage(R.string.confirm_quit_signup)
                .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SignupActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(android.R.string.cancel,null)
                .create().show();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        showQuitTips();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (mLocalUser != null && !isVerified) {
            isVerified = false;
            mLocalUser.deleteEventually();
            Log.d(TAG, "Delete user " + mLocalUser.getUsername());
        }
        super.onDestroy();
    }

}
