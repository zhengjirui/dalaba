package com.lechuang.dalaba.view.activity.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ali.auth.third.core.model.Session;
import com.alibaba.baichuan.android.trade.adapter.login.AlibcLogin;
import com.alibaba.baichuan.android.trade.callback.AlibcLoginCallback;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.LeCommon;
import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.model.bean.DataBean;
import com.lechuang.dalaba.model.bean.TaobaoLoginBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.activity.own.BoundPhoneActivity;
import com.lechuang.dalaba.view.defineView.ClearEditText;
import com.lechuang.dalaba.view.dialog.FlippingLoadingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/9/27 14:39
 * 邮箱：961567115@qq.com
 * 修改备注:登录界面
 */
public class LoginAndRegisterActivity extends AppCompatActivity {
    @BindView(R.id.et_phonenum)
    ClearEditText et_phonenum;
    @BindView(R.id.et_pwd)
    ClearEditText et_pwd;
    @BindView(R.id.tv_mima)
    TextView tvMima;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_zhuche)
    TextView btnZhuche;
    @BindView(R.id.tv_taobao)
    TextView tvTaobao;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_sangfang)
    ImageView ll_sanfang;
    public FlippingLoadingDialog mLoadingDialog;
    public LocalSession mSession;
    public SharedPreferences.Editor se;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_register);
        mLoadingDialog = new FlippingLoadingDialog(this, "请求提交中");
        ButterKnife.bind(this);
        mSession = LocalSession.get(this);
        //保存用户登录信息的sp
        se = PreferenceManager.getDefaultSharedPreferences(this).edit();
    }

    @OnClick({R.id.tv_mima, R.id.btn_login, R.id.btn_zhuche, R.id.tv_taobao, R.id.iv_back, R.id.iv_sangfang})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_mima://找回密码
                //type 判断是找回密码还是修改密码    1  找回    2 修改
                startActivity(new Intent(this, FindBackActivity.class).putExtra("type", 1));
                break;
            case R.id.btn_login://登录
                final String userId = et_phonenum.getText().toString();
                //md5加密
                final String pwd = Utils.getMD5(et_pwd.getText().toString().trim());
                //输入框为空 提示用户
                if (Utils.isEmpty(et_phonenum)) {
                    Utils.show(this, "请输入手机号!");
                    return;
                }
                //输入的不是正确的手机号
                if (!Utils.isTelNumber(userId)) {
                    Utils.show(this, "请输入正确的手机号!");
                    return;
                }
                //没有输入密码
                if (Utils.isEmpty(et_pwd)) {
                    Utils.show(this, "请输入密码");
                    return;
                }
                //登录
                normalLogin(userId, pwd);
                break;
            case R.id.btn_zhuche://注册
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;
            case R.id.tv_taobao://淘宝登录
            case R.id.iv_sangfang:
                taobaoLogin();
                break;
            case R.id.iv_back:
                back();
                break;
            default:
                break;
        }
    }

    /**
     * @author yrj
     * @date 2017/10/11
     * @E-mail 1422947831@qq.com
     * @desc 不登录处理
     */
    private void back() {
//        startActivity(new Intent(this, MainActivity.class).putExtra("start", 1));
        //sendBroadcast(new Intent(LeCommon.ACTION_LOGIN_SUCCESS));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
        }
        return false;
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/10/5  17:51
     * @describe 正常登录
     */
    private void normalLogin(String number, String pwd) {
        // TODO: 2017/10/5
        Netword.getInstance().getApi(CommenApi.class)
                .login(number, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<DataBean>(this) {
                    @Override
                    public void successed(DataBean data) {
                        DataBean.UserBean result = data.user;
                        String photo = result.photo;
                        //用户信息
                        //登录状态设为true
                        mSession.setLogin(true);
                        se.putBoolean(LeCommon.KEY_HAS_LOGIN, true);
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
                            se.putInt(LeCommon.KEY_AGENCY_STATUS, result.isAgencyStatus);
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
                               /* se.putString("userId", userId);
                                se.putString("pwd", pwd)*/
                        ;
                        se.commit();
                        //登陆成功
                        Utils.show(LoginAndRegisterActivity.this, "登陆成功");
                        sendBroadcast(new Intent(LeCommon.ACTION_LOGIN_SUCCESS));
                        finish();
                    }
                });
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/10/5  15:44
     * @describe 淘宝的登录
     */
    private void taobaoLogin() {
        showLoadingDialog("");
        final AlibcLogin alibcLogin = AlibcLogin.getInstance();
        alibcLogin.showLogin(this, new AlibcLoginCallback() {

            @Override
            public void onFailure(int i, String s) {

            }

            @Override
            public void onSuccess() {
                Session taobao = alibcLogin.getSession();
                mSession.setLogin(true);
                //获取淘宝头像
                mSession.setImge(taobao.avatarUrl);
                //淘宝昵称
                mSession.setName(taobao.nick);
                mSession.setAccountNumber(taobao.nick);
                se.putBoolean(LeCommon.KEY_HAS_LOGIN, true);
                //se.putString("photo",taobao.avatarUrl);
                threeLogin(mSession.getAccountNumber());
                Utils.show(LoginAndRegisterActivity.this, "登陆成功!");
                dismissLoadingDialog();
            }
        });

    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/10/5  19:24
     * @describe 绑定手机号
     */
    public void threeLogin(String params) {
        Netword.getInstance().getApi(CommenApi.class)
                .threeLogin(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<TaobaoLoginBean>(this) {
                    @Override
                    protected void error300(int errorCode, String s) {
                        if (errorCode == 300) {    //绑定手机号
                            // TODO: 2017/10/5 绑定手机号
                            Utils.show(LoginAndRegisterActivity.this, s);
                            startActivity(new Intent(LoginAndRegisterActivity.this, BoundPhoneActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void successed(TaobaoLoginBean result) {  //代表之前绑定过手机号码
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
                        se.commit();
                        finish();
                    }
                });

    }

    // 显示加载框
    public void showLoadingDialog(String text) {
        if (text != null) {
            mLoadingDialog.setText(text);
        }

        mLoadingDialog.show();
    }

    // 关闭加载框
    public void dismissLoadingDialog() {
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

}
