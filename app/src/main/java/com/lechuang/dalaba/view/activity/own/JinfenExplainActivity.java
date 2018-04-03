package com.lechuang.dalaba.view.activity.own;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.bean.HelpInfoNewBean;
import com.lechuang.dalaba.model.bean.HeroBean;
import com.lechuang.dalaba.model.bean.KefuInfoBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.presenter.net.netApi.OwnApi;
import com.lechuang.dalaba.view.defineView.ProgressWebView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class JinfenExplainActivity extends AppCompatActivity {

    @BindView(R.id.wv_progress)
    ProgressWebView wvContent;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jinfen_explain);
        ButterKnife.bind(this);
        initView();
        getData();
    }

    private void initView() {
        tvTitle.setText("积分说明");
    }

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }

    }

    public void getData() {
        Netword.getInstance().getApi(CommenApi.class)
                .getHelpInfoNew(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HelpInfoNewBean>(this) {
                    @Override
                    public void successed(HelpInfoNewBean result) {
                        String helpInfo = result.helpInfoNew.helpInfo;
                        webData(helpInfo);
                    }
                });
//
    }

    private void webData(String helpInfo) {
        //记载网页
        WebSettings webSettings = wvContent.getSettings();
        // 让WebView能够执行javaScript

        webSettings.setSupportZoom(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBuiltInZoomControls(true);//support zoom
        //自适应屏幕
        webSettings.setUseWideViewPort(true);// 这个很关键
        webSettings.setLoadWithOverviewMode(true);

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
}
