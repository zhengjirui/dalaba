package com.lechuang.dalaba.view.activity.own;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.mobileim.gingko.model.base.MulitImageVO;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.bean.HeroBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.OwnApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.XCRoundImageView;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/10/24 10:21
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class HeroActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.txt_today_rongyubang)
    TextView mTxtToDayRongYuBang;
    @BindView(R.id.txt_sevenday_hero)
    TextView mTxtSevenYuHero;
    @BindView(R.id.txt_user_info)
    TextView mTxtUserInfo;
    @BindView(R.id.txt_jiangli)
    TextView mTxtJiangLi;
    @BindView(R.id.lv_team)
    ListView lvTeam;
    @BindView(R.id.common_nothing_data)
    LinearLayout nothingData;
    @BindView(R.id.refresh_item)
    PullToRefreshScrollView mRefreshItem;


    public List<HeroBean.ListBean> list;
    private int type = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_heroes);
        ButterKnife.bind(this);
        initView();
        getData();
    }

    private void initView() {
        tvTitle.setText("英雄榜");
        mRefreshItem.setOnRefreshListener(refresh);
    }

    @OnClick({R.id.iv_back, R.id.common_nothing_data, R.id.txt_today_rongyubang, R.id.txt_sevenday_hero})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.common_nothing_data:
                getData();
                break;
            case R.id.txt_today_rongyubang:
                //可以用select背景切换
                mTxtToDayRongYuBang.setBackground(getResources().getDrawable(R.drawable.bg_shape_left_yellow));
                mTxtSevenYuHero.setBackground(getResources().getDrawable(R.drawable.bg_shape_right_white));
                mTxtToDayRongYuBang.setTextColor(getResources().getColor(R.color.white));
                mTxtSevenYuHero.setTextColor(getResources().getColor(R.color.c_5D5D5D));
                type = 1;
                getData();
                break;
            case R.id.txt_sevenday_hero:
                //可以用select背景切换
                mTxtSevenYuHero.setBackground(getResources().getDrawable(R.drawable.bg_shape_right_yellow));
                mTxtToDayRongYuBang.setBackground(getResources().getDrawable(R.drawable.bg_shape_left_white));
                mTxtToDayRongYuBang.setTextColor(getResources().getColor(R.color.c_5D5D5D));
                mTxtSevenYuHero.setTextColor(getResources().getColor(R.color.white));
                type = 2;
                getData();
                break;
        }

    }

    public void getData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", type + "");
        Netword.getInstance().getApi(OwnApi.class)
                .heroAgentInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HeroBean>(this) {
                    @Override
                    public void successed(HeroBean result) {
                        list = result.list;
                        //造数据
//                        for (int i = type; i < 20 ;i++){
//                            HeroBean.ListBean listBean = new HeroBean.ListBean();
//                            listBean.nickName = "别名" + i;
//                            listBean.sumIntegral = i * 2;
//                            listBean.sumIntegralStr = i * 4 + "";
//                            listBean.Phone = "18513316431";
//                            list.add(listBean);
//                        }

                        if (null != list && list.toString().equals("[]")) {
                            nothingData.setVisibility(View.VISIBLE);
                        } else {
                            nothingData.setVisibility(View.GONE);
                        }
                        if (list.size() >= 1){
                            //设置英雄榜排名
                            HeroBean.ListBean listBean = list.get(0);
                            list.remove(0);
                            mTxtUserInfo.setText(Utils.hideFirstName(listBean.nickName) + "  " + Utils.hidePhoneRex(listBean.Phone));
                            mTxtJiangLi.setText(listBean.sumIntegralStr + "元");
                            mTxtJiangLi.setVisibility(View.VISIBLE);
                        }else {
                            mTxtUserInfo.setText("暂无");
                            mTxtJiangLi.setVisibility(View.INVISIBLE);
                        }
                        lvTeam.setAdapter(new Myadapter(list));
                    }
                });

    }

    public PullToRefreshBase.OnRefreshListener refresh = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            String label = DateUtils.formatDateTime(
                    getApplicationContext(),
                    System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME
                            | DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_ABBREV_ALL);
            // 显示最后更新的时间
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            getData();
            mRefreshItem.onRefreshComplete();
        }
    };


    public class Myadapter extends BaseAdapter {
        List<HeroBean.ListBean> list;

        public Myadapter(List list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder myViewHolder;
            if (convertView == null) {
                myViewHolder = new MyViewHolder();
                convertView = View.inflate(HeroActivity.this, R.layout.hero_item, null);
                myViewHolder.txtPaiMine = convertView.findViewById(R.id.txt_paiming);
                myViewHolder.txtPhoneNum = convertView.findViewById(R.id.txt_phone_num);
                myViewHolder.txtUserName = convertView.findViewById(R.id.txt_user_name);
                myViewHolder.txtJiangli = convertView.findViewById(R.id.txt_jiangli);
//                myViewHolder.iv = (XCRoundImageView) convertView.findViewById(R.id.iv_img);
//                myViewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//                myViewHolder.iv_design = (ImageView) convertView.findViewById(R.id.iv_design);
//                myViewHolder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
//                myViewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_design);
                convertView.setTag(myViewHolder);
            } else {
                myViewHolder = (MyViewHolder) convertView.getTag();
            }
            HeroBean.ListBean listBean = list.get(position);
//            if (listBean.photo != null && !listBean.photo.equals("")) {
//                Glide.with(HeroActivity.this).load(listBean.photo).error(R.drawable.pic_morentouxiang).into(myViewHolder.iv);  //头像
//            }



            if (position == 0) {
                //设置冠军排行
//                myViewHolder.iv_design.setVisibility(View.VISIBLE);
//                myViewHolder.iv_design.setImageResource(R.drawable.jin);
//                myViewHolder.tv_time.setVisibility(View.GONE);
                Drawable drawable= getResources().getDrawable(R.drawable.ic_hero_two);
                drawable.setBounds(0, -10, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                myViewHolder.txtPaiMine.setCompoundDrawables(null, drawable,null,null);
                myViewHolder.txtPaiMine.setText("");
            } else if (position == 1) {
//                myViewHolder.iv_design.setVisibility(View.VISIBLE);
//                myViewHolder.iv_design.setImageResource(R.drawable.yin);
//                myViewHolder.tv_time.setVisibility(View.GONE);
                Drawable drawable= getResources().getDrawable(R.drawable.ic_hero_three);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                myViewHolder.txtPaiMine.setCompoundDrawables(null, drawable,null,null);
                myViewHolder.txtPaiMine.setText("");
            } else {
//                myViewHolder.iv_design.setVisibility(View.GONE);
//                myViewHolder.tv_time.setVisibility(View.VISIBLE);
//                myViewHolder.tv_time.setText(position + 1 + "");
                myViewHolder.txtPaiMine.setText(position + 2 + "");
            }
            myViewHolder.txtJiangli.setText(listBean.sumIntegralStr + "元");
            myViewHolder.txtUserName.setText(Utils.hideFirstName(listBean.nickName));
            myViewHolder.txtPhoneNum.setText(Utils.hidePhoneRex(listBean.Phone));
            return convertView;
        }
    }

    private class MyViewHolder {
        //        public XCRoundImageView iv;
//        public TextView tv_name;
//        public ImageView iv_design;
//        public TextView tv_money;
//        public TextView tv_time;
        public TextView txtPaiMine;
        public TextView txtPhoneNum;
        public TextView txtUserName;
        public TextView txtJiangli;

    }

}
