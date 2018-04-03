package com.lechuang.dalaba.presenter.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 作者： li
 * 邮箱：961567115@qq.com
 * 时间：on 2018/1/4-16:54
 * 域名：com.lechuang.dalaba.presenter.adapter
 */

public class ProfremProductAdapter<T> extends BaseAdapter{
    public List<T> listBeans;
    public ProfremProductAdapter(List<T> listBean){
        this.listBeans=listBean;
    }
    @Override
    public int getCount() {
        return listBeans==null?0:listBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return listBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){}
        return null;
    }
}
