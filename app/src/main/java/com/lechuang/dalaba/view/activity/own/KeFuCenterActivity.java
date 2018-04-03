package com.lechuang.dalaba.view.activity.own;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.bean.KefuInfoBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.view.defineView.ProgressWebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/10/17 11:47
 * 邮箱：961567115@qq.com
 * 修改备注: 帮助中心
 */
public class KeFuCenterActivity extends AppCompatActivity {
    @BindView(R.id.wv_progress)
    ProgressWebView wvContent;
    @BindView(R.id.iv_back)
    ImageView ivBack;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infu);
        ButterKnife.bind(this);
        initView();
        getData();
    }

    protected void initView() {
            ((TextView) findViewById(R.id.tv_title)).setText("客服中心");
        findViewById(R.id.iv_back).setVisibility(View.VISIBLE);

    }

    public void getData() {
        Netword.getInstance().getApi(CommenApi.class)
                .getHelpInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<KefuInfoBean>(this) {
                    @Override
                    public void successed(KefuInfoBean result) {
                        String helpInfo = result.HelpInfo.helpInfo;
                        webData(helpInfo);
                    }
                });
    }


    private void webData(String helpInfo) {
        //记载网页
        WebSettings webSettings = wvContent.getSettings();
        // 让WebView能够执行javaScript

        webSettings.setSupportZoom(false);//禁止手势缩放
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBuiltInZoomControls(true);//support zoom
        //自适应屏幕
        webSettings.setUseWideViewPort(true);// 这个很关键
        webSettings.setLoadWithOverviewMode(true);

//        webSettings.setDefaultFontSize(30);
        //加载HTML字符串进行显示
        wvContent.getSettings().setDefaultTextEncodingName("UTF -8");//设置默认为utf-8
        wvContent.loadData(helpInfo, "text/html; charset=UTF-8", null);
        // 设置WebView的客户端
        wvContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;// 返回false
            }
        });
    }
    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
