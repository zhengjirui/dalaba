package com.lechuang.dalaba.view.activity.own;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lechuang.dalaba.R;

/**
 * Author: guoning
 * Date: 2017/10/10
 * Description:
 */

public class PayFailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_fail);
        initViews();
    }

    private void initViews() {
        ((TextView) findViewById(R.id.tv_title)).setText("支付失败");

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void finish(View view) {
        finish();
    }
}
