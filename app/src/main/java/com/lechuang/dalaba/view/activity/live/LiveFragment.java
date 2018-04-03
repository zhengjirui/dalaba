package com.lechuang.dalaba.view.activity.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.Constants;
import com.lechuang.dalaba.model.bean.LiveProductInfoBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.QUrl;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.activity.SunBigPicActivity;
import com.lechuang.dalaba.view.activity.home.ProductDetailsActivity;
import com.lechuang.dalaba.view.defineView.ProgressWebView;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 方法名：直播碎片
 * 方法描述：
 * 作者：韩雪松 on 2017/8/1 11:30
 * 邮箱：15245605689@163.com
 */

public class LiveFragment extends android.support.v4.app.Fragment {
    private ProgressWebView mWeb;
    private Context mContext = getActivity();
    private View v;
    private boolean isRefulsh = true;    //是否刷新


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_live, container, false);
        //v = View.inflate(BaseApplication.getInstance(), R.layout.fragment_live, null);
        initView();
        initEvent();
        ViewGroup vg = (ViewGroup) this.v.getParent();
        if (null != vg) {
            vg.removeAllViewsInLayout();
        }
        return this.v;
    }

    //初始化view
    private void initView() {
        ((TextView) v.findViewById(R.id.tv_title)).setText("直播间");

    }

    private void initEvent() {
        mWeb = (ProgressWebView) v.findViewById(R.id.wv_live);
        //js调用
        mWeb.getSettings().setJavaScriptEnabled(true);
        //是否储存
        mWeb.getSettings().setDomStorageEnabled(true);
        //缓存大小
        //mWeb.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        //缓存路径
        //String appCachePath = getCacheDir().getAbsolutePath();
        //mWeb.getSettings().setAppCachePath(appCachePath);
        //是否禁止访问文件数据
        mWeb.getSettings().setAllowFileAccess(true);
        mWeb.getSettings().setAppCacheEnabled(true);
        mWeb.getSettings().setUseWideViewPort(true);
        mWeb.getSettings().setLoadWithOverviewMode(true);
        //是否支持缩放
        mWeb.getSettings().setSupportZoom(false);
        mWeb.getSettings().setBuiltInZoomControls(true);
        mWeb.getSettings().setDisplayZoomControls(false);
        mWeb.loadUrl(QUrl.live);
        mWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("appfun:live:")) {
                    isRefulsh = false;
                    // appfun:live:533792247617:1vyg6
                    String produceId = "";
                    String zbjId = "";
                    try {
                        String alipayItemId = url.substring(12);
                        String[] split = alipayItemId.split(":");
                        produceId = split[0];
                        zbjId = split[1];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final String mzbjId = zbjId;
                    Netword.getInstance().getApi(CommenApi.class)
                            .getProductInfo(produceId, "3", zbjId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new ResultBack<LiveProductInfoBean>(getActivity()) {
                                @Override
                                public void successed(LiveProductInfoBean result) {
                                    if(result.productWithBLOBs!=null){
//                                        startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
//                                                .putExtra(Constants.listInfo, JSON.toJSONString(result.productWithBLOBs)));
                                        Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                                        intent.putExtra(Constants.listInfo, JSON.toJSONString(result.productWithBLOBs));
                                        intent.putExtra("t",3);
                                        intent.putExtra("zbjId",mzbjId);
                                        startActivity(intent);
                                    }else{
                                        Utils.show(getActivity(),"商品已过期");
                                    }

                                }
                            });
                    //startActivity(new Intent(getActivity(),LiveProductDetailsActivity.class).putExtra("alipayItemId",alipayItemId));

                } else if (url.startsWith("appfun:zoomImage:")) {
                    //是否刷新
                    isRefulsh = false;
                    Intent intent = new Intent(getActivity(), SunBigPicActivity.class);
                    intent.putExtra("live", 1);
                    intent.putExtra("bigImg", url.substring(17));
                    getActivity().startActivity(intent);
                }
                return true;
            }
        });
        //WebViewUtils.loadUrl(mWeb, getActivity(), QUrl.live);
       /* mWeb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK ) {
                        //这里处理返回键事件
                        if (mWeb.canGoBack()){
                            mWeb.goBack();
                            Toast.makeText(getActivity(), "ok", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }
                }
                return false;
            }
        });*/

    }

    @Override
    public void onPause() {
        super.onPause();
        //点击大图之后不刷新
        if (isRefulsh) {
            initEvent();
        }
        isRefulsh = true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
