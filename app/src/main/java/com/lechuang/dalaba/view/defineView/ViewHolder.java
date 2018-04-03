package com.lechuang.dalaba.view.defineView;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lechuang.dalaba.base.MyApplication;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by yrj on 2017/8/15.
 * 公共的viewHolder
 */

public class ViewHolder {
    //存储各个控件View
    private SparseArray<View> mView;
    //记录索引位置
    private int mPosition;
    private View mConvertView;

    //构造方法
    public ViewHolder(Context context, int position, ViewGroup parent, int layoutId) {
        mPosition = position;
        mView = new SparseArray<View>();
        mConvertView = View.inflate(context, layoutId, null);
        mConvertView.setTag(this);
    }

    //获取ViewHolder
    public static ViewHolder getViewHolder(Context context, int position,
                                           View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            return new ViewHolder(context, position, parent, layoutId);
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mPosition = position;
            return viewHolder;
        }
    }

    //获取各个控件
    public <T extends View> T getView(int layoutId) {
        View view = mView.get(layoutId);
        if (view == null) {
            view = getmConvertView().findViewById(layoutId);
            mView.put(layoutId, view);
        }
        return (T) view;
    }


    //获取Layout
    public View getmConvertView() {
        return mConvertView;
    }

    //TextView设置数据
    public ViewHolder setText(int viewId, String txt) {
        TextView mTextView = getView(viewId);
        mTextView.setText(txt);
        return this;
    }

    //TextView设置数据
    public ViewHolder setSpannelTextViewGrid(int viewId, String txt, int shopType) {
        SpannelTextViewGrid mTextView = getView(viewId);
        mTextView.setDrawText(txt);
        mTextView.setShopType(shopType);
        return this;
    }

    //ImageView设置数据
    public ViewHolder setImageResource(int viewId, int img) {
        ImageView mImageView = getView(viewId);
        mImageView.setImageResource(img);
        return this;
    }

    public ViewHolder displayImage(int viewId, String url) {
        ImageView mImageView = getView(viewId);
        Glide.with(MyApplication.getInstance()).load(url).into(mImageView);
        return this;
    }

    public ViewHolder displayRoundImage(int viewId, String url) {
        RoundedImageView mImageView = getView(viewId);
        Glide.with(MyApplication.getInstance()).load(url).into(mImageView);
        return this;
    }
    public ViewHolder displaySquareImageView(int viewId, String url) {
        SquareImageView mImageView = getView(viewId);
        Glide.with(MyApplication.getInstance()).load(url).into(mImageView);
        return this;
    }
}
