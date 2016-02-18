package com.baoshi.mua.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.baoshi.mua.App;
import com.baoshi.mua.R;
import com.baoshi.mua.model.Creator;
import com.baoshi.mua.model.avos.User;
import com.baoshi.mua.view.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by ThinkPad on 2014/11/29.
 */
public class LoginActivity extends Base2Activity {
    private static final int REQUEST_CODE_RESET_PASSWORD = 10000;
    private EditText mPhoneView;
    private EditText mPasswordView;

    private ImageButton phoneClearer;
    private ImageButton passwordClearer;

    private CircleImageView userHead;
    private TextView mNickName;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_head_nophoto)
                .showImageOnFail(R.drawable.ic_head_nophoto)
                .imageScaleType(ImageScaleType.NONE)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        initViews();
        initLoginInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(RESULT_OK == resultCode){
            if(data != null){
                String phoneNumber = data.getStringExtra(ResetPasswordActivity.KEY_PHONE_NUMBER);
                mPhoneView.setText(phoneNumber);
                mPasswordView.requestFocus();
            }else{
                this.finish();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initViews(){

        userHead = (CircleImageView) findViewById(R.id.user_head);
        mNickName = (TextView)findViewById(R.id.nickname_text_view);

        phoneClearer = (ImageButton) findViewById(R.id.username_input_clearer);
        passwordClearer = (ImageButton)findViewById(R.id.password_input_clearer);

        mPhoneView = (EditText) findViewById(R.id.input_username);
        mPhoneView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        updateClearerButton(mPhoneView, phoneClearer);

        mPasswordView = (EditText)findViewById(R.id.input_password);
        updateClearerButton(mPasswordView,passwordClearer);
    }

    private void initLoginInfo(){
        Creator creator = getCreator();
        if(creator != null){
            ImageLoader.getInstance().displayImage(creator.getHeadUrl(),userHead,options);

            mNickName.setText(creator.getNickname());
            mPhoneView.setText(creator.getPhoneNumber());

            if(!mPhoneView.getText().toString().isEmpty()){
                mPasswordView.requestFocus();
            }
        }
    }

    private void updateClearerButton(final EditText edit, final View clearer){
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(!edit.getText().toString().isEmpty()){
                        clearer.setVisibility(View.VISIBLE);
                    }
                }else{
                    clearer.setVisibility(View.GONE);
                }
            }
        });

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearer.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void onViewClick(View v) {
        final int id = v.getId();

        switch (id){
            case R.id.container:
                hideSoftIMM();
                break;
            case R.id.button_login:
                onLoginButtonClick();
                break;
            case R.id.button_forget:
                onForgetButtonClick();
                break;
            case R.id.button_signip:
                Intent intent = new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(intent);
                break;
            case R.id.username_input_clearer:
                mPhoneView.setText(null);
                break;
            case R.id.password_input_clearer:
                mPasswordView.setText(null);
                break;
        }
    }

    private void hideSoftIMM() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    private void onForgetButtonClick(){
        new AlertDialog.Builder(this).setTitle(R.string.text_forget_password)
                .setItems(R.array.forget_action_list,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(which == 0){
                            gotoResetPassword();
                        }else if(which == 1){
                            gotoLoginBySmsCode();
                        }
                    }
                }).create().show();
    }

    private void gotoResetPassword(){
        Intent intent = new Intent(getApplicationContext(),ResetPasswordActivity.class);
        startActivityForResult(intent,REQUEST_CODE_RESET_PASSWORD);
    }

    private void gotoLoginBySmsCode(){
        Intent intent = new Intent(getApplicationContext(), LoginBySmsCodeActivity.class);
        startActivityForResult(intent, 1);
    }

    private void onLoginButtonClick(){
        String phoneNumber = mPhoneView.getText().toString();
        phoneNumber = phoneNumber.replaceAll(" ","");

        if(phoneNumber.isEmpty()){
            showMessage(getString(R.string.toast_typed_empty, getString(R.string.text_phone)));
            mPhoneView.requestFocus();
            return;
        }else if(!isMobilePhoneNumber(phoneNumber)){
            showMessage(getString(R.string.phone_number_text_error));
            mPhoneView.requestFocus();
            return;
        }

        if(mPasswordView.getText().toString().isEmpty()){
            showMessage(getString(R.string.toast_typed_empty,getString(R.string.text_password)));
            mPasswordView.requestFocus();
            return;
        }

        showProgressDialog(getString(R.string.user_loading));
        AVUser.loginByMobilePhoneNumberInBackground(phoneNumber, mPasswordView.getText().toString(),new LogInCallback<User>() {
            @Override
            public void done(User avUser, AVException e) {
                dismissProgressDialog();
                if(avUser != null){
                    //success to login
                    gotoMainInterface(avUser);
                    LoginActivity.this.finish();
                }else{
                    App.getInstance().handleLeanCloudException(e);
                }
            }
        }, User.class);

    }

}
