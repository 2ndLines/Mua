package com.baoshi.mua.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.internal.widget.CompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoshi.mua.App;
import com.baoshi.mua.R;
import com.baoshi.mua.SpannableStringHelper;
import com.baoshi.mua.activity.PhotoViewer;
import com.baoshi.mua.listener.OnItemClickListener;
import com.baoshi.mua.model.LocationInfo;
import com.baoshi.mua.model.orm.OrmPubFollow;
import com.baoshi.mua.model.orm.OrmPublication;
import com.baoshi.mua.model.orm.OrmUser;
import com.baoshi.mua.provider.MuaDatabaseHelper;
import com.baoshi.mua.utils.LazyDateUtil;
import com.baoshi.mua.utils.Lg;
import com.baoshi.mua.view.CircleImageView;
import com.baoshi.mua.view.SelfAdapteLayoutManager;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by ThinkPad on 2014/11/11.
 */
public class PublicationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnItemClickListener<String>{
    /**
     * To show photos
     */
    private RecyclerView recyclerView;
    /**
     * The time publishing the publication
     * Style:HH:mm
     */
    private TextView mHourMin;
    /**
     * The short date publishing the publication
     * Style: yyyy:MM:dd or week day
     */
    private TextView mShortDate;

    private TextView captionView;
    private CircleImageView circleHead;
    /**
     * Where the publication delivered .
     */
    private TextView location;


    private DisplayImageOptions options;
    private Context context;
    private Collection<String>  photoUrls;
    private OrmPublication publication;


    public PublicationViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        initViews(itemView);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_head_nophoto)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.NONE)
                .build();
    }

    private void initViews(View parent){
//        recyclerView = (RecyclerView) parent.findViewById(R.id.staggered_grid);
//        mHourMin = (TextView)parent.findViewById(R.id.timeaxis_time);
//        mShortDate = (TextView)parent.findViewById(R.id.timeaxis_date);
//        captionView = (TextView)parent.findViewById(R.id.caption);
//        circleHead = (CircleImageView) parent.findViewById(R.id.user_head);
//        location = (TextView)parent.findViewById(R.id.location);

    }

    private boolean isFollowed()  {
        OrmUser curUser = App.getInstance().getCurrentOrmUser();
        if(curUser != null) {
            MuaDatabaseHelper helper = OpenHelperManager.getHelper(context,MuaDatabaseHelper.class);
            Dao pubUserDao = null;
            try {
                pubUserDao = helper.getDao(OrmPubFollow.class);
                OrmPubFollow object = new OrmPubFollow(curUser,getPublication());

                List<OrmPubFollow> follows = pubUserDao.queryForMatching(object);
                if(follows != null && follows.size() != 0){
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void followOrUnfollow(){
        OrmUser curUser = App.getInstance().getCurrentOrmUser();

        if(curUser != null) {
            MuaDatabaseHelper helper = OpenHelperManager.getHelper(context,MuaDatabaseHelper.class);
            try {
                Dao pubUserDao = helper.getDao(OrmPubFollow.class);
                OrmPubFollow object = new OrmPubFollow(curUser, getPublication());

                if(isFollowed()){
                    pubUserDao.delete(object);
                }else{
                    pubUserDao.create(object);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id){
            case R.id.caption:
                CompatTextView caption = (CompatTextView) v;
                if(2 == caption.getLineCount()){
                    //expand
                    caption.setMaxLines(20);
                }else if(caption.getLineCount() > 2){
                    //shrink
                    caption.setMaxLines(2);
                }
                break;
            case R.id.user_head:
                break;
            case R.id.follow:

                break;

        }
    }

    private Collection<String> getPhotoUrls(){
        return photoUrls;
    }

    private void setPhotoUrls(Collection<String> urls){
        this.photoUrls = urls;
    }

    public OrmPublication getPublication() {
        return publication;
    }

    public void setPublication(OrmPublication publication) {
        this.publication = publication;
    }

    public void bindView(OrmPublication publication){
        setPublication(publication);
        setPhotoUrls(publication.getPhotoUrls());
        bindImage(publication.getThumbUris());
        bindCreatedTime(publication.getCreatedAt());
        bindOther(publication.getCaption(), publication.getLocationInfo(), publication.getCreator());

        ImageView followIcon = (ImageView) itemView.findViewById(R.id.follow);
        followIcon.setOnClickListener(this);
        followIcon.setSelected(isFollowed());
        itemView.findViewById(R.id.comment_shrink).setOnClickListener(this);
    }

    private void bindImage(Collection<String> images){
        RecyclerView recyclerView = (RecyclerView) itemView.findViewById(R.id.staggered_grid);
        StaggeredGridLayoutManager layoutManager = new SelfAdapteLayoutManager(1,RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if(images != null){
            SixImageAdapter adapter = new SixImageAdapter(context, null);
            recyclerView.setAdapter(adapter);
            adapter.setLayoutManager(layoutManager);
            adapter.registerAdapterDataObserver();
            adapter.addAll(images);
            adapter.setOnItemClickListener(this);
        }else{
            recyclerView.setAdapter(null);
        }
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adapter, String data, int position) {
        Lg.d("__SixImageAdapter_Publication thumbnail URL = " + data);
        if(getPhotoUrls() != null ){
            Intent intent = new Intent(context.getApplicationContext(), PhotoViewer.class);
            intent.putExtra(PhotoViewer.Constant.KEY_INIT_POSITION, position);
            intent.putExtra(PhotoViewer.Constant.KEY_URIS,new ArrayList<String>(getPhotoUrls()));
            intent.putExtra(PhotoViewer.Constant.KEY_URI_TYPE_IS_REMOTE, true);
            context.startActivity(intent);
        }
    }

    private void bindCreatedTime(Date createdAt){
        if(createdAt != null){

            TextView shortDate = (TextView) itemView.findViewById(R.id.timeaxis_date);
            TextView hourMin = (TextView) itemView.findViewById(R.id.timeaxis_time);

            String lazyTime = LazyDateUtil.getDateDetail(createdAt);
            if(lazyTime != null){
                String[] date = lazyTime.split(" ");
//                Lg.d(String.format("___time = %s, __date = %s", date[0], date[1]));
                shortDate.setText(date[0]);
                hourMin.setText(date[1]);
            }
        }
    }

    private void bindOther(String caption,LocationInfo info, OrmUser creator){
        buildCaptionString(creator.getNickname(),caption);

        ImageView circleHead = (ImageView) itemView.findViewById(R.id.user_head);
        circleHead.setOnClickListener(this);
        ImageLoader.getInstance().displayImage(creator.getHeadUrl(),circleHead,options);

        buildLocationString(info);

    }

    private void buildCaptionString(String nickname,String caption){

        if(nickname == null) {
            throw new IllegalArgumentException("Nickname must not be null. Check it please.");
        }

        SpannableString nickNameSpannable = SpannableStringHelper.getInstance().buildSpannableString(nickname,
                new SpannableStringHelper.OnSpanClickListener() {
            @Override
            public void onSpanClick(View view, String span) {
                Lg.toast(context,span);
            }
        });

        TextView captionView = (TextView) itemView.findViewById(R.id.caption);
        if(nickNameSpannable != null) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder()
                    .append(nickNameSpannable).append(caption);
            captionView.setText(spannableStringBuilder);
            captionView.setMovementMethod(LinkMovementMethod.getInstance());
        }else{
            captionView.setText(caption);
        }

    }

    private void buildLocationString(LocationInfo locationInfo){
        String title = locationInfo.getTitle();
        String province = locationInfo.getProvince();
        String city = locationInfo.getCity();
        StringBuilder builder = new StringBuilder();
        if(province.equals(city)){
            builder.append(city);//直辖市，省份跟市名相同
        }else{
            builder.append(province).append(city);
        }
        builder.append("#").append(title);

        SpannableString spannableString = SpannableStringHelper.getInstance()
                .buildSpannableString(builder.toString(),new SpannableStringHelper.OnSpanClickListener() {
                    @Override
                    public void onSpanClick(View view, String piece) {
                        Lg.toast(context,"___piece String is " + piece);
                    }
                });

        TextView location = (TextView) itemView.findViewById(R.id.location);

        location.setClickable(true);
        location.setText(spannableString);
        location.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
