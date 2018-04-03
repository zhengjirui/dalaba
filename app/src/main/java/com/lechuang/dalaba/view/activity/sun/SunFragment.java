package com.lechuang.dalaba.view.activity.sun;


import android.Manifest;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseFragment;
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
 * 作者：li on 2017/9/21 17:46
 * 邮箱：961567115@qq.com
 * 修改备注: yrj  修改网络请求  无网络状态
 */
public class SunFragment extends BaseFragment {
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

    private View inflate;
    private String name;


    private int page = 1;
    private SharedPreferences sp;

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
                initData();
                hideWaitDialog();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_thesun, container, false);
        unbinder = ButterKnife.bind(this, inflate);
        initView();
        initData();
        getActivity().registerReceiver(receiver, new IntentFilter(LeCommon.ACTION_LOGIN_SUCCESS));
        refreshScrollView.setOnRefreshListener(refresh);
        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (!sp.getBoolean(LeCommon.KEY_HAS_LOGIN, false)) {
            hideWaitDialog();
            showShortToast("请先登陆后查看内容~");
        }
    }

    /* @Override
    public void onResume() {
        super.onResume();
        initData();
    }*/

    private void initView() {
        mSession = LocalSession.get(getActivity());
        TextView send = (TextView) inflate.findViewById(R.id.tv_complete);
        TextView title = (TextView) inflate.findViewById(R.id.tv_title);
        ImageView iv_back = (ImageView) inflate.findViewById(R.id.iv_back);
        //发布的布局
        send.setVisibility(View.VISIBLE);
        send.setText("发布");
        /*int i = UtilsDpAndPx.dip2px(getActivity(), 14);*/
        send.setTextSize(14);
        //标题
        title.setText("晒单广场");
        iv_back.setVisibility(View.GONE);
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
        if (Utils.isNetworkAvailable(getActivity())) {
            llNoNet.setVisibility(View.GONE);
            refreshScrollView.setVisibility(View.VISIBLE);
            Netword.getInstance().getApi(TheSunApi.class)
                    .getSun(page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<SunShowBean>(getActivity()) {
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
                                Utils.show(getActivity(), "亲!已经到底了");
                                refreshScrollView.onRefreshComplete();
                                return;
                            }

                            //初始化晒集合
                            for (int i = 0; i < list.size(); i++) {     //数据加入
                                newsList.add(list.get(i));
                            }
                            //设置数据
                            Glide.with(getActivity()).load(bannerList.get(0).img).into(rv_banner);
                            map = new HashMap<String, ArrayList<String>>();
                            if (page == 1) {
                                //ListView适配器 传入map存储每一条的ListView里面的GridView的数据 Key 就是listview的position
                                theSunAdapter = new TheSunAdapter(newsList, getActivity(), map);
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
                                        startActivity(new Intent(getContext()
                                                , TheSunDetails.class).putExtra("id", newsList.get(position).id));
                                    } else {
                                        startActivity(new Intent(getActivity(), LoginActivity.class));
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
            showShortToast(getString(R.string.net_error));
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
        unbinder.unbind();
    }

    @OnClick({R.id.tv_complete, R.id.iv_tryAgain, R.id.common_nothing_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_complete: //发布
                setCamear();
                if (mSession.isLogin()) {
                    startActivity(new Intent(getActivity(), MyThesunActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
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

   /* //拍照
    private void getPicture() {
        //UUID随机ID数
        name = UUID.randomUUID() + ".jpg";
        //判断内存卡是否存在
        String SDStart = Environment.getExternalStorageState();
        if (SDStart.equals(Environment.MEDIA_MOUNTED)) {
            File file1 = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name));//文件夹是否存在
            if (!file1.exists()) {
                file1.mkdir();
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name) + "/" + name);

            *//*Uri u = Uri.fromFile(file);
            //调用系统照相机
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
            startActivityForResult(intent, RESULT_CODE);*//*
            //android 7.0
            Uri imageUri = FileProvider.getUriForFile(getActivity(), "com.lechuang.youleduo.fileProvider", file);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, RESULT_CODE);
        } else {
            LogUtils.toast(getContext(), "内存卡不存在");
        }
    }*/

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
            images.add(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name) + "/" + name);
            Intent intent = new Intent(getContext(), MyThesunActivity.class);
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
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA); //fragment请求权限
                return;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                    Utils.show(getActivity(), "很遗憾你把相机权限禁用了!");
                    break;
                }
                break;
            case REQUEST_PERMISSION_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //getPicture();

                } else {
                    // Permission Denied
                    Utils.show(getActivity(), "很遗憾你把读取文件权限禁用了!");
                    break;
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    //设置轮播图数据
    private void setBannerListData() {

    }

    private PullToRefreshBase.OnRefreshListener2 refresh = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            String label = DateUtils.formatDateTime(
                    getActivity().getApplicationContext(),
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
                    getActivity().getApplicationContext(),
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

}
