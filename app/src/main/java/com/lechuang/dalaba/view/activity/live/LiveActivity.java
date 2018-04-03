package com.lechuang.dalaba.view.activity.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
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

import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/10/19 16:36
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class LiveActivity extends AppCompatActivity {
    private ProgressWebView mWeb;
    private Context mContext = this;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private boolean isRefulsh = true;    //是否刷新

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        //v = View.inflate(BaseApplication.getInstance(), R.layout.fragment_live, null);
        initView();
        initEvent();
    }

    //初始化view
    private void initView() {
        /*findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
        ((TextView) findViewById(R.id.tv_title)).setText("精彩直播");

    }

    private void initEvent() {
        mWeb = (ProgressWebView) findViewById(R.id.wv_live);
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
                            .subscribe(new ResultBack<LiveProductInfoBean>(mContext) {
                                @Override
                                public void successed(LiveProductInfoBean result) {
                                    if(result.productWithBLOBs!=null){
//                                        startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
//                                                .putExtra(Constants.listInfo, JSON.toJSONString(result.productWithBLOBs)));
                                        Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                                        intent.putExtra(Constants.listInfo, JSON.toJSONString(result.productWithBLOBs));
                                        intent.putExtra("t",3);
                                        intent.putExtra("zbjId",mzbjId);
                                        startActivity(intent);
                                    }else{
                                        Utils.show(mContext,"商品已过期");
                                    }

                                }
                            });
                } else if (url.startsWith("appfun:zoomImage:")) {
                    //是否刷新
                    isRefulsh = false;
                    Intent intent = new Intent(mContext, SunBigPicActivity.class);
                    intent.putExtra("live", 1);
                    intent.putExtra("bigImg", url.substring(17));
                    mContext.startActivity(intent);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWeb.destroy();
        //调用了AlibcTrade.show方法的Activity都需要调用AlibcTradeSDK.destory()
        AlibcTradeSDK.destory();
    }
}
