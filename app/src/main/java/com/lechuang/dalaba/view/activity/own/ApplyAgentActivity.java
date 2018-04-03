package com.lechuang.dalaba.view.activity.own;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.LeCommon;
import com.lechuang.dalaba.model.bean.OwnBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.OwnApi;
import com.lechuang.dalaba.utils.Utils;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/10/6 15:31
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class ApplyAgentActivity extends AppCompatActivity{

    private ImageView iv_noNet, iv_applyAgent;
    private Context mContext = ApplyAgentActivity.this;
    private ImageView mContentView;
    private boolean mLoadImg = false;
    private double payPriceStr;
    private OwnBean.Agency agency;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_agent);


        initView();
//        WebViewUtils.loadUrl(progressWebView,this,"http://192.168.1.210:8889/user/appUsers/agencyDetail");

        Netword.getInstance().getApi(OwnApi.class).agency() .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnBean>(this) {
                    @Override
                    public void successed(OwnBean result) {
                        agency = result.agency;
                        String img = agency.img;
                        payPriceStr = agency.payPriceStr;
                        Glide.with(ApplyAgentActivity.this).load(img).centerCrop().into(mContentView);
                        mLoadImg = true;
                    }
                });


        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLoadImg)return;
                if(agency.type==1){
                    return;
                }
                if(agency.type==0&&agency.payPrice==0){
                    Netword.getInstance().getApi(OwnApi.class)
                            .autoAgent()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new ResultBack<String>(mContext) {
                                @Override
                                public void successed(String result) {
                                    Utils.show(mContext,result);
                                    sendBroadcast(new Intent(LeCommon.ACTION_APPLY_AGENT_SUCCESS));
                                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
                                    sp.edit().putInt("isAgencyStatus",1).commit();
                                    finish();
                                }
                            });
                    return;
                }
                startActivity(new Intent(ApplyAgentActivity.this,PayStyleActivity.class).putExtra(LeCommon.KEY_PAY_PRICE,payPriceStr));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
//        getData();
//        findViewById(R.id.web_back).setVisibility(View.VISIBLE);
    }

    private void initView() {
        iv_noNet = (ImageView) findViewById(R.id.iv_noNet);
        iv_applyAgent = (ImageView) findViewById(R.id.iv_applyAgent);
        mContentView = (ImageView) findViewById(R.id.content_agency);
    }

    public void getData() {
        // TODO: 2017/10/6
    }
}
