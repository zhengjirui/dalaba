package com.lechuang.dalaba.view.activity.own;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.utils.PhotoUtil;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.ClearEditText;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lechuang.dalaba.utils.PhotoUtil.t;

public class CheckIdentityActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_phone, tv_code;
    private ClearEditText et_code;
    private Button btn_change_ok;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_identity);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        initView();
        initdate();
    }


    private void initView() {
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        et_code = (ClearEditText) findViewById(R.id.et_code);
        tv_code = (TextView) findViewById(R.id.tv_code);
        btn_change_ok = (Button) findViewById(R.id.btn_change_ok);
        tv_code.setOnClickListener(this);
        btn_change_ok.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (!sp.getString("phone", "").equals("")) {
            tv_phone.setText(sp.getString("phone", ""));
        }
    }

    private void initdate() {

    }


    private LocalSession mSession = LocalSession.get(this);

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_code:
                if (Utils.isNetworkAvailable(this)) {
                    //获取验证码
                    getCheckCode();
                } else {
                    Utils.show(this, getString(R.string.net_error));
                }

                break;
            case R.id.btn_change_ok:
                if (Utils.isEmpty(et_code)) {
                    Utils.show(this, "验证码不能为空!");
                    return;
                }
                Netword.getInstance().getApi(CommenApi.class)
                        .getVerifiCode(et_code.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ResultBack<String>(this) {

                            @Override
                            public void successed(String result) {
                                if (getIntent().getIntExtra("type", 2) == 1) {
                                    //更换手机号码
                                    startActivity(new Intent(CheckIdentityActivity.this, ChangePhoneNumberActivity.class));
                                    finish();
                                } else {
                                    //更绑支付宝
                                    if (mSession.getAlipayNumber().equals("")) {//没绑定支付宝
                                        startActivity(new Intent(CheckIdentityActivity.this, BoundAlipayActivity.class));
                                    } else {
                                        startActivity(new Intent(CheckIdentityActivity.this, ChangeBoundAlipayActivity.class).putExtra("pay", sp.getString("alipayNumber", "")));
                                    }
                                    finish();
                                }
                            }
                        });

                break;
            default:
                break;
        }

    }


    //验证码
    private void getCheckCode() {
        try {
            Netword.getInstance().getApi(CommenApi.class)
                    .getCheckCode()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<String>(this) {

                        @Override
                        public void successed(String result) {

                            tv_code.setEnabled(false);
                            PhotoUtil.getCode(tv_code);
                            PhotoUtil.handler.post(t);
                            Utils.show(CheckIdentityActivity.this, result);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhotoUtil.closeCode();
    }
}
