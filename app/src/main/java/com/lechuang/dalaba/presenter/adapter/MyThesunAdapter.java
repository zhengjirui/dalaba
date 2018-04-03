package com.lechuang.dalaba.presenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lechuang.dalaba.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by zhf on 2017/8/8.
 * 【我要分享---添加图片适配器】
 */

public class MyThesunAdapter extends RecyclerView.Adapter<MyThesunAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mImages;
    private LayoutInflater mInflater;

    public static final int ADD_IMAGE = 1;
    public final static int TYPE_PHOTO = 2;
    public final static int MAX = 9;

    public MyThesunAdapter(Context context) {
        mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case ADD_IMAGE:
                view = mInflater.inflate(R.layout.item_add, parent, false);
                break;
            case TYPE_PHOTO:
                view = mInflater.inflate(R.layout.adapter_image, parent, false);
                break;
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_PHOTO) {
            final String image = mImages.get(position);
            Glide.with(mContext).load(new File(image)).into(holder.ivImage);
            //
            holder.iv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImages.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        //return mImages == null ? 0 : mImages.size();
        if (null == mImages) {
            return (mImages = new ArrayList<>()).size() + 1;
        }
        int count = mImages.size() + 1;
        if (count > MAX) {
            count = MAX;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mImages.size() && position != MAX) ? ADD_IMAGE : TYPE_PHOTO;
    }

    public void refresh(ArrayList<String> images) {
        mImages = images;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage, iv_remove;//图片和删除小图标

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            iv_remove = (ImageView) itemView.findViewById(R.id.iv_remove);
        }
    }
}
