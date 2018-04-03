package com.lechuang.dalaba.view.activity.own;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.Extra;
import com.lechuang.dalaba.model.bean.TeamNextBean;
import com.lechuang.dalaba.presenter.CommonAdapter;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.OwnApi;
import com.lechuang.dalaba.view.defineView.MListView;
import com.lechuang.dalaba.view.defineView.ViewHolder;
import com.lechuang.dalaba.view.defineView.XCRoundImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MySecondTeamActivity extends AppCompatActivity {

    @BindView(R.id.sv_team_refresh)
    PullToRefreshScrollView refreshItem;
    @BindView(R.id.iv_headImg)
    XCRoundImageView ivHead;
    @BindView(R.id.tv_team_nickname)
    TextView tvNickName;
    @BindView(R.id.tv_team_contribute)
    TextView tvContribute;
    @BindView(R.id.lv_team)
    MListView lvTeam;

    public int page = 1;
    private String mUserId;
    private Context mContext = MySecondTeamActivity.this;
    private CommonAdapter<TeamNextBean.TeamNext.TeamMember> mAdapter;
    private List<TeamNextBean.TeamNext.TeamMember> items = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_second_team);
        ButterKnife.bind(this);

        initView();
        getData();
    }

    private void initView() {
        mUserId = getIntent().getStringExtra(Extra.USER_ID);
        refreshItem.setOnRefreshListener(refresh);
        refreshItem.setMode(PullToRefreshBase.Mode.BOTH);
        refreshItem.onRefreshComplete();
    }

    @OnClick({R.id.iv_team_back})
    public void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.iv_team_back:
                finish();
                break;
        }
    }

    private void getData() {
        Netword.getInstance().getApi(OwnApi.class)
                .nextTeam(mUserId, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<TeamNextBean>(mContext) {
                    @Override
                    public void successed(TeamNextBean result) {
                        Log.i(mUserId+"Result_successed", JSON.toJSONString(result));
                        if (result == null)
                            return;
                        TeamNextBean.TeamNext mData = result.record;
                        tvContribute.setText(mData.contributionSum);
                        tvNickName.setText(mData.nickname);
                        Glide.with(mContext).load(mData.photo).error(R.drawable.wode_touxiang).into(ivHead);
                        List<TeamNextBean.TeamNext.TeamMember> mList = mData.list;
                        refreshItem.setMode(mList.size() > 0 ? PullToRefreshBase.Mode.BOTH : PullToRefreshBase.Mode.PULL_FROM_START);
                        if (items.size() > 0 && mList.toString().equals("[]")) {
                            Toast.makeText(mContext, "亲!已经到底了", Toast.LENGTH_SHORT).show();
                            refreshItem.onRefreshComplete();
                            return;
                        }
                        int size = mList.size();
                        for (int i = 0; i < size; i++) {
                            items.add(mList.get(i));
                        }
                        refreshItem.onRefreshComplete();

                        if (1 == page) {
                            mAdapter = new CommonAdapter<TeamNextBean.TeamNext.TeamMember>(mContext, items, R.layout.item_next_team) {
                                @Override
                                public void setData(ViewHolder viewHolder, Object item) {
                                   
                                }

                                @Override
                                public void setData2(ViewHolder viewHolder, Object item, int position) {
                                    if(position%2==0){
                                        LinearLayout view = viewHolder.getView(R.id.ll_content);
                                        view.setSelected(true);
                                    }
                                    final TeamNextBean.TeamNext.TeamMember mItem = (TeamNextBean.TeamNext.TeamMember) item;
                                    viewHolder.displayImage(R.id.iv_touxiang,mItem.photo);
                                    // 昵称
                                    viewHolder.setText(R.id.tv_team_nickname, mItem.nickname);
                                    // 加入时间
                                    viewHolder.setText(R.id.tv_team_contribute, mItem.joinTime);
                                }
                            };
                            lvTeam.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
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
            if (null != items) {
                items.clear();
            }
            //refreshScrollView.getRefreshableView().smoothScrollTo(0, 0);
            // 模拟加载任务
            getData();
            refreshView.onRefreshComplete();
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
            getData();
            refreshItem.onRefreshComplete();
        }
    };
}
