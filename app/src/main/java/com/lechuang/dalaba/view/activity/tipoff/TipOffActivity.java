package com.lechuang.dalaba.view.activity.tipoff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.view.activity.home.SearchActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 作者：li on 2017/9/21 17:46
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class TipOffActivity extends AppCompatActivity {

    @BindView(R.id.tv_cancel)
    ImageView tvCancel;
    @BindView(R.id.tv_good)
    TextView tvGood;
    @BindView(R.id.iv_noNet)
    ImageView ivNoNet;
    @BindView(R.id.tablayout_topoff)
    TabLayout tablayoutTopoff;
    @BindView(R.id.vp_topoff)
    ViewPager vpTopoff;
    Unbinder unbinder;
    //fragments集合
    private List<TipoffBaseFragment> fragments;
//    private List<TipoffBaseFragmentPubu> fragments;
    //标题信息
    public String[] title = new String[]{"最新", "昨天", "两天前", "一周前", "月季度"};
    private MyPaperAdapter myPaperAdapter;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipoff);
        unbinder = ButterKnife.bind(this);
        initView();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
    }


    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fragments = new ArrayList<>();
        int length = title.length;
        //创建页面
        for (int i = 0; i < length; i++) {
            fragments.add((TipoffBaseFragment) setTitle(new TipoffBaseFragment(), title[i], i));
            //fragments.add((TipoffBaseFragmentPubu) setTitle(new TipoffBaseFragmentPubu(), title[i], i));
        }
        //设置适配器
        myPaperAdapter = new MyPaperAdapter(getSupportFragmentManager());
        vpTopoff.setAdapter(myPaperAdapter);
        /*//设置tablout 滑动模式
        tablayoutTopoff.setTabMode(TabLayout.MODE_SCROLLABLE);*/
        //联系tabLayout和viwpager
        tablayoutTopoff.setupWithViewPager(vpTopoff);
        tablayoutTopoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 设置头目
     */
    private Fragment setTitle(Fragment fragment, String title, int i) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("conditionType", i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_good, R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_good:  //点击输入框
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.tv_cancel: //点击删去按钮
                tvGood.setText("");
                changeContent("");

                break;
          /*  case R.id.iv_rili:  //点击签到按钮
                if (sp.getBoolean("isLogin", false) == true){
                    startActivity(new Intent(this, SigneActivity.class));
                }else{
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;*/
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == 1) {                                         //从搜索页返回的内容
            String content = data.getStringExtra("content");
            tvGood.setText(content);
            changeContent(content);
        }
    }

    /**
     * 改变联动fragment的搜索内容Content
     */
    private void changeContent(String content) {
        int size = fragments.size();
        for (int i = 0; i < size; i++) {
            fragments.get(i).content = content;
        }
        int currentItem = vpTopoff.getCurrentItem();
        fragments.get(currentItem).initData();
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
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).getArguments().getString("title");
        }
    }


}
