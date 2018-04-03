package com.lechuang.dalaba.view.activity.own;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseActivity;

/**
 * @author yrj
 * @date   2017/10/10
 * @E-mail 1422947831@qq.com
 * @desc   消息详情
 */
public class NewsDetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_news_details;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.iv_back).setVisibility(View.GONE);
        findViewById(R.id.iv_back2).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_title)).setText("消息详情");
        ((TextView) findViewById(R.id.tv_news_title)).setText("消息通知");
        ((TextView) findViewById(R.id.tv_time)).setText(getIntent().getStringExtra("time"));
        ((TextView) findViewById(R.id.tv_content)).setText(getIntent().getStringExtra("content"));
    }

    @Override
    protected void initData() {

    }

}
