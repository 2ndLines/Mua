package com.baoshi.mua.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.baoshi.mua.R;
import com.baoshi.mua.activity.PlaceActivity;
import com.baoshi.mua.helper.PoiSearchHelper;
import com.baoshi.mua.listener.PoiSearchCompleteListener;
import com.baoshi.mua.model.LocationInfo;
import com.baoshi.mua.utils.Lg;
import com.baoshi.mua.view.fab.FloatingActionButton;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

//import com.baoshi.mua.adapter.ArrayAdapter;

/**
 * Created by ThinkPad on 2014/11/16.
 */
public class PlaceChooseFragment extends BaseFragment<PlaceActivity> implements
        PoiSearchCompleteListener, PullToRefreshListView.OnRefreshListener2<ListView>, AdapterView.OnItemClickListener {
    //    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    //    private ArrayAdapter<LocationInfo> adapter;
    private PoiSearchHelper searchHelper;
    private PullToRefreshListView refreshWrapper;
    private LocInfoAdapter adapter;
    private ListView listView;
    private AutoCompleteTextView completeTextView;
    private Button searchButton;

    private LinearLayout searchBar;
    private String searchedCity;
    private String oldQueryString;
    private boolean pullFromStart;

    private void initSearchBar() {
        searchBar = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.search_bar, null, false);
        completeTextView = (AutoCompleteTextView) searchBar.findViewById(R.id.auto_complete_textview);
        completeTextView.addTextChangedListener(searchTextWatcher);
        completeTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Lg.d("___item click ., position = " + position);
//                hideIMM(view);
                onSearchButtonClick(parent.getAdapter().getItem(position).toString(),null);
            }
        });
        searchButton = (Button) searchBar.findViewById(R.id.search_go_btn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(completeTextView.getText()!=null){
                    String text = completeTextView.getText().toString().trim();
                    onSearchButtonClick(text,searchedCity);
                }
            }
        });
    }

    private void onSearchButtonClick(String searchText,String city){
        if(searchText!= null && !searchText.isEmpty()){
            searchHelper.initQueryAndSearch(searchText,null,city,null,-1);
        }
        hideIMM(completeTextView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchHelper = PoiSearchHelper.getInstance(getActivity());
        searchHelper.setOnSearchCompleteListener(this);
        setHasOptionsMenu(true);
        initSearchBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_place_chooser, container, false);

        refreshWrapper = (PullToRefreshListView) root.findViewById(R.id.refreshable_view);
        refreshWrapper.setOnRefreshListener(this);
        refreshWrapper.getLoadingLayoutProxy(true,false).setPullLabel(getResources().getString(R.string.refresh_label_current_place));
        listView = refreshWrapper.getRefreshableView();
        listView.setOnItemClickListener(this);
        adapter = new LocInfoAdapter(getActivity(), android.R.layout.simple_list_item_2);
        listView.setAdapter(adapter);
        listView.addHeaderView(searchBar, null, false);
        fab = (FloatingActionButton) root.findViewById(R.id.floating_action_button);
        fab.attachToListView(listView);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        searchHelper.searchAround();
        refreshWrapper.setRefreshing(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        searchHelper.reset();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(android.R.id.home == item.getItemId()){
            getActivity().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillData(List<PoiItem> poiItems, boolean reload) {
        if (poiItems != null && poiItems.size() > 0) {
            Lg.d("___poi item size = " + poiItems.size());
            List<LocationInfo> data = new ArrayList<LocationInfo>();
            for (PoiItem item : poiItems) {
                StringBuilder address = new StringBuilder(/*"Distance:").append(item.getDistance() / 1000.0).append("km "*/)
                        .append(item.getCityName())
                        .append(item.getAdName())
                        .append(item.getSnippet());

                LocationInfo info = new LocationInfo();
                info.setTitle(item.getTitle());
                info.setPoint(item.getLatLonPoint());
                info.setProvince(item.getProvinceName());
                info.setCity(item.getCityName());
                info.setDistrict(item.getAdName());
                info.setAddress(address.toString());
                data.add(info);
            }
            if(reload){
                adapter.clear();
            }
            int position = adapter.getCount();
            adapter.addAll(data);
            listView.smoothScrollToPositionFromTop(position,200);
        }
    }

    @Override
    public void onPoiSearchComplete(PoiSearch.Query query, PoiResult result, int resultCode) {
        Lg.d("___is refreshing = " + refreshWrapper.getState());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshWrapper.onRefreshComplete();
            }
        }, 200);
        if(query != null){
            Lg.d("__search complete !!__resultCode = " + resultCode + ", pageIndex = " + query.getPageNum()+", queryString = " + query.getQueryString());
        }

        if (result != null && result.getQuery().equals(query)) {
            List<PoiItem> poiItems = result.getPois();
            if(poiItems != null && poiItems.size() > 0){
                fillData(poiItems, (!result.getQuery().getQueryString().equals(oldQueryString)) || pullFromStart);

                oldQueryString = result.getQuery().getQueryString();
            }else{
                List<SuggestionCity> citys = result.getSearchSuggestionCitys();
                if(citys != null && citys.size() > 0){
                    showSuggestionCityDialog(query.getQueryString(), citys);
                }
                Lg.d("___suggestionCity size = " + citys.size());
            }
        }
        pullFromStart = false;
    }

    private void showSuggestionCityDialog(final String queryString, List<SuggestionCity> cityList){
        final List<String> citys = new ArrayList<String>();
        for(SuggestionCity city : cityList){

            citys.add(city.getCityName());
        }

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.choose_city)
                .setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.popup_item_1, citys),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onSearchButtonClick(queryString, citys.get(which));
                        dialog.dismiss();
                    }
                }).create().show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        Lg.d("___pick position = " + position + ",  _id = " + id);
        intent.putExtra(LocationInfo.KEY, adapter.getItem((int)id));
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        Lg.d("___Pull down__");
        searchHelper.searchAround();
        pullFromStart = true;
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        Lg.d("___Pull up__");
        searchHelper.nextPage();
        pullFromStart = false;
    }

    private class PlaceHolder {
        TextView title;
        TextView address;
        View itemView;

        public PlaceHolder(View itemView) {
            this.itemView = itemView;
            findView();
        }

        private void findView() {
            title = (TextView) itemView.findViewById(android.R.id.text1);
            address = (TextView) itemView.findViewById(android.R.id.text2);
        }

        public void setLocInfo(LocationInfo info) {
            setTitle(info.getTitle());
            setAddress(info.getAddress());
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setAddress(String addr) {
            this.address.setText(addr);
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder {
        SearchView searchView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            searchView = (SearchView) itemView;
        }
    }

    private class LocInfoAdapter extends ArrayAdapter<LocationInfo> {

        private int resId;

        public LocInfoAdapter(Context context, int resource) {
            super(context, resource);
            resId = resource;
        }

        public LocInfoAdapter(Context context, int resource, List<LocationInfo> objects) {
            super(context, resource, objects);
            resId = resource;
        }

        private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
            PlaceHolder holder;

            if (convertView == null) {
                View view = LayoutInflater.from(getContext()).inflate(resource, parent, false);
                holder = new PlaceHolder(view);
                view.setTag(holder);
            } else {
                holder = (PlaceHolder) convertView.getTag();
            }

            holder.setLocInfo(getItem(position));

            return holder.itemView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return createViewFromResource(position, convertView, parent, resId);
        }
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String newText = s.toString();
            // 输入信息的回调方法
            Inputtips inputTips = new Inputtips(getActivity(), new Inputtips.InputtipsListener() {

                @Override
                public void onGetInputtips(List<Tip> tipList, int rCode) {
                    if(tipList != null){
                        List<String> listString = new ArrayList<String>();
                        for (int i = 0; i < tipList.size(); i++) {
                            listString.add(tipList.get(i).getName());
                        }
                        ArrayAdapter<String> aAdapter = new ArrayAdapter(
                                getActivity().getApplicationContext(),
                                R.layout.popup_item_1, listString);
                        completeTextView.setAdapter(aAdapter);
                        aAdapter.notifyDataSetChanged();
                    }

                }
            });
            try {
                // 发送输入提示请求
                // 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号
                inputTips.requestInputtips(newText, "");
            } catch (AMapException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
