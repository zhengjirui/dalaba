package com.lechuang.dalaba.view.activity.own;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.AlibcTaokeParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcMyOrdersPage;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.Constants;
import com.lechuang.dalaba.model.DemoTradeCallback;
import com.lechuang.dalaba.view.defineView.ProgressWebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：li on 2017/11/22 10:40
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class WebViewActivity extends AppCompatActivity {
    @BindView(R.id.wv_noVisibly)
    ProgressWebView  pWb;

    public ArrayList<String> item = new ArrayList<>();
    //打开页面的方法
    private AlibcShowParams alibcShowParams = new AlibcShowParams(OpenType.H5, false);
    private Map exParams = new HashMap<>();

    public WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:window.handler.show(document.body.innerHTML);");
            super.onPageFinished(view, url);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        pWb.getSettings().setJavaScriptEnabled(true);
        //添加接口
        pWb.addJavascriptInterface(new Handlers(), "handler");
        AlibcBasePage alibcBasePage = new AlibcMyOrdersPage(0, true);
        AlibcTaokeParams taokeParams = new AlibcTaokeParams(Constants.PID, "", "");
        WebChromeClient webChromeClient = new WebChromeClient();
        pWb.setWebViewClient(webViewClient);
        pWb.setWebChromeClient(webChromeClient);
        AlibcTrade.show(this, pWb, webViewClient, webChromeClient, alibcBasePage, alibcShowParams, taokeParams, exParams, new DemoTradeCallback());
    }

    public class Handlers {
        @JavascriptInterface
        public String show(String data) {
            String regexd = "<div class=\"module (\\d{16,17})\\d* status[^>]*>.*?<p class=\"h\">([^<]*)</p>";
            Pattern p = Pattern.compile(regexd);
            Matcher m = p.matcher(data);
            if (item != null) {
                item.clear();
            }
            while (m.find()) {
                String group = m.group(1);
               /* String group1 = m.group(2);*/
                item.add(group);
            }
            new AlertDialog.Builder(WebViewActivity.this).setMessage(data).create().show();
            setResult(2,new Intent().putExtra("items",item));
           finish();
            return null;
        }
    }
}
