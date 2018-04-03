package com.lechuang.dalaba.view.activity.own;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.dalaba.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangeBoundAlipayActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_change_binding)
    TextView tvChangeBinding;
    @BindView(R.id.btn_changeBound)
    Button btn_changeBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_bound_alipay);
        ButterKnife.bind(this);
        initView();
    }

    public void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText("支付宝更绑");
        tvChangeBinding.setText("已绑定支付宝账户: " + getIntent().getStringExtra("pay"));
    }
    @OnClick({R.id.iv_back, R.id.btn_changeBound})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_changeBound:
                Intent intent = new Intent(this, BoundAlipayActivity.class);
                startActivityForResult(intent, 1);//开启换绑界面
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) { //从绑定界面返回的数据
            //((TextView) findViewById(R.id.tv_change_binding)).setText("已绑定支付宝账户: " + data.getStringExtra("ZfbNumber"));
            tvChangeBinding.setText("已绑定支付宝账户: " + data.getStringExtra("ZfbNumber"));
        }
    }


}
