package com.baoshi.mua.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoshi.mua.R;
import com.baoshi.mua.model.orm.OrmPublication;

import java.util.List;

/**
 * Created by ThinkPad on 2014/12/6.
 */
public class PubItemAdapter extends android.widget.ArrayAdapter<OrmPublication> {
    private int resourceId;
    private Context context;

    public PubItemAdapter(Context context){
        this(context, R.layout.layout_publication_item);
    }

    public PubItemAdapter(Context context, int resource) {
        super(context, resource);
        init(context,resource);
    }


    public PubItemAdapter(Context context, int resource, List<OrmPublication> objects) {
        super(context, resource, objects);
        init(context,resource);
    }

    private void init(Context _cxt, int resource){
        resourceId = resource;
        context = _cxt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PublicationViewHolder viewHolder = null;

        if(convertView == null) {
            final View itemView = LayoutInflater.from(context).inflate(resourceId,parent,false);
            viewHolder = new PublicationViewHolder(itemView);
            itemView.setTag(viewHolder);
        }else{
            viewHolder = (PublicationViewHolder) convertView.getTag();
        }

        viewHolder.bindView(getItem(position));

        return viewHolder.itemView;
    }

}
