package com.lechuang.dalaba.view.activity.sun;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.LeCommon;
import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.model.bean.SunShowBean;
import com.lechuang.dalaba.presenter.adapter.TheSunAdapter;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.TheSunApi;
import com.lechuang.dalaba.utils.ImageSelectorUtils;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.activity.MyThesunActivity;
import com.lechuang.dalaba.view.activity.ui.LoginActivity;
import com.lechuang.dalaba.view.defineView.MListView;
import com.lechuang.dalaba.view.dialog.FlippingLoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/10/23 16:05
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class SunActivity extends Activity {
    //打开照相机的请求码
    private static final int RESULT_CODE = 100;
    private static final int REQUEST_PERMISSION_CAMERA = 200;
    private static final int REQUEST_PERMISSION_STORAGE = 300;
    @BindView(R.id.mlv_theSun)
    MListView mlvTheSun;
    @BindView(R.id.sun_refresh)
    PullToRefreshScrollView refreshScrollView;
    @BindView(R.id.rv_banner)
    ImageView rv_banner;      //轮播图插件
    Unbinder unbinder;
    @BindView(R.id.iv_tryAgain)
    ImageView ivTryAgain;
    @BindView(R.id.ll_noNet)
    LinearLayout llNoNet;
    @BindView(R.id.common_nothing_data)
    LinearLayout nothingData;

    private boolean _isVisible = true;
    private FlippingLoadingDialog waitDialog;

    private int page = 1;
    private SharedPreferences sp;
    private Context mContext = this;

    //晒单内容的集合
    List<SunShowBean.ListBean> newsList = new ArrayList<>();
    private Map map;
    private TheSunAdapter theSunAdapter;
    private LocalSession mSession;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (LeCommon.ACTION_LOGIN_SUCCESS.equals(action)) {
                initView();
                initData();
                hideWaitDialog();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_thesun);
       unbinder = ButterKnife.bind(this);
        initView();
        initData();
        registerReceiver(receiver, new IntentFilter(LeCommon.ACTION_LOGIN_SUCCESS));
        refreshScrollView.setOnRefreshListener(refresh);
    }


    @Override
    public void onStart() {
        super.onStart();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sp.getBoolean(LeCommon.KEY_HAS_LOGIN, false)) {
            hideWaitDialog();
            Utils.show(this, "请先登陆后查看内容~");
        }
    }

    private void initView() {
        mSession = LocalSession.get(this);
        TextView send = (TextView) findViewById(R.id.tv_complete);
        ((TextView) findViewById(R.id.tv_title)).setText("晒单广场");
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //发布的布局
        send.setVisibility(View.VISIBLE);
        send.setText("发布");
        send.setTextColor(getResources().getColor(R.color.rgb_ff6b80));
        /*int i = UtilsDpAndPx.dip2px(getActivity(), 14);*/
        send.setTextSize(14);
        //获取焦点  必须先获取焦点才能在顶部  另外内部的listview gridView不能有焦点
        refreshScrollView.setFocusable(true);
        refreshScrollView.setFocusableInTouchMode(true);
        refreshScrollView.requestFocus();
        refreshScrollView.getRefreshableView().scrollTo(0, 0);
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/29  20:11
     * @describe 访问网络
     */
    public void initData() {

        showWaitDialog("");
        if (Utils.isNetworkAvailable(this)) {
            llNoNet.setVisibility(View.GONE);
            refreshScrollView.setVisibility(View.VISIBLE);
            Netword.getInstance().getApi(TheSunApi.class)
                    .getSun(page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<SunShowBean>(mContext) {
                        @Override
                        public void successed(SunShowBean result) {
                            if (result == null) {
                                nothingData.setVisibility(View.VISIBLE);
                                if (theSunAdapter != null)
                                    theSunAdapter.notifyDataSetChanged();
                                refreshScrollView.onRefreshComplete();
                                return;
                            }
                            //轮播图数据
                            List<SunShowBean.BannerListBean> bannerList = result.bannerList;
                            //晒单数据集合
                            List<SunShowBean.ListBean> list = result.list;
                            refreshScrollView.setMode(list.size() > 0 ? PullToRefreshBase.Mode.BOTH : PullToRefreshBase.Mode.PULL_FROM_START);
                            if (page == 1 && (list.toString().equals("[]") || list.size() <= 0)) {
                                nothingData.setVisibility(View.VISIBLE);
                                if (theSunAdapter != null)
                                    theSunAdapter.notifyDataSetChanged();
                                refreshScrollView.onRefreshComplete();
                                return;
                            }
                            nothingData.setVisibility(View.GONE);
                            if (page != 1 && list.toString().equals("[]")) {            //数据没有了
                                Utils.show(mContext, "亲!已经到底了");
                                hideWaitDialog();
                                refreshScrollView.onRefreshComplete();
                                return;
                            }


                            //初始化晒集合
                            for (int i = 0; i < list.size(); i++) {     //数据加入
                                newsList.add(list.get(i));
                            }
                            //设置数据
                            Glide.with(mContext).load(bannerList.get(0).img).into(rv_banner);
                            map = new HashMap<String, ArrayList<String>>();
                            if (page == 1) {
                                //ListView适配器 传入map存储每一条的ListView里面的GridView的数据 Key 就是listview的position
                                theSunAdapter = new TheSunAdapter(newsList, mContext, map);
                                mlvTheSun.setAdapter(theSunAdapter);
                            } else {
                                theSunAdapter.notifyDataSetChanged();
                            }
                            hideWaitDialog();
                            refreshScrollView.onRefreshComplete();
                            //点击详情
                            mlvTheSun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    if (sp.getBoolean(LeCommon.KEY_HAS_LOGIN, false)) {
                                        startActivity(new Intent(mContext
                                                , TheSunDetails.class).putExtra("id", newsList.get(position).id));
                                    } else {
                                        startActivity(new Intent(mContext, LoginActivity.class));
                                    }

                                }
                            });
                        }

                        @Override
                        public void onCompleted() {
                            super.onCompleted();
                            hideWaitDialog();
                        }
                    });
        } else {
            //隐藏加载框
            hideWaitDialog();
            llNoNet.setVisibility(View.VISIBLE);
            refreshScrollView.setVisibility(View.GONE);
            Utils.show(mContext, getString(R.string.net_error));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.tv_complete, R.id.iv_tryAgain, R.id.common_nothing_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_complete: //发布
                setCamear();
                if (mSession.isLogin()) {
                    startActivity(new Intent(mContext, MyThesunActivity.class));
                } else {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
                break;
            case R.id.common_nothing_data:
            case R.id.iv_tryAgain:
                //刷新重试
                page = 1;
                if (null != newsList) {
                    newsList.clear();
                }

                // 模拟加载任务
                initData();
                break;
            default:
                break;
        }
    }


    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/23  15:03
     * @describe 处理从照相机返回的信息
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CODE) {
            ArrayList<String> images = new ArrayList<>();
            images.add(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name) + "/");
            Intent intent = new Intent(mContext, MyThesunActivity.class);
            intent.putStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT, images);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/23  19:06
     * @describe 检查，申请权限
     */
    private void setCamear() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA); //fragment请求权限
                return;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            //就像onActivityResult一样这个地方就是判断你是从哪来的。
            case REQUEST_PERMISSION_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // Permission Denied
                    Utils.show(mContext, "很遗憾你把相机权限禁用了!");
                    break;
                }
                break;
            case REQUEST_PERMISSION_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //getPicture();

                } else {
                    // Permission Denied
                    Utils.show(mContext, "很遗憾你把读取文件权限禁用了!");
                    break;
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private PullToRefreshBase.OnRefreshListener2 refresh = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            String label = DateUtils.formatDateTime(
                    mContext.getApplicationContext(),
                    System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME
                            | DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_ABBREV_ALL);
            // 显示最后更新的时间
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            page = 1;
            if (null != newsList) {
                newsList.clear();
            }
            // 模拟加载任务
            initData();
            refreshScrollView.onRefreshComplete();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            String label = DateUtils.formatDateTime(
                    mContext.getApplicationContext(),
                    System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME
                            | DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_ABBREV_ALL);
            // 显示最后更新的时间
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            page += 1;
            // 模拟加载任务
            initData();
            refreshScrollView.onRefreshComplete();
        }
    };


    public FlippingLoadingDialog showWaitDialog(String message) {
        if (_isVisible) {
            if (waitDialog == null) {
                waitDialog = new FlippingLoadingDialog(mContext, message);
            }
            if (waitDialog != null) {
                waitDialog.setText(message);
                waitDialog.show();
            }
            return waitDialog;
        }
        return null;
    }

    public void hideWaitDialog() {
        if (_isVisible && waitDialog != null) {
            try {
                if (waitDialog.isShowing())
                    waitDialog.dismiss();
                waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
