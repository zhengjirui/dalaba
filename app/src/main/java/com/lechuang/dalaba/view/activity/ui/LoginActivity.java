package com.lechuang.dalaba.view.activity.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ali.auth.third.core.model.Session;
import com.alibaba.baichuan.android.trade.adapter.login.AlibcLogin;
import com.alibaba.baichuan.android.trade.callback.AlibcLoginCallback;
import com.google.gson.Gson;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.Constants;
import com.lechuang.dalaba.base.MyApplication;
import com.lechuang.dalaba.model.LeCommon;
import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.model.bean.DataBean;
import com.lechuang.dalaba.model.bean.QQInfoBean;
import com.lechuang.dalaba.model.bean.QQTokenBean;
import com.lechuang.dalaba.model.bean.TaobaoLoginBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.utils.PhotoUtil;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.activity.own.BoundPhoneActivity;
import com.lechuang.dalaba.view.dialog.FlippingLoadingDialog;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/9/27 14:39
 * 邮箱：961567115@qq.com
 * 修改备注:登录界面
 */
public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.et_phonenum)
    EditText et_phonenum;
    @BindView(R.id.et_pwd)
    EditText et_pwd;
    @BindView(R.id.tv_yanzhengma)
    TextView tvYanzhengma;
    @BindView(R.id.tv_mima)
    TextView tvMima;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_zhuche)
    TextView btnZhuche;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    public FlippingLoadingDialog mLoadingDialog;
    public LocalSession mSession;
    public SharedPreferences.Editor se;
    private Tencent mTencent;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoadingDialog = new FlippingLoadingDialog(this, "请求提交中");
        ButterKnife.bind(this);
        mSession = LocalSession.get(this);
        //保存用户登录信息的sp
        se = PreferenceManager.getDefaultSharedPreferences(this).edit();
        registerReceiver(receiver, new IntentFilter(LeCommon.WEIXIN));
    }

    @OnClick({R.id.tv_yanzhengma, R.id.tv_mima, R.id.btn_login, R.id.btn_zhuche, R.id.tv_taobao,
            R.id.tv_weixin, R.id.tv_qq, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_yanzhengma://验证码登录
                // startActivity(new Intent(this, LoginYanActivity.class));
                break;
            case R.id.tv_mima://找回密码
                //type 判断是找回密码还是修改密码    1  找回    2 修改
                startActivity(new Intent(this, FindBackActivity.class));
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
                taobaoLogin();
                break;
            case R.id.tv_weixin://微信登录
                Utils.show(this,"微信登录正在开发中！");
//                weiXinLogin();
                break;
            case R.id.tv_qq://QQ登录
                Utils.show(this,"QQ登录正在开发中！");
//                qqLogin();
                break;
            case R.id.iv_back:
                back();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhotoUtil.closeCode();
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
                        Utils.show(LoginActivity.this, "登陆成功");
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
                Utils.show(LoginActivity.this, "登陆成功!");
                dismissLoadingDialog();
            }
        });

    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/10/5  15:44
     * @describe 微信的登录
     */

    private void weiXinLogin() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, false);
        api.registerApp(Constants.WX_APP_ID);

        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_sdfatest";
        api.sendReq(req);
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/10/5  15:44
     * @describe QQ的登录
     */
    private void qqLogin() {

        //QQ登录
        mTencent = Tencent.createInstance(Constants.QQ_APP_ID, MyApplication.getInstance());
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "get_user_info", new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    Log.e("----",o.toString());
                    Gson gson = new Gson();
                    QQTokenBean tokenBean = gson.fromJson(o.toString(),QQTokenBean.class);
                    getQQUserInfo(tokenBean.access_token, Constants.QQ_APP_ID, tokenBean.openid);
                }

                @Override
                public void onError(UiError uiError) {

                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    private void getQQUserInfo(String access_token, String oauth_consumer_key, String openid) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request
                .Builder()
                .get()
                .url("https://graph.qq.com/user/get_user_info?" +
                        "access_token=" + access_token + "&oauth_consumer_key=" + oauth_consumer_key + "&openid=" + openid)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                finish();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.e("---", json);
                Gson gson = new Gson();
                QQInfoBean infoBean = gson.fromJson(json, QQInfoBean.class);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, new IUiListener() {
            @Override
            public void onComplete(Object o) {

            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

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
                            Utils.show(LoginActivity.this, s);
                            startActivity(new Intent(LoginActivity.this, BoundPhoneActivity.class));
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
