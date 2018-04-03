package com.lechuang.dalaba.view.activity.get;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseFragment;
import com.lechuang.dalaba.model.LeCommon;
import com.lechuang.dalaba.model.bean.GetBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.GetApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.activity.home.SearchActivity;
import com.lechuang.dalaba.view.defineView.CustomTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/9/21 17:46
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class GetFragment extends BaseFragment {
    @BindView(R.id.tablayout_get)
    TabLayout tablayoutGet;
    @BindView(R.id.vp_get)
    ViewPager vpGet;
    @BindView(R.id.ll_search)
    LinearLayout etTopSearch;
    @BindView(R.id.ll_noNet)
    LinearLayout llNoNet; //没有网络
    @BindView(R.id.iv_tryAgain)
    ImageView tryAgain;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.iv_shuoming)
    ImageView ivShuoming;
    Unbinder unbinder;
    //fragments集合
    private List<Fragment> fragments;
    private static final long DURATION = 500;
    private int lastSelectPos = 0;
    //viewpage标题
    private List<GetBean.TopTab> topTabList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.fragment_get, null, false);
        unbinder = ButterKnife.bind(this, content);
        etTopSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });
        ivShuoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),GetInfoActivity.class));
            }
        });
        getData();
        return content;
    }

    private void getData() {

        showWaitDialog("");
        if (Utils.isNetworkAvailable(getActivity())) {
//            refreshScrollView.setVisibility(View.VISIBLE);
            llContent.setVisibility(View.VISIBLE);
            llNoNet.setVisibility(View.GONE);
            Netword.getInstance().getApi(GetApi.class)
                    .topTabList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<GetBean>(getActivity()) {
                        @Override
                        public void successed(GetBean result) {
                            hideWaitDialog();
                            if (result == null) return;
                            GetBean.TopTab topTab = new GetBean.TopTab();
                            topTab.rootId = -1;
                            topTab.rootName = "精选";
                            topTabList.add(topTab);
                            List<GetBean.TopTab> tabList = result.tbclassTypeList;
                            if (tabList != null) {
                                topTabList.addAll(tabList);
                            }
                            initFragment();
                        }
                    });
        } else {
            hideWaitDialog();
            llNoNet.setVisibility(View.VISIBLE);
            llContent.setVisibility(View.GONE);
        }
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/22  12:22
     * @describe 中间viewPager和fragment联动
     */
    private void initFragment() {
        fragments = new ArrayList<>();
        for (GetBean.TopTab tab : topTabList) {
            fragments.add(setTitle(new CommFragmentImpl(), tab.rootId, tab.rootName));
        }
        //设置适配器
        MyPaperAdapter myPaperAdapter = new MyPaperAdapter(getFragmentManager());
        vpGet.setAdapter(myPaperAdapter);
        //设置tablout 滑动模式
        tablayoutGet.setTabMode(TabLayout.MODE_SCROLLABLE);
        //联系tabLayout和viwpager
        tablayoutGet.setupWithViewPager(vpGet);
        //TabLayout每个条目及下划线的长度是有最长的条目决定的  修改其样式为条目 下划线 适应text的长度
        CustomTabLayout.reflex(tablayoutGet);
    }

    /**
     * 设置头目
     */
    private Fragment setTitle(Fragment fragment, int rootId, String title) {
        Bundle args = new Bundle();
        args.putInt(LeCommon.KEY_ROOT_ID, rootId);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.iv_tryAgain)
    public void onViewClicked() {
        if (Utils.isNetworkAvailable(getActivity())) {
            getData();
        } else {
            showShortToast(getString(R.string.net_error));
        }
    }


    /**
     * 适配器
     */
    private class MyPaperAdapter extends FragmentPagerAdapter {

        public MyPaperAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return topTabList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return topTabList.get(position).rootName;
        }
    }


    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            //TODO
        }
    };

}
