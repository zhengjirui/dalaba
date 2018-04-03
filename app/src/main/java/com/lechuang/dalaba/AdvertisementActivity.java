package com.lechuang.dalaba;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lechuang.dalaba.base.Constants;
import com.lechuang.dalaba.model.bean.AdvertisementBean;
import com.lechuang.dalaba.model.bean.LiveProductInfoBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.view.activity.home.EmptyWebActivity;
import com.lechuang.dalaba.view.activity.home.ProductDetailsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author yrj
 * @date 2017/10/30
 * @E-mail 1422947831@qq.com
 * @desc 广告图
 */

public class AdvertisementActivity extends AppCompatActivity {

    @BindView(R.id.iv_img)
    ImageView ivImg;
    @BindView(R.id.tv_time)
    TextView tvTime;

    private Context mContext = AdvertisementActivity.this;
    //跳转的url
    private String adUrl;
    //广告跳转类型 0：跳app外web页面 1：跳商品详情
    private int type;
    private AdvertisementBean.AdvertisingImgBean bean;

    public String alipayItemId;
    LiveProductInfoBean.ProductWithBLOBsBean productWithBLOBs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement);
        ButterKnife.bind(this);
        initView();
    }

    //初始化视图
    private void initView() {
        getAdvertisementTime(tvTime);
        handler.post(t);
        Netword.getInstance().getApi(CommenApi.class)
                .advertisementInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<AdvertisementBean>(mContext) {
                    @Override
                    public void successed(AdvertisementBean result) {
                        bean = result.advertisingImg;
                        Glide.with(mContext).load(bean.adImage).into(ivImg);
                        type = bean.type;
                        alipayItemId=bean.alipayItemId;
                        adUrl = bean.adUrl;
                        if(type == 1){
                            Netword.getInstance().getApi(CommenApi.class)
                                    .getProductInfo(alipayItemId, "", "")
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new ResultBack<LiveProductInfoBean>(mContext) {
                                        @Override
                                        public void successed(LiveProductInfoBean result) {
                                            productWithBLOBs = result.productWithBLOBs;
                                        }


                                    });
                        }

                    }
                });
    }

    @OnClick({R.id.iv_img, R.id.tv_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_img:
                if (type == 1) {
                    //handlers.sendEmptyMessage(1);
                    startActivity(new Intent(mContext, MainActivity.class));
                    startActivity(new Intent(mContext, ProductDetailsActivity.class)
                            .putExtra(Constants.listInfo, JSON.toJSONString(productWithBLOBs)));
                } else {
                    startActivity(new Intent(mContext, MainActivity.class));
                    startActivity(new Intent(mContext, EmptyWebActivity.class).putExtra("url", adUrl));
                }
                finish();
                break;
            case R.id.tv_time:
                //跳过广告图
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
                break;
        }
    }


    //广告图时间倒计时
    private static int advertisement_time = Constants.ADVERTISEMENT_TIME;
    /*
    * 倒计时
    * */
    public static Handler handler;
    public static Thread t;

    //广告图倒计时
    public void getAdvertisementTime(final TextView tv) {
        handler = new Handler();
        t = new Thread() {
            @Override
            public void run() {
                super.run();
                advertisement_time--;
                tv.setText(advertisement_time + "s后跳过广告");
                if (advertisement_time <= 0) {
                    startActivity(new Intent(mContext, MainActivity.class));
                    finish();
                    return;
                }
                if (handler != null){
                    handler.postDelayed(this, 1000);
                }
            }
        };
    }

    //广告图倒计时回收
    public void advertisementCloseCode() {
        if (handler != null) {
            handler = null;
            t = null;
            advertisement_time = Constants.ADVERTISEMENT_TIME;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        advertisementCloseCode();
    }
}
