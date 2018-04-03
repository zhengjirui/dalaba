package com.lechuang.dalaba.mine.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.MyApplication;
import com.lechuang.dalaba.view.defineView.RoundedImageView;
import com.lechuang.dalaba.view.defineView.SpannelTextView;
import com.lechuang.dalaba.view.defineView.SpannelTextViewGrid;

/**
 * @author: LGH
 * @since: 2017/12/18
 * @describe: 通用 Recycler 的 ViewHolder
 */

public class ViewHolderRecycler extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;
    private Context mContext;

    public ViewHolderRecycler(Context context, View itemView, ViewGroup parent) {
        super(itemView);
        mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<View>();
    }


    public static ViewHolderRecycler get(Context context, ViewGroup parent, int layoutId) {

        View itemView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        ViewHolderRecycler holder = new ViewHolderRecycler(context, itemView, parent);
        return holder;
    }


    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    //获取Layout
    public View getmConvertView() {
        return mConvertView;
    }

    //TextView设置数据
    public ViewHolderRecycler setText(int viewId, String txt) {
        TextView mTextView = getView(viewId);
        mTextView.setText(txt);
        return this;
    }

    //TextView设置数据
    public ViewHolderRecycler setSpannelTextViewGrid(int viewId, String txt, int shopType) {
        SpannelTextViewGrid mTextView = getView(viewId);
        mTextView.setDrawText(txt);
        mTextView.setShopType(shopType);
        return this;
    }

    //TextView设置数据
    public ViewHolderRecycler setSpannelTextView(int viewId, String txt, int shopType) {
        SpannelTextView mTextView = getView(viewId);
        mTextView.setDrawText(txt);
        mTextView.setShopType(shopType);
        return this;
    }

    //ImageView设置数据
    public ViewHolderRecycler setImageResource(int viewId, int img) {
        ImageView mImageView = getView(viewId);
        mImageView.setImageResource(img);
        return this;
    }

    public ViewHolderRecycler displayImage(int viewId, String url) {
        ImageView mImageView = getView(viewId);
        Glide.with(MyApplication.getInstance()).load(url).into(mImageView);
        return this;
    }

    public ViewHolderRecycler displayImage(int viewId, String url, int defaultView) {
        ImageView mImageView = getView(viewId);
        Glide.with(MyApplication.getInstance()).load(url).placeholder(defaultView).into(mImageView);
        return this;
    }

    public ViewHolderRecycler displayRoundImage(int viewId, String url) {
        RoundedImageView mImageView = getView(viewId);
        Glide.with(MyApplication.getInstance()).load(url).into(mImageView);
        return this;
    }
    /**
     * 设置TextView，含有图标
     *
     * @param viewId
     * @param txt
     * @param shopType
     * @return
     */
    public ViewHolderRecycler setTextViewImageSpan(int viewId, String txt, int shopType) {
        TextView mTextView = getView(viewId);
        SpannableString ss = new SpannableString(" " + txt);
        Bitmap bitmap = BitmapFactory.decodeResource(MyApplication.getInstance().getResources(),
                shopType == 1 ? R.drawable.zhuan_taobao : R.drawable.zhuan_tmall);
        ss.setSpan(new ImageSpan(MyApplication.getInstance(),bitmap,ImageSpan.ALIGN_BASELINE),
                0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextView.setText(ss);
        return this;
    }
}
