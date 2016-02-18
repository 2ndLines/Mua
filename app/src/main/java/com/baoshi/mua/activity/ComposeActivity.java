package com.baoshi.mua.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baoshi.mua.R;
import com.baoshi.mua.adapter.SixImageAdapter;
import com.baoshi.mua.helper.PubTypeHelper;
import com.baoshi.mua.listener.OnItemClickListener;
import com.baoshi.mua.model.Carrier;
import com.baoshi.mua.model.LocationInfo;
import com.baoshi.mua.model.PublicContract;
import com.baoshi.mua.model.orm.OrmPublicationContract;
import com.baoshi.mua.utils.Lg;
import com.baoshi.mua.view.PopupButtonList;
import com.baoshi.mua.view.SelfAdapteLayoutManager;
import com.baoshi.mua.view.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

//import android.widget.ArrayAdapter;

/**
 * Created by ThinkPad on 2014/11/13.
 */
public class ComposeActivity extends ActionBarActivity implements OnItemClickListener<String> {
    public static final String KEY_PUB_TYPE = "pub_type";
    public static final String KEY_PHOTOS = "photos";
    public static final String KEY_LOCATION_INFO = "location_info";
    public static final String KEY_CAPTION = "caption";

    private static final String DEFAULT_IMAGE_URI = "file:///storage/emulated/0/Mua/demo.jpg";
    public static final int RC_TAKE_PHOTO = 1000;
    public static final int RC_PICK_PHOTO = 1001;
    public static final int RC_CHOOSE_PLACE = 1002;
    @InjectView(R.id.staggered_grid)
    RecyclerView staggeredGrid;

    @InjectView(R.id.pub_place)
    TextView placeTV;
    @InjectView(R.id.caption_editText)
    EditText captionET;
    @InjectView(R.id.button_insert_photo)
    FloatingActionButton floatingButton;
    @InjectView(R.id.button_create_text)
    FloatingActionButton createCaptionButton;

    @InjectView(R.id.button_recorder)
    FloatingActionButton recorderButton;

    private List<String> selectedData = new ArrayList<String>();
    private TextView typeText;
    private PopupWindow typePopup;
    private Uri mOutputFileUri;
    private MediaScannerConnection mMediaScanner;
    private DisplayImageOptions options;
    private SixImageAdapter adapter;
    private LocationInfo locInfo;

    /**
     * @return return activity content view.
     */
    private View getContentView() {
        return ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            this.onBackPressed();
            return true;
        }else if(R.id.menu_deliver == item.getItemId()){
            onSendMenuItemClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mMediaScanner == null) {
            mMediaScanner = new MediaScannerConnection(this, null);
        }

        if (!mMediaScanner.isConnected()) {
            mMediaScanner.connect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaScanner != null) {
            mMediaScanner.disconnect();
        }
        if (adapter != null) {
            adapter.unregisterAdapterDataObserver();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        couldQuitConfirm();
    }


    private void addSelectedData(String uri) {
        selectedData.add(uri);
        if (adapter != null) {
//            adapter.notifyItemInserted(selectedData.size() - 1);
            adapter.notifyDataSetChanged();
        }
    }

    private void setSelectedData(List<String> data) {
        selectedData.clear();
        if (data != null && !data.isEmpty()) {
            selectedData.addAll(data);

        } else {
            //Add default image
            selectedData.add(DEFAULT_IMAGE_URI);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (RC_TAKE_PHOTO == requestCode) {
                if (mMediaScanner != null && mOutputFileUri != null) {
                    mMediaScanner.scanFile(this, new String[]{mOutputFileUri.getPath()}, new String[]{"image/*"},
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, final Uri uri) {
                                    Lg.d("__Media Scan complete !! uri = " + uri.toString());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            addSelectedData(uri.toString());
                                        }
                                    });
                                }
                            });
                }
            } else if ((PhotoViewer.REQUEST_CODE_PREVIEW_PHOTO == requestCode || RC_PICK_PHOTO == requestCode) &&
                    data != null) {
                setSelectedData(data.getStringArrayListExtra(PhotoChooser.SELECTED_KEY));

            } else if (RC_CHOOSE_PLACE == requestCode && data != null) {
                Lg.d("__choose place__");
                LocationInfo info = data.getParcelableExtra(LocationInfo.KEY);
                String loc = new StringBuilder().append(info.getProvince())
                        .append(info.getCity())
                        .append("#")
                        .append(info.getTitle())
                        .toString();
                placeTV.setText(loc);
                locInfo = info;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void initActionBar() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(createTypeView(),
                new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER));

    }

    private void init() {
        initActionBar();
        ButterKnife.inject(ComposeActivity.this);
        StaggeredGridLayoutManager layoutManager = new SelfAdapteLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        staggeredGrid.setLayoutManager(layoutManager);
        options = new DisplayImageOptions.Builder().build();
        selectedData.add(DEFAULT_IMAGE_URI);
        adapter = new SixImageAdapter(this, selectedData, R.layout.six_image_item);
        adapter.setOnItemClickListener(this);
        staggeredGrid.setAdapter(adapter);
        adapter.setLayoutManager(layoutManager);
        adapter.registerAdapterDataObserver();

    }

    private View createTypeView() {
        View view = getLayoutInflater().inflate(R.layout.actionbar_textview, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypePopup((TextView) v);
            }
        });
        typeText = (TextView) view;
        return view;
    }

    private void showTypePopup(final TextView anchor) {

        List<String> types = PubTypeHelper.getInstance().getAllTypes(getApplicationContext());
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types);
        ListView listView = new ListView(this);
        final PopupWindow popupWindow = new PopupWindow(listView, 260, ViewGroup.LayoutParams.WRAP_CONTENT);
        listView.setAdapter(adapter);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listView.setLayoutParams(lp);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == parent.getCount() - 1) {
                    showCustomTypeDialog();
                } else {
                    anchor.setText(parent.getAdapter().getItem(position).toString());
                }
                popupWindow.dismiss();
            }
        });

        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(anchor);

    }

    private void showCustomTypeDialog() {
        final EditText et = (EditText) getLayoutInflater().inflate(R.layout.layout_edit_dialog, null, false);
        ViewGroup.MarginLayoutParams mlp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mlp.setMargins(10,20,10,10);
        et.setLayoutParams(mlp);
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle(R.string.custom_type)
                .setView(et)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String type = et.getText().toString();
                        if (type.trim().length() > 2) {//2 chars at least.
                            typeText.setText(type);
                        }
                    }
                })
                .setNeutralButton(R.string.ok_and_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String type = et.getText().toString();
                        if (type.trim().length() > 2) {//2 chars at least.
                            typeText.setText(type);
                            PubTypeHelper.getInstance().addType(getApplicationContext(), type);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dlg.show();
    }

    private void couldQuitConfirm() {
        boolean couldQuit = true;

        String caption = captionET.getText().toString();

        if (!caption.trim().isEmpty()) {
            couldQuit = false;
        }

        if (!selectedData.contains(DEFAULT_IMAGE_URI)) {
            couldQuit = false;
        }

        if(!placeTV.getText().toString().isEmpty()){
            couldQuit = false;
        }

        if (!couldQuit) {
            new AlertDialog.Builder(this)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage(R.string.message_quit_comfirm)
                    .setPositiveButton(android.R.string.cancel, null)
                    .setCancelable(false)
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ComposeActivity.super.onBackPressed();
                        }
                    }).create().show();
        } else {
            super.onBackPressed();
        }
    }

    public void onCreateCaptionClick(){
        hideSoftIM(captionET);
    }

    public void onPlaceClick(View view) {
        Intent intent = new Intent(getApplicationContext(), PlaceActivity.class);
        startActivityForResult(intent, RC_CHOOSE_PLACE);
    }

    /**
     * Invoke this in layout xml.
     *
     * @param view
     */
    public void showInsertPhotoWindow(View view) {
        hideSoftIM(captionET);
        final PopupButtonList buttonList = new PopupButtonList.Builder(this)
                .addButton(R.string.pick_photo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickPhotos();
                    }
                }).addButton(R.string.take_photo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePhoto();
                    }
                }).build();
        buttonList.show(getWindow().getDecorView());
    }

    private void takePhoto() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String name = DateFormat.format("yymmddhhmmss", Calendar.getInstance(Locale.getDefault())).toString();
            name = new StringBuffer().append("mua_").append(name).append(".jpg").toString();

            mOutputFileUri = Uri.fromFile(new File(BaseActivity.getDataCacheDir(), name));
            Lg.d("___ cacheDir = " + BaseActivity.getDataCacheDir() + "___uri = " + mOutputFileUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
            startActivityForResult(intent, RC_TAKE_PHOTO);
        } else {
            Lg.toast(this, R.string.no_sdcard);
        }
    }

    private void pickPhotos() {
        Intent intent = new Intent(getApplicationContext(), PhotoChooser.class);

        selectedData.remove(DEFAULT_IMAGE_URI);
        intent.putStringArrayListExtra(PhotoChooser.SELECTED_KEY, new ArrayList<String>(selectedData));
        startActivityForResult(intent, RC_PICK_PHOTO);
    }

    @Override
    public void onItemClick(RecyclerView.Adapter holder, String data, int position) {
        Lg.d("____click position = " + position + ", uri = " + data);
        if (DEFAULT_IMAGE_URI.equals(data)) {
            showInsertPhotoWindow(null);
        } else {
            Intent intent = new Intent(getApplicationContext(), PhotoViewer.class);
            intent.putExtra(PhotoViewer.Constant.KEY_INIT_POSITION, position);
            intent.putExtra(PhotoViewer.Constant.KEY_URIS, new ArrayList<String>(selectedData));
            ComposeActivity.this.startActivityForResult(intent, PhotoViewer.REQUEST_CODE_PREVIEW_PHOTO);
        }

    }

    private void onSendMenuItemClick(){
        hideSoftIM(captionET);
        String caption = captionET.getText().toString().trim();
        if(caption.isEmpty()){
            Lg.toast(this,R.string.caption_empty);
            return ;
        }

        String type = typeText.getText().toString();
        if(type.equals(getString(R.string.hint_pub_type))){
            Lg.toast(this,R.string.pub_type_empty);
            return ;
        }

        String place = placeTV.getText().toString();
        if(place.isEmpty()){
            Lg.toast(this,R.string.place_info_empty);
            return;
        }

        Intent intent = new Intent();

        Carrier carrier = new Carrier();
        carrier.setType(type);

        intent.putExtra(OrmPublicationContract.CARRIER, carrier.toString());
        intent.putExtra(OrmPublicationContract.CAPTION,caption);
        selectedData.remove(DEFAULT_IMAGE_URI);
        if(selectedData.size() > 0){
            intent.putExtra(PublicContract.IMAGES,new ArrayList<String>(selectedData));
        }

        intent.putExtra(OrmPublicationContract.LOCATIONINFO,locInfo);

        setResult(Activity.RESULT_OK,intent);
        ComposeActivity.this.finish();
    }

    protected void hideSoftIM(View focusView){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(focusView != null){
            imm.hideSoftInputFromInputMethod(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
