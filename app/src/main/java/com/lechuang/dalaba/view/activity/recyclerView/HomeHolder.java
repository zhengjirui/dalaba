package com.lechuang.dalaba.view.activity.recyclerView;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.MyApplication;

/**
 * Author: LGH
 * Date: 2017/12/6
 * Description:
 */

public class HomeHolder extends BaseViewHolder {

    public HomeHolder(View view) {
        super(view);
    }

    public HomeHolder displayImage(int viewId, String url) {
        ImageView mImageView = getView(viewId);
        Glide.with(MyApplication.getInstance()).load(url).placeholder(R.drawable.zhuan_shangpinjiazai).into(mImageView);
        return this;
    }

    public HomeHolder displayImage(int viewId, String url, String s) {
        ImageView mImageView = getView(viewId);
        Glide.with(MyApplication.getInstance()).load(url).into(mImageView);
        return this;
    }

    public HomeHolder displayImage(int viewId, String url, int imgUrl) {
        ImageView mImageView = getView(viewId);
        Glide.with(MyApplication.getInstance()).load(url).placeholder(imgUrl).into(mImageView);
        return this;
    }
}
