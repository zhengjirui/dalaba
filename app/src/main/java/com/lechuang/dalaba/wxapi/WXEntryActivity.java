package com.lechuang.dalaba.wxapi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.Constants;
import com.lechuang.dalaba.model.LeCommon;
import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.model.bean.WXAccessTokenBean;
import com.lechuang.dalaba.model.bean.WXInfoBean;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.activity.own.BoundPhoneActivity;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    public LocalSession mSession;
    public SharedPreferences.Editor se;
    private OkHttpClient okHttpClient;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 0) {
                WXInfoBean info = (WXInfoBean) msg.obj;
                mSession.setImge(info.headimgurl);
                mSession.setName(info.nickname);
                mSession.setAccountNumber(info.nickname);
                sendBroadcast(new Intent(LeCommon.WEIXIN));
                startActivity(new Intent(WXEntryActivity.this, BoundPhoneActivity.class));
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
        mSession = LocalSession.get(this);
        se = PreferenceManager.getDefaultSharedPreferences(this).edit();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        // baseresp.getType 1:第三方授权， 2:分享

        String result = "";
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送成功";//发送成功
                String code = ((SendAuth.Resp) baseResp).code;
                getAccessToken(code);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL://发送取消
                result = "发送取消";
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED://发送被拒绝
                result = "发送被拒绝";
                finish();
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = "不支持错误";//不支持错误
                finish();
                break;
            default:
                result = "发送返回";//发送返回
                finish();
                break;
        }
        Utils.show(this, result);
    }

    /**
     * @param code 根据code再去获取AccessToken
     */
    private void getAccessToken(String code) {
        okHttpClient = new OkHttpClient();
        Request request = new Request
                .Builder()
                .get()
                .url("https://api.weixin.qq.com/sns/oauth2/access_token?" +
                        "appid=" + Constants.WX_APP_ID + "&secret=" + Constants.WX_APP_SECRET + "&code=" + code + "&grant_type=authorization_code")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                finish();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String json = response.body().string();
                Gson gson = new Gson();
                WXAccessTokenBean tokenBean = gson.fromJson(json, WXAccessTokenBean.class);
                if (tokenBean.errcode == 0) {
                    getUserInfo(okHttpClient, tokenBean.access_token, tokenBean.openid);
                    return;
                } else if (tokenBean.errmsg != null) {
                    Log.e("---", tokenBean.errmsg);
                    finish();
                }
            }
        });

    }

    private void getUserInfo(OkHttpClient okHttpClient, String access_token, String openid) {
        Request request = new Request
                .Builder()
                .get()
                .url("https://api.weixin.qq.com/sns/userinfo?" +
                        "access_token=" + access_token + "&openid=" + openid)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                finish();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String json = response.body().string();
                Gson gson = new Gson();
                WXInfoBean infoBean = gson.fromJson(json, WXInfoBean.class);
                if (infoBean.errcode == 0) {

                    Message msg = new Message();
                    msg.obj = infoBean;
                    msg.arg1 = 0;
                    handler.sendMessage(msg);
                    return;
                } else if (infoBean.errmsg != null) {
                    Log.e("---", infoBean.errmsg);
                    finish();
                }
            }
        });

    }
}
