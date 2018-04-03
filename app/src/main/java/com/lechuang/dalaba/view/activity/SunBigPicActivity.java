package com.lechuang.dalaba.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.lechuang.dalaba.R;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.BigPicMap;

import java.util.ArrayList;

/**
 * @author zhf 2017/08/25
 * 【晒单图片放大】
 */

public class SunBigPicActivity extends AppCompatActivity implements BigPicMap.OnItemClickListener {
    private BigPicMap iv_sun_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sun_big_pic);
        initView();
        initEvents();
    }
    public void initView() {
        iv_sun_map = (BigPicMap) findViewById(R.id.iv_sun_map);
    }

    public void initEvents() {

        iv_sun_map.setOnItemClickListener(this);
        if(getIntent().getIntExtra("live",0)==1){
            ArrayList<String> list = new ArrayList<>();
            list.add(getIntent().getStringExtra("bigImg"));
            iv_sun_map.setAdapter(list);
        }else{
            ArrayList<String> list = getIntent().getStringArrayListExtra("list");
            Utils.E(list.toString());
            iv_sun_map.setAdapter(getIntent().getStringArrayListExtra("list"));
            iv_sun_map.setCurrentItem(getIntent().getIntExtra("current", 0));
        }
    }

    @Override
    public void onItemClick(int position, Object object) {

    }
}
