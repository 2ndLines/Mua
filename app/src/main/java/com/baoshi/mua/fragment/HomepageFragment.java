package com.baoshi.mua.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.avos.avoscloud.AVUser;
import com.baoshi.mua.App;
import com.baoshi.mua.R;
import com.baoshi.mua.activity.ActivityTabHost;
import com.baoshi.mua.activity.BaseActivity;
import com.baoshi.mua.activity.ComposeActivity;
import com.baoshi.mua.activity.PublicationDetail;
import com.baoshi.mua.activity.SearchableActivity;
import com.baoshi.mua.adapter.PubItemAdapter;
import com.baoshi.mua.model.Carrier;
import com.baoshi.mua.model.LocationInfo;
import com.baoshi.mua.model.PublicContract;
import com.baoshi.mua.model.avos.AVPublication;
import com.baoshi.mua.model.avos.User;
import com.baoshi.mua.model.orm.OrmPubList;
import com.baoshi.mua.model.orm.OrmPublication;
import com.baoshi.mua.model.orm.OrmPublicationContract;
import com.baoshi.mua.request.PubDownloadRequest;
import com.baoshi.mua.request.PubUploadRequest;
import com.baoshi.mua.utils.Lg;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.persistence.ormlite.RoboSpiceDatabaseHelper;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Created by ThinkPad on 2014/11/9.
 */
public class HomepageFragment extends BaseFragment<ActivityTabHost> implements BaseActivity.HostCallback,
        AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2<ListView>{
    private static final int REQUEST_CODE_COMPOSE = 10;

    private ViewPager viewPager;
//    private TabsPagerAdapter tabsAdapter;
    private ArrayAdapter<String> drawerListAdapter;
    private ListView drawerList;

    private PubItemAdapter itemAdapter;
    private List<OrmPublication>  pubList;
//    private PagerTabStrip tabStrip;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private String mTitle;
//    private RecyclerView recyclerView;
//    private SwipeRefreshLayout swiper;
    private boolean isSearchViewValid ;
    private PullToRefreshListView refreshWrapper;
    private ListView itemListView;
//    private View rvHeader;
//    private View rvFooter;

    private void refreshPublications(){
        Lg.d("___refresh publications !!");
        PubDownloadRequest request = new PubDownloadRequest(OrmPubList.class);
        final SpiceManager spiceManager = getHostActivity().getSpiceManager();
        spiceManager.execute(request, OrmPubList.CACHE_KEY_ALL, DurationInMillis.ALWAYS_EXPIRED,new RequestListener<OrmPubList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Lg.d("__fail to download , Cause = " + spiceException.getMessage());
                refreshWrapper.onRefreshComplete();
            }

            @Override
            public void onRequestSuccess(OrmPubList list) {
                Lg.d("____Publication request success !! data size = " + list.getAllPubs().size() );
                Collection<OrmPublication> publications =list.getAllPubs();
                itemAdapter.addAll(publications);
                refreshWrapper.onRefreshComplete();
            }
        });
    }

    private <T> void printInfo(Class<T> clazz){
        RoboSpiceDatabaseHelper helper = getHostActivity().getHelper();

        RuntimeExceptionDao<T,String> dao = helper.getRuntimeExceptionDao(clazz);

        Lg.d(String.format("___printInfo, {} size = {}", clazz.getSimpleName(), dao.countOf()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getHostActivity().setHostCallback(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(drawerLayout == null) {
            drawerLayout = (DrawerLayout) inflater.inflate(R.layout.fragment_homepage, container, false);
            refreshWrapper = (PullToRefreshListView) drawerLayout.findViewById(R.id.refreshable_view);
            refreshWrapper.setOnRefreshListener(this);
            itemListView = refreshWrapper.getRefreshableView();
            initItemListView(itemListView);
            initDrawerLayout();
        }
        removeFromParent(drawerLayout);
        return drawerLayout;
    }

    private void initItemListView(ListView listView){
        if(listView != null) {
            listView.setOnItemClickListener(this);
            itemAdapter = new PubItemAdapter(getActivity());
            listView.setAdapter(itemAdapter);
            refreshWrapper.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            refreshWrapper.setRefreshing(false);
        }
    }

    private void initDrawerLayout(){
        initDrawerList(drawerLayout);
        mTitle = getActionBar().getTitle().toString();
        drawerLayout.setDrawerShadow(null, GravityCompat.START);
        drawerToggle = new ActionBarDrawerToggle(getHostActivity(),drawerLayout,
                R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                getHostActivity().moveTabWidget((long) (slideOffset* 1000));
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(R.string.select_place);
                ActivityCompat.invalidateOptionsMenu(getActivity());
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                ActivityCompat.invalidateOptionsMenu(getActivity());
                getActionBar().setTitle(mTitle);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void initDrawerList(View view){
        String[] items = new String[]{"Item0","Item1","Item2","Item3"};
        drawerListAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,items);

        drawerList = (ListView) view.findViewById(R.id.left_drawer);
        drawerList.setAdapter(drawerListAdapter);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(REQUEST_CODE_COMPOSE == requestCode && data != null){
                AVPublication publication = createPublication(data.getExtras());
                itemAdapter.insert(new OrmPublication(publication), 0);
                uploadPublication(publication);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private AVPublication createPublication(Bundle bundle){
        User user = AVUser.getCurrentUser(User.class);
        Lg.d("___createPublication(), nickname = " + user.getNickname());
        AVPublication publication = new AVPublication(user);

        String carJson = bundle.getString(OrmPublicationContract.CARRIER);
        publication.setDeliveredAt(Calendar.getInstance(Locale.CHINA).getTime());
        publication.setCarrier(Carrier.toJava(carJson));
        publication.setCaption(bundle.getString(OrmPublicationContract.CAPTION));

        LocationInfo locationInfo = bundle.getParcelable(OrmPublicationContract.LOCATIONINFO);
        publication.setLocationInfo(locationInfo);
        publication.setGeoPoint(locationInfo.getPoint().getLatitude(), locationInfo.getPoint().getLongitude());
        List<String> images =  bundle.getStringArrayList(PublicContract.IMAGES);
        publication.setImageUris(images);

        return publication;
    }

    private void uploadPublication(AVPublication publication){
        PubUploadRequest request = new PubUploadRequest(publication,
                App.getInstance().generateThumbnailSize(getResources().getDimensionPixelSize(R.dimen.thumbnail_size)));

        SpiceManager spiceManager = getHostActivity().getSpiceManager();

        spiceManager.execute(request,new RequestListener<Void>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Lg.d("___spiceException = " + spiceException.getMessage());
            }

            @Override
            public void onRequestSuccess(Void aVoid) {
                Lg.d("___[Success to save publication]___");
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_discovery, menu);
        MenuItem newItem = menu.findItem(R.id.menu_new);

        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.setGroupVisible(R.id.menu_group,!drawerOpen);
        if(!drawerOpen){
            initSearchView(menu);
        }

    }

    private void initSearchView(Menu menu){
        final MenuItem search = menu.findItem(R.id.menu_search);

        SearchView sv = (SearchView) MenuItemCompat.getActionView(search);
        if(sv != null){
            ComponentName cn = new ComponentName(getActivity().getApplicationContext(),SearchableActivity.class);
            SearchManager sm = (SearchManager) getHostActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchableInfo si = sm.getSearchableInfo(cn);

            isSearchViewValid = si != null;

            sv.setSearchableInfo(si);
            sv.setIconifiedByDefault(false);

            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    MenuItemCompat.collapseActionView(search);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        final int id = item.getItemId();
        switch (id){
            case R.id.menu_new:
                Intent intent = new Intent(getActivity().getApplicationContext(), ComposeActivity.class);
                this.startActivityForResult(intent, REQUEST_CODE_COMPOSE);
                return true;
            case R.id.menu_search:

                if(!isSearchViewValid){
                    Intent it = new Intent(getActivity().getApplicationContext(), SearchableActivity.class);
                    startActivity(it);
                    return true;
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt("tab",viewPager.getCurrentItem());
    }

    private void selectItem(int position){
        drawerList.setItemChecked(position, true);
        mTitle = drawerListAdapter.getItem(position);
        setTitle(mTitle);
        drawerLayout.closeDrawer(drawerList);
    }

    private void setTitle(String title){
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    public void callback() {
        handleCallback();
    }

    private void handleCallback() {
        Lg.d("___handle callback__");
        if(drawerLayout != null && drawerLayout.isDrawerOpen(drawerList)){
            drawerLayout.closeDrawer(drawerList);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Lg.d("_ ItemAdapter__position = " + position);
        Adapter adapter = parent.getAdapter();

        OrmPublication item = (OrmPublication) adapter.getItem(position);
        Lg.d("___publication Item click, caption = " + item.getCaption() );
        App.getInstance().setOrmPublication(item);
        Intent intent = new Intent(getActivity().getApplicationContext(), PublicationDetail.class);
        startActivity(intent);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        refreshPublications();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    private class DrawerItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}