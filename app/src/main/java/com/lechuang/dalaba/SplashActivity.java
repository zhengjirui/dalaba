package com.lechuang.dalaba;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;

import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.model.bean.AdvertisementBean;
import com.lechuang.dalaba.model.bean.LoadingImgBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: guoning
 * Date: 2017/10/10
 * Description:
 */

public class SplashActivity extends AppCompatActivity {
    private Context mContext = this;
    //启动页list
    private List<String> loadingList;
    private SharedPreferences sp;
    private final String isFirstIn = "isFirstIn";
    private Boolean isFirst;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSession = LocalSession.get(SplashActivity.this);
        //设置请求头信息
        setUserInfo();
        isFirst = sp.getBoolean(isFirstIn, true);
        initDate();
        FrameLayout root = (FrameLayout) findViewById(R.id.splash_content);
        ViewPropertyAnimator animate = root.animate();
        animate.setDuration(1500).start();
        animate.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 如果第一次，则进入引导页WelcomeActivity
                        if (isFirst) {
                            sp.edit().putBoolean(isFirstIn, false).commit();
                            loadingList = new ArrayList<String>();
                            getFirstAdverPic();
                        } else if (isLoadAdvertisement) {
                            //广告图
                            startActivity(new Intent(SplashActivity.this, AdvertisementActivity.class));
                            //startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }
                        // 设置Activity的切换效果
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }
                }, 1000);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


    }

    private boolean isLoadAdvertisement = false;

    private void initDate() {
        Netword.getInstance().getApi(CommenApi.class)
                .advertisementInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<AdvertisementBean>(mContext) {
                    @Override
                    public void successed(AdvertisementBean result) {
                        if (result.advertisingImg != null) {
                            isLoadAdvertisement = true;
                        }
                    }
                });
    }

    public void getFirstAdverPic() {
        Netword.getInstance().getApi(CommenApi.class)
                .loadingImg()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<LoadingImgBean>(mContext) {
                    @Override
                    public void successed(LoadingImgBean result) {
                        int size = result.list.size();
                        for (int i = 0; i < size; i++) {
                            loadingList.add(result.list.get(i).startImage);
                        }
                        Intent intent = new Intent(mContext, FirstAdvertActivity.class);
                        intent.putStringArrayListExtra("arraylist", (ArrayList<String>) loadingList);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        finish();
                    }
                });
    }

    private LocalSession mSession;

    //设置用户信息
    private void setUserInfo() {
        mSession.setId(sp.getString("id", ""));
        mSession.setImge(sp.getString("photo", ""));
        mSession.setName(sp.getString("nickName", ""));
        mSession.setPhoneNumber(sp.getString("phone", ""));
        mSession.setAccountNumber(sp.getString("taobaoNumber", ""));
        mSession.setAlipayNumber(sp.getString("alipayNumber", ""));
        mSession.setIsAgencyStatus(sp.getInt("isAgencyStatus", 0));
        mSession.setSafeToken(sp.getString("safeToken", ""));
        mSession.setLogin(sp.getBoolean("isLogin", false));
    }
}
