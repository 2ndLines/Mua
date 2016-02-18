package com.baoshi.mua.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.TextView;

import com.baoshi.mua.R;
import com.baoshi.mua.adapter.MultiChooserAdapter;
import com.baoshi.mua.listener.OnItemClickListener;
import com.baoshi.mua.utils.Lg;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThinkPad on 2014/11/17.
 */
public class PhotoChooser extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener<String> {

    public static final String SELECTED_KEY = "selected_photos";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DisplayImageOptions options;
    private TextView emptyTextView;
    private Button preview, confirm;

    private MultiChooserAdapter multiChooserAdapter;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            setSelectedData(intent.getStringArrayListExtra(SELECTED_KEY));
        }
    }

    private void setSelectedData(List<String> data) {
        multiChooserAdapter.setSelected(data);
        updateButton(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_chooser);
        init();
        handleIntent(getIntent());
        getSupportLoaderManager().initLoader(0, null, this);

    }

    private void init() {
        forceShowOverflow();

        layoutManager = new GridLayoutManager(this, 4);
        layoutManager.supportsPredictiveItemAnimations();

        emptyTextView = (TextView) findViewById(android.R.id.empty);
        emptyTextView.setText(R.string.no_photo);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .build();
        multiChooserAdapter = new MultiChooserAdapter(this, new ImageSize(100,100));
        multiChooserAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(multiChooserAdapter);

        preview = (Button) findViewById(R.id.button_preview);
        preview.setVisibility(View.VISIBLE);
        confirm = (Button) findViewById(R.id.button_done);
    }

    //force to show overflow menu in actionbar for android 4.4 below
    private void forceShowOverflow() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_choose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.done == item.getItemId()) {
            onDoneClick(null);
            return true;
        } else if (R.id.smart_crop == item.getItemId()) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        multiChooserAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        multiChooserAdapter.swapCursor(null);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (multiChooserAdapter != null) {
            multiChooserAdapter.registerAdapterDataObserver(observer);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (multiChooserAdapter != null) {
            multiChooserAdapter.unregisterAdapterDataObserver(observer);
        }
    }

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (multiChooserAdapter != null) {
                if (multiChooserAdapter.getItemCount() != 0) {
                    emptyTextView.setVisibility(View.GONE);
                } else {
                    emptyTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PhotoViewer.REQUEST_CODE_PREVIEW_PHOTO) {
                Lg.d("___catch activity result __");
                if (data != null) {
                    setSelectedData(data.getStringArrayListExtra(SELECTED_KEY));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Update confirm button text.
     */
    private void updateButton(List<String> selected) {
        if(selected == null) return;
        final int size = selected.size();
        preview.setEnabled(size != 0);
        String text = new StringBuilder()
                .append(getString(R.string.button_done))
                .append("(")
                .append(size)
                .append(")").toString();
        confirm.setText(text);
    }

    /**
     * Invoke it in layout xml.
     *
     * @param view
     */
    public void onPreviewClick(View view) {
        Intent intent = new Intent(getApplicationContext(), PhotoViewer.class);
        List<String> selected = multiChooserAdapter.getSelected();

        intent.putStringArrayListExtra(PhotoViewer.Constant.KEY_URIS,new ArrayList<String>(selected));
        startActivityForResult(intent, PhotoViewer.REQUEST_CODE_PREVIEW_PHOTO);
    }

    /**
     * Invoke it in layout xml.
     *
     * @param view
     */
    public void onDoneClick(View view) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(SELECTED_KEY, new ArrayList<String>(multiChooserAdapter.getSelected()));

        setResult(Activity.RESULT_OK, intent);
        PhotoChooser.this.finish();
    }

    @Override
    public void onItemClick(RecyclerView.Adapter holder, String data, int position) {
        Lg.d("___onItemClick, position = " + position);
        updateButton(multiChooserAdapter.getSelected());
    }
}
