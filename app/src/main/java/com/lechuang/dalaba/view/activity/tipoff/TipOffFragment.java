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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.view.activity.SigneActivity;
import com.lechuang.dalaba.view.activity.home.SearchActivity;
import com.lechuang.dalaba.view.activity.ui.LoginActivity;

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
public class TipOffFragment extends Fragment {

    @BindView(R.id.tv_cancel)
    ImageView tvCancel;
    @BindView(R.id.tv_good)
    TextView tvGood;
    @BindView(R.id.iv_rili)
    ImageView ivRili;
    @BindView(R.id.iv_noNet)
    ImageView ivNoNet;
    @BindView(R.id.tablayout_topoff)
    TabLayout tablayoutTopoff;
    @BindView(R.id.vp_topoff)
    ViewPager vpTopoff;
    Unbinder unbinder;
    //fragments集合
    private List<TipoffBaseFragment> fragments;
    //标题信息
    public String[] title = new String[]{"最新", "昨天", "两天前", "一周前", "月季度"};
    private MyPaperAdapter myPaperAdapter;
    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_tipoff, container, false);
        unbinder = ButterKnife.bind(this, inflate);
        initFragment();
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return inflate;
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/22  12:22
     * @describe 中间viewPager和fragment联动
     */
    private void initFragment() {
        fragments = new ArrayList<>();
        int length = title.length;
        //创建页面
        for (int i = 0; i < length; i++) {
            fragments.add((TipoffBaseFragment) setTitle(new TipoffBaseFragment(), title[i],i));
        }
        //设置适配器
        myPaperAdapter = new MyPaperAdapter(getFragmentManager());
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
    private Fragment setTitle(Fragment fragment, String title,int i) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("conditionType",i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_good, R.id.tv_cancel, R.id.iv_rili})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_good:  //点击输入框
                startActivityForResult(new Intent(getActivity(), SearchActivity.class).putExtra("whereSearch", 0), 0);
                break;
            case R.id.tv_cancel: //点击删去按钮
                tvGood.setText("");
               changeContent("");

                break;
            case R.id.iv_rili:  //点击签到按钮
                if (sp.getBoolean("isLogin", false) == true){
                    startActivity(new Intent(getActivity(), SigneActivity.class));
                }else{
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if(requestCode==0&&resultCode==1){                                         //从搜索页返回的内容
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
            fragments.get(i).content=content;
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
