package com.lechuang.dalaba;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lechuang.dalaba.base.Constants;
import com.lechuang.dalaba.base.MyApplication;
import com.lechuang.dalaba.earn.GetMoneyFragment;
import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.model.bean.GetHostUrlBean;
import com.lechuang.dalaba.model.bean.OwnCheckVersionBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.QUrl;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.activity.get.GetFragment;
import com.lechuang.dalaba.view.activity.home.HomeFragment;
import com.lechuang.dalaba.view.activity.home.HomeNoTabFragment;
import com.lechuang.dalaba.view.activity.live.LiveFragment;
import com.lechuang.dalaba.view.activity.own.OwnFragment;
import com.lechuang.dalaba.view.activity.soufanli.SouFanLiFragment;
import com.lechuang.dalaba.view.activity.ui.LoginActivity;
import com.lechuang.dalaba.view.dialog.VersionUpdateDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {


    @BindView(R.id.ll_contest)
    RelativeLayout llContest;
    @BindView(R.id.main_home)
    TextView mainHome;
    @BindView(R.id.main_tipoff)
    TextView mainTipoff;
    @BindView(R.id.main_thesun)
    TextView mainThesun;
    @BindView(R.id.main_own)
    TextView mainOwn;
    @BindView(R.id.content)
    LinearLayout content;
    @BindView(R.id.main_live)
    TextView mainLive;
    //fragment集合
    private List<Fragment> fragments;
    //textview集合
    private List<TextView> views;

    //指定显示的界面
    private int oldIndex = 0;
    private HomeNoTabFragment homeFragment;
    private SouFanLiFragment souFanLiFragment;
    private GetMoneyFragment getFragment;
    private LiveFragment liveFragment;
    private OwnFragment ownFragment;
    private SharedPreferences sp;
    private LocalSession mSession;
    public Context mContext = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Utils.verifyStoragePermissions(this);
        mSession = LocalSession.get(MainActivity.this);
        //读取用户信息
        sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        initView();
        setUserInfo();
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                initFragments();
                return false;
            }
        }).sendEmptyMessageDelayed(0, 500);
        getData();

    }


    /**
     * 检查更新  获取分享商品域名
     */
    public void getData() {

        Netword.getInstance().getApi(CommenApi.class)
                .getShareProductUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<GetHostUrlBean>(mContext) {
                    @Override
                    public void successed(GetHostUrlBean result) {
                        Utils.E(result.show.appHost);
                        sp.edit().putString(Constants.getShareProductHost, result.show.appHost).apply();
                    }
                });

        Netword.getInstance().getApi(CommenApi.class)
                .updataVersion("1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnCheckVersionBean>(mContext) {
                    @Override
                    public void successed(OwnCheckVersionBean result) {
                        // ((TextView) findViewById(R.id.tv_versiondesc)).setText(result.app.versionDescribe);
                        if (!Utils.getAppVersionName(mContext).equals(result.maxApp.versionNumber)) {//版本号
                           /* UpdateVersion version = new UpdateVersion(mContext);
                            version.setDescribe(result.maxApp.versionDescribe);//版本描述
                            if (result.maxApp.downloadUrl != null)
                                version.setUrl(QUrl.url + result.maxApp.downloadUrl);//下载地址
                            version.showUpdateDialog();*/
                            VersionUpdateDialog version = new VersionUpdateDialog(mContext, result.maxApp.versionDescribe);
                            //下载地址
                            if (result.maxApp.downloadUrl != null)
                                version.setUrl(QUrl.url + result.maxApp.downloadUrl);
                            version.show();
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        setUserInfo();
        MobclickAgent.onPause(this);
    }

   /* @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getIntExtra("start", 0) == 1) {
            showCurrentFragment(0);
        }
    }*/

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/21  17:12
     * @describe 初始化应用操作
     */
    private void initView() {
        views = new ArrayList<>();
        views.clear();
        views.add(mainHome);
        views.add(mainLive);
        views.add(mainTipoff);
        views.add(mainThesun);
        views.add(mainOwn);
        views.get(0).setSelected(true);
    }

    //@OnClick({R.id.main_live, R.id.main_home, R.id.main_tipoff, R.id.main_thesun, R.id.main_own})
    @OnClick({R.id.main_home, R.id.main_tipoff, R.id.main_live, R.id.main_thesun, R.id.main_own})
    public void onViewClicked(View view) {
        int current = oldIndex;
        switch (view.getId()) {

            case R.id.main_home:
                current = 0;
                break;
            case R.id.main_live:
                current = 1;
                break;
            case R.id.main_tipoff:
                current = 2;
                break;
            case R.id.main_thesun:
                current = 3;
                break;
            case R.id.main_own:
                if (!sp.getBoolean("isLogin", false)) {
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    current = 4;
                }
                break;
        }
        showCurrentFragment(current);
    }

    /**
     * 初始化用到的Fragment
     */
    private void initFragments() {
        homeFragment = new HomeNoTabFragment();
        souFanLiFragment = new SouFanLiFragment();
        getFragment = new GetMoneyFragment();
        liveFragment = new LiveFragment();
        ownFragment = new OwnFragment();
        fragments = new ArrayList<>();
        fragments.clear();
        fragments.add(homeFragment);
        fragments.add(souFanLiFragment);
        fragments.add(getFragment);
        fragments.add(liveFragment);
        fragments.add(ownFragment);
//        默认加载前两个Fragment，其中第一个展示，第二个隐藏
        getSupportFragmentManager().beginTransaction()
                .add(R.id.ll_contest, homeFragment)
//                .add(R.id.ll_contest, tipOffFragment)
//                .hide(tipOffFragment)
                .show(homeFragment)
                .commit();
//        showCurrentFragment(0);

    }

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

    /**
     * 展示当前选中的Fragment
     *
     * @param currentIndex
     */
    public void showCurrentFragment(int currentIndex) {
        if (currentIndex != oldIndex) {
            views.get(oldIndex).setSelected(false);
            views.get(currentIndex).setSelected(true);
            FragmentTransaction ft = getSupportFragmentManager()
                    .beginTransaction();
            ft.hide(fragments.get(oldIndex));
            if (!fragments.get(currentIndex).isAdded()) {
                ft.add(R.id.ll_contest, fragments.get(currentIndex));
            }
            ft.show(fragments.get(currentIndex)).commit();
            oldIndex = currentIndex;
        }
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    /**
     * 双击返回
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {

                @Override
                public void run() {

                    isExit = false;
                }
            }, 2000);
        } else {
            MyApplication.getInstance().exit();
            Process.killProcess(Process.myPid());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return false;
    }


}
