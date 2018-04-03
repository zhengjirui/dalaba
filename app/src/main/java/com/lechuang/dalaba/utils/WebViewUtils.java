package com.lechuang.dalaba.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ali.auth.third.core.model.Session;
import com.alibaba.baichuan.android.trade.adapter.login.AlibcLogin;
import com.alibaba.baichuan.android.trade.callback.AlibcLoginCallback;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.bean.UpdataInfoBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.view.activity.MyThesunActivity;
import com.lechuang.dalaba.view.activity.SigneActivity;
import com.lechuang.dalaba.view.activity.own.ChangeUserNameActivity;
import com.lechuang.dalaba.view.activity.own.CheckIdentityActivity;
import com.lechuang.dalaba.view.activity.own.HelpCenterActivity;
import com.lechuang.dalaba.view.activity.own.UserCenterActivity;
import com.lechuang.dalaba.view.defineView.ProgressWebView;
import com.lechuang.dalaba.view.dialog.JiFenShuoMingDialog;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhf on 2017/8/19.
 */

public class WebViewUtils {

    public static void loadUrl(ProgressWebView mWeb, final Context context, final String url) {

        //js调用
        mWeb.getSettings().setJavaScriptEnabled(true);
        //是否储存
        mWeb.getSettings().setDomStorageEnabled(false);
        //缓存大小
        //mWeb.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        //缓存路径
        //String appCachePath = context.getCacheDir().getAbsolutePath();
        //mWeb.getSettings().setAppCachePath(appCachePath);
        //是否禁止访问文件数据
        mWeb.getSettings().setAllowFileAccess(true);
        mWeb.getSettings().setAppCacheEnabled(false);
        mWeb.getSettings().setUseWideViewPort(true);
        mWeb.getSettings().setLoadWithOverviewMode(true);
        //是否支持缩放
        mWeb.getSettings().setSupportZoom(false);
        mWeb.getSettings().setBuiltInZoomControls(true);
        mWeb.getSettings().setDisplayZoomControls(false);
        mWeb.loadUrl(url);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        mWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                if (url.equals("appfun:sign:reward")) {
                    //跳到签到奖励
                    context.startActivity(new Intent(context, SigneActivity.class));
                } else if (url.equals("appfun:bask:reward")) {
                    //跳到晒单界面
                    context.startActivity(new Intent(context, MyThesunActivity.class));
                } else if (url.equals("appfun:upload:avatar")) {
                    //上传头像
                    context.startActivity(new Intent(context, UserCenterActivity.class));
                } else if (url.equals("appfun:complete:nick")) {
                    //完善昵称
                    context.startActivity(new Intent(context, ChangeUserNameActivity.class));
                } else if (url.equals("appfun:bind:ALI")) {
                    //绑定支付宝
                    context.startActivity(new Intent(context, CheckIdentityActivity.class));
                } else if (url.equals("appfun:bind:taobao")) {
                    //绑定淘宝账号

                    //绑定淘宝账号
//                    context.startActivity(new Intent(context, UserCenterActivity.class));

                    // TODO: 2017/9/25 三方淘宝的接口
                    if (Utils.isNetworkAvailable(context)) {
                        final AlibcLogin alibcLogin = AlibcLogin.getInstance();
                        alibcLogin.showLogin((Activity) context,new AlibcLoginCallback() {
                            @Override
                            public void onSuccess() {
                                final Session taobao = alibcLogin.getSession();
                                //mSession.setLogin(true);
                                //mSession.setImge(taobao.avatarUrl);
                                //mSession.setName(taobao.nick);
                                //mSession.setAccountNumber(taobao.nick);
                                Map<String,String> map=new HashMap<>();
                                map.put("taobaoNumber",taobao.nick);
                                Netword.getInstance().getApi(CommenApi.class)
                                        .updataInfo(map)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new ResultBack<UpdataInfoBean>(context) {
                                            @Override
                                            public void successed(UpdataInfoBean result) {
                                                sp.edit().putString("taobaoNumber",taobao.nick).commit();
                                            }
                                        });
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Utils.show(context,s);

                            }
                        });
                    } else {
                        Utils.show(context,context.getString(R.string.net_error1));
                    }
                }else if (url.equals("app:jump:helpcenter")) {
                    //任务中心积分说明
//                    context.startActivity(new Intent(context, HelpCenterActivity.class));

                    new JiFenShuoMingDialog(context).show();
                }/*else if(url.startsWith("appfun:zoomImage:")){
                    Intent intent = new Intent(context, SunBigPicActivity.class);
                    intent.putExtra("live",1);
                    intent.putExtra("bigImg",url.substring(17));
                    context.startActivity(intent);
                }*/else if (url.startsWith("http")) {
                    view.loadUrl(url);
                }
                return true;
            }
        });
    }

}
