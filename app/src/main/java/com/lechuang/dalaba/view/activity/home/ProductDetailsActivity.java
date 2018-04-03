package com.lechuang.dalaba.view.activity.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.AlibcTaokeParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.alibaba.fastjson.JSON;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseActivity;
import com.lechuang.dalaba.base.Constants;
import com.lechuang.dalaba.model.DemoTradeCallback;
import com.lechuang.dalaba.model.LeCommon;
import com.lechuang.dalaba.model.bean.AllProductBean;
import com.lechuang.dalaba.model.bean.TaobaoUrlBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.QUrl;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.view.activity.ui.LoginActivity;
import com.lechuang.dalaba.view.defineView.ProgressWebView;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author zhf 2017/08/17
 *         【商品详情】
 */
public class ProductDetailsActivity extends BaseActivity {
    private ProgressWebView mWeb;
    private String productUrl;
    private Context mContext = ProductDetailsActivity.this;

    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private int t;
    private String zbjId;
    //是否是代理
    private int isAgencyStatus;
    //用户id
    private String userId;
    private SharedPreferences sp;
    public ProductDetailsActivity mContent = this;
    private AllProductBean.ListInfo bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_product_details;
    }

    @Override
    protected void initTitle() {
        findViewById(R.id.iv_back).setVisibility(View.GONE);
        findViewById(R.id.iv_back2).setVisibility(View.VISIBLE);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        bean = JSON.parseObject(getIntent().getStringExtra("listInfo"), AllProductBean.ListInfo.class);
        t = getIntent().getIntExtra("t", 1);
        zbjId = getIntent().getStringExtra("zbjId");
        mWeb = (ProgressWebView) findViewById(R.id.web_product);
        ((TextView) findViewById(R.id.tv_title)).setText("商品详情");
//        findViewById(R.id.iv_right).setVisibility(View.VISIBLE);//分享
        //分享
//        findViewById(R.id.iv_right).setOnClickListener(this);

        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        //代理
        isAgencyStatus = sp.getInt("isAgencyStatus", 0);
        //用户id
        userId = sp.getString("id", "");
        if (isAgencyStatus == 1) {
            //代理  a = 1
            loadUrl = QUrl.productDetails + "?i=" + bean.alipayItemId + "&t=" + t + "&a=1" + "&userId=" + userId + "&zbjId="+ zbjId +"&id=" + bean.id;
        } else {
            loadUrl = QUrl.productDetails + "?i=" + bean.alipayItemId + "&t=" + t + "&a=0" + "&userId=" + userId + "&zbjId="+ zbjId +"&id=" + bean.id;
        }
    }

    private String loadUrl;

    @Override
    protected void initData() {
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
        mWeb.loadUrl(loadUrl);
        mWeb.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.equals("appfun:product:detail") && sp.getBoolean(LeCommon.KEY_HAS_LOGIN, false)) {
                    Netword.getInstance().getApi(CommenApi.class)
                            .tb_privilegeUrl(bean.alipayItemId, bean.alipayCouponId, bean.imgs, bean.name)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new ResultBack<TaobaoUrlBean>(mContext) {
                                @Override
                                public void successed(TaobaoUrlBean result) {
                                    productUrl = result.productWithBLOBs.tbPrivilegeUrl;
                                    alibcShowParams = new AlibcShowParams(OpenType.Native, false);
                                    AlibcTaokeParams taoke = new AlibcTaokeParams(Constants.PID, "", "");
                                    exParams = new HashMap<>();
                                    exParams.put("isv_code", "appisvcode");
                                    exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
                                    //Utils.E("url:"+url);
                                    //view.loadUrl(url);

                                    //Utils.E("cccccc:"+getIntent().getStringExtra("alipayItemId"));
                                    //AlibcBasePage alibcBasePage = new AlibcDetailPage(getIntent().getStringExtra("alipayItemId"));
                                    //实例化URL打开page
                                    //AlibcBasePage alibcBasePage = new AlibcPage("https://uland.taobao.com/coupon/edetail?activityId=91cdf70f6a944043b21c9dfca39a889c&pid=mm_116411007_36292444_142176907&itemId=544410512702&src=lc_tczs");
                                    AlibcBasePage alibcBasePage = new AlibcPage(productUrl);
                                    //AlibcTrade.show(ProductDetailsActivity.this, alibcBasePage, alibcShowParams, null, exParams , new DemoTradeCallback());

                                    //添加购物车
                                    //AlibcBasePage alibcBasePage = new AlibcAddCartPage(getIntent().getStringExtra("alipayItemId"));
                                    AlibcTrade.show(ProductDetailsActivity.this, alibcBasePage, alibcShowParams, taoke, exParams, new DemoTradeCallback());

                                }
                            });

                } else if(url.equals("appfun:product:share") && sp.getBoolean(LeCommon.KEY_HAS_LOGIN, false)){
                    startActivity(new Intent(mContext, ProductShareActivity.class).putExtra(Constants.listInfo, bean));
                }else {
                    startActivity(new Intent(ProductDetailsActivity.this, LoginActivity.class));
                }
                return true;

            }
        });

    }

//右侧的点击分享
//    @Override
//    public void onClick(View v) {
//        super.onClick(v);
//        switch (v.getId()) {
//            case R.id.iv_right:
//                if (sp.getBoolean(LeCommon.KEY_HAS_LOGIN, false)) {
//                    startActivity(new Intent(mContext, ProductShareActivity.class).putExtra(Constants.listInfo, bean));
//                } else {
//                    startActivity(new Intent(ProductDetailsActivity.this, LoginActivity.class));
//                }
//                break;
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWeb.destroy();
        //调用了AlibcTrade.show方法的Activity都需要调用AlibcTradeSDK.destory()
        AlibcTradeSDK.destory();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWeb.canGoBack()) {
            mWeb.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //加载webview
        initData();
    }
}
