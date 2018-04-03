package com.lechuang.dalaba.presenter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lechuang.dalaba.view.defineView.ViewHolder;

import java.util.List;

/**
 * Created by yrj on 2017/8/15.
 * 自定义的适配器Adapter
 */


public abstract class CommonAdapter<T> extends BaseAdapter {

    protected List<T> mDatas;
    protected Context context;
    private int layoutId;

    public CommonAdapter(Context context, List<T> mDatas, int layoutId) {
        this.mDatas = mDatas;
        this.context = context;
        this.layoutId = layoutId;
    }


    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas == null ? null : mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDatas == null ? 0 : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.getViewHolder(context, position, convertView, parent, layoutId);
        setData(viewHolder, mDatas.get(position));
        setData2(viewHolder, mDatas.get(position),position);
        return viewHolder.getmConvertView();
    }


    /*
    * adapter继承MyAdapter 之后,只需要重写setData方法
    * */
    public abstract void setData(ViewHolder viewHolder, Object item);
    public  void setData2(ViewHolder viewHolder, Object item,int position){

    };


}