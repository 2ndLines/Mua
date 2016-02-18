package com.baoshi.mua.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.baoshi.mua.R;
import com.baoshi.mua.activity.ActivityTabHost;
import com.baoshi.mua.model.avos.User;
import com.baoshi.mua.view.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ThinkPad on 2014/11/9.
 */
public class MeFragment extends BaseFragment<ActivityTabHost> implements View.OnClickListener , AdapterView.OnItemClickListener{
    private ListView mListView;
//    private View mHeader;
    private TextView mTVNearBy, mTVFriends, mTVAboutMe;

    private CircleImageView mUserHead;
    private TextView mNicknameView;
    private TextView mSignatureView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mListView == null) {
            mListView = new ListView(getActivity());
            View mHeader = inflater.inflate(R.layout.layout_header_mine, null, false);
            mListView.addHeaderView(mHeader);
            initHeader(mHeader);
            initListView();

        }
        removeFromParent(mListView);
        return mListView;
    }

    private void initHeader(View header){
        if(header == null) {
            throw new IllegalArgumentException("Header view must mot be null!!");
        }
        mTVNearBy = (TextView) header.findViewById(R.id.navi_item_nearby);
        mTVFriends = (TextView) header.findViewById(R.id.navi_item_friends);
        mTVAboutMe = (TextView) header.findViewById(R.id.navi_item_aboutme);

        mTVNearBy.setOnClickListener(this);
        mTVAboutMe.setOnClickListener(this);
        mTVFriends.setOnClickListener(this);

        mUserHead = (CircleImageView) header.findViewById(R.id.user_head);
        mUserHead.setOnClickListener(this);

        User user = AVUser.getCurrentUser(User.class);
        if(user != null) {
            TextView nickNameView = (TextView) header.findViewById(R.id.user_nickname);
            nickNameView.setText(user.getNickname());
            TextView signature = (TextView) header.findViewById(R.id.user_signature_text);
            signature.setText(user.getMobilePhoneNumber());
        }

    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id){
            case R.id.user_head:
                break;
            case R.id.navi_item_nearby:
                break;
            case R.id.navi_item_friends:
                break;
            case R.id.navi_item_aboutme:
                break;
        }

    }

    private void initListView(){
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mListView.setLayoutParams(lp);
        mListView.setOnItemClickListener(this);

        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),createAdapterData(),
                R.layout.layout_mine_navi_item,new String[]{"icon", "title"}, new int[]{R.id.navi_icon,R.id.navi_title});

        mListView.setAdapter(simpleAdapter);
    }

    private List<Map<String, Object>> createAdapterData(){
        final String[] itemTexts =   getResources().getStringArray(R.array.mine_vertical_navi_item_list);
        final int[] drawables = new int[]{
                R.drawable.ic_local_florist_grey600_36dp,
                R.drawable.ic_follow_grey600_36dp,
                R.drawable.ic_stars_grey600_36dp,
                R.drawable.ic_settings_applications_grey600_36dp
        };

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

        for(int i = 0; i < itemTexts.length; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("icon", drawables[i]);
            item.put("title", itemTexts[i]);

            data.add(item);
        }

        return data;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


}
