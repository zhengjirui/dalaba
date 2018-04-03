package com.lechuang.dalaba.view.activity.own;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseActivity;


/**
 * Created by yrj on 2017/8/16.
 * 支付成功页面
 */

public class PaySuccessActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_pay_success;
    }

    @Override
    protected void initTitle() {
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.tv_title)).setText("支付成功");
        findViewById(R.id.iv_back).setVisibility(View.GONE);
        findViewById(R.id.iv_back2).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_invite).setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_invite: //邀请好友
                startActivity(new Intent(this, ShareMoneyActivity.class));
                finish();
                break;
            default:
                break;
        }
    }


}
