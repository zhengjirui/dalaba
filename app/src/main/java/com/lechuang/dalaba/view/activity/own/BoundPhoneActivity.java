package com.lechuang.dalaba.view.activity.own;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.ali.auth.third.login.callback.LogoutCallback;
import com.alibaba.baichuan.android.trade.adapter.login.AlibcLogin;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseActivity;
import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.model.bean.TaobaoLoginBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.utils.PhotoUtil;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.ClearEditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lechuang.dalaba.utils.PhotoUtil.t;


/**
 * Created by yrj on 2017/8/21.
 * 绑定手机号
 */

public class BoundPhoneActivity extends BaseActivity {
    private Context mContext = BoundPhoneActivity.this;
    private LocalSession mSession;
    private TextView tv_code;
    //手机号 验证码
    private ClearEditText et_phonenum, et_proving;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSession = LocalSession.get(mContext);
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_bound_phone;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tv_code = (TextView) findViewById(R.id.tv_code);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        et_phonenum = (ClearEditText) findViewById(R.id.et_phonenum);
        //验证码
        et_proving = (ClearEditText) findViewById(R.id.et_proving);
        tv_code.setOnClickListener(this);
        findViewById(R.id.iv_bound_back).setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_bound_back://返回
                exitTaobao();
                showShortToast("登录失败");
                finish();
                break;
            case R.id.tv_code: //获取验证码
                if (!Utils.isTelNumber(et_phonenum.getText().toString())) {
                    showShortToast("请输入正确的手机号");
                    return;
                }
                if (Utils.isNetworkAvailable(mContext)) {
                    threeCode();
                    PhotoUtil.getCode(tv_code);
                    PhotoUtil.handler.post(t);
                    tv_code.setEnabled(false);
                } else {
                    showShortToast(getString(R.string.net_error1));
                }
                break;
            case R.id.btn_ok: //确定
                if (Utils.isEmpty(et_phonenum)) {
                    showShortToast("请输入手机号");
                    return;
                }
                if (Utils.isEmpty(et_proving)) {
                    showShortToast("请输入验证码");
                    return;
                }
                /*if (Utils.isEmpty(tv_code)) {
                    showShortToast("请输入验证码");
                    return;
                }*/
                if (Utils.isTelNumber(et_phonenum.getText().toString())) {
                    if (Utils.isNetworkAvailable(mContext)) {
                        threeBinding();
                    } else {
                        showShortToast(getString(R.string.net_error1));
                    }
                } else {
                    showShortToast("请输入正确的手机号");
                    return;
                }
                break;
            default:
                break;
        }
    }

    //验证码
    private void threeCode() {
        try {
            Netword.getInstance().getApi(CommenApi.class)
                    .threeSendCode(et_phonenum.getText().toString(), mSession.getAccountNumber(), URLEncoder.encode(sp.getString("photo", ""), "utf-8"))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<String>(this) {

                        @Override
                        public void successed(String result) {
                            showShortToast(result);
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void threeBinding() {
        final SharedPreferences.Editor se = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        Netword.getInstance()
                .getApi(CommenApi.class)
                .threeBinding(et_phonenum.getText().toString(), mSession.getAccountNumber(), et_proving.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<TaobaoLoginBean>(this) {

                    @Override
                    public void successed(TaobaoLoginBean result) {
                        //用户信息
                        //登录状态设为true
                        mSession.setLogin(true);
                        se.putBoolean("isLogin", true);
                        if (result == null)
                            return;
                        //绑定的支付宝号
                        if (result.alipayNumber != null) {
                            mSession.setAlipayNumber(result.alipayNumber);
                            se.putString("alipayNumber", result.alipayNumber);
                        }
                        //用户id
                        if (result.id != null) {
                            mSession.setId(result.id);
                            se.putString("id", result.id);
                        }
                        //是否是代理
                        if (result.isAgencyStatus != 0) {
                            mSession.setIsAgencyStatus(result.isAgencyStatus);
                            se.putInt("isAgencyStatus", result.isAgencyStatus);
                        }
                        //昵称
                        if (result.nickName != null) {
                            mSession.setName(result.nickName);
                            se.putString("nickName", result.nickName);
                        }
                        //用户手机号
                        if (result.phone != null) {
                            mSession.setPhoneNumber(result.phone);
                            se.putString("phone", result.phone);
                        }
                        //头像
                        if (result.photo != null) {
                            mSession.setImge(result.photo);
                            se.putString("photo", result.photo);
                        }
                        //safeToken
                        if (result.safeToken != null) {
                            mSession.setSafeToken(result.safeToken);
                            se.putString("safeToken", result.safeToken);
                        }
                        //淘宝号
                        if (result.taobaoNumber != null) {
                            mSession.setAccountNumber(result.taobaoNumber);
                            se.putString("taobaoNumber", result.taobaoNumber);
                        }
                        se.apply();
                        finish();
                    }
                });
    }

    private void threeWeiXin(){

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return false;
    }

    //退出淘宝
    private void exitTaobao() {
        AlibcLogin alibcLogin = AlibcLogin.getInstance();
        alibcLogin.logout(BoundPhoneActivity.this, new LogoutCallback() {
            @Override
            public void onSuccess() {
                mSession.loginOut();
                sp.edit().clear().apply();
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhotoUtil.closeCode();
    }
}
