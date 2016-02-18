package com.baoshi.mua.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.baoshi.mua.R;
import com.baoshi.mua.adapter.CursorAdapter;
import com.baoshi.mua.utils.Lg;

/**
 * Created by ThinkPad on 2014/11/13.
 */
public class SearchableActivity extends ActionBarActivity {
    private RecyclerView recyclerView;
//    private LinearLayoutManager layoutManager;
    private CursorAdapter cursorAdapter;
//    private SearchView searchView;
    private String query;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleSearchIntent(intent);
    }
    private void handleSearchIntent(Intent intent){
        if (intent != null && Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            Lg.d("_searchaleAct_query = " + query );

            doMySearch(query);
        }
    }

    private void doMySearch(String query) {
        //TODO
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recycler_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initViews();

        handleSearchIntent(getIntent());
    }

    private void initViews(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
//        cursorAdapter = new CursorAdapter<Publication>();

        recyclerView.setAdapter(cursorAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager sm = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if(searchView != null) {
            searchView.setSearchableInfo(sm.getSearchableInfo(getComponentName()));
            searchView.setSubmitButtonEnabled(true);
            searchView.setIconifiedByDefault(false);
            Lg.d("__onOptionMenu create query = " + query);
            searchView.setQuery(query,false);
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(android.R.id.home == item.getItemId()){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
