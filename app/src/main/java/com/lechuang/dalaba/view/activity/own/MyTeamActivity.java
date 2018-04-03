package com.lechuang.dalaba.view.activity.own;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.Extra;
import com.lechuang.dalaba.model.bean.TeamBean;
import com.lechuang.dalaba.presenter.CommonAdapter;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.OwnApi;
import com.lechuang.dalaba.view.defineView.MListView;
import com.lechuang.dalaba.view.defineView.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/10/6 15:42
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class MyTeamActivity extends AppCompatActivity {

    @BindView(R.id.tv_team_one)
    TextView tvTeamOne;
    @BindView(R.id.tv_team_two)
    TextView tvTeamTwo;
    @BindView(R.id.tv_team_none)
    TextView tvTeamNone;
    @BindView(R.id.common_nothing_data)
    LinearLayout nothingData;

    @BindView(R.id.sv_team_refresh)
    PullToRefreshScrollView refreshItem;
    private MListView lv_team;
    private Context mContext = MyTeamActivity.this;
    public int page = 1;
    private List<TeamBean.MineTeamBean.TeamSubBean> items = new ArrayList<>();
    private CommonAdapter<TeamBean.MineTeamBean.TeamSubBean> mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team);
        ButterKnife.bind(this);
        initView();
        getData();
    }

    public void initView() {
//        ((TextView) findViewById(R.id.tv_title)).setText("我的团队");
        lv_team = (MListView) findViewById(R.id.lv_team);
        refreshItem.setOnRefreshListener(refresh);
        refreshItem.setMode(PullToRefreshBase.Mode.BOTH);
        refreshItem.onRefreshComplete();

    }

    @OnClick({R.id.iv_team_back, R.id.common_nothing_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_team_back:
                finish();
                break;
            case R.id.common_nothing_data:
                page = 1;
                if (null != items) {
                    items.clear();
                }
                getData();
                break;
        }
    }

    public void getData() {
        Netword.getInstance().getApi(OwnApi.class)
                .mineTeam(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<TeamBean>(mContext) {
                    @Override
                    public void successed(TeamBean result) {
//                        Log.i("Result_successed", JSON.toJSONString(result));
                        if (result == null) {
                            nothingData.setVisibility(View.VISIBLE);
                            if (mAdapter != null)
                                mAdapter.notifyDataSetChanged();
                            refreshItem.onRefreshComplete();
                            return;
                        }
                        TeamBean.MineTeamBean mData = result.record;
                        tvTeamOne.setText(mData.members1 + "人");
                        tvTeamTwo.setText(mData.members2 + "人");
                        tvTeamNone.setText(mData.members3 + "人");
                        List<TeamBean.MineTeamBean.TeamSubBean> mList = mData.list;
                        refreshItem.setMode(mList.size() > 0 ? PullToRefreshBase.Mode.BOTH : PullToRefreshBase.Mode.PULL_FROM_START);
                        if (page == 1 && (mList.toString().equals("[]") || mList.size() <= 0)) {
                            nothingData.setVisibility(View.VISIBLE);
                            if (mAdapter != null)
                                mAdapter.notifyDataSetChanged();
                            refreshItem.onRefreshComplete();
                            return;
                        }
                        nothingData.setVisibility(View.GONE);
                        if (items.size() > 0 && mList.toString().equals("[]")) {
                            Toast.makeText(mContext,"亲!已经到底了",Toast.LENGTH_SHORT).show();
                            refreshItem.onRefreshComplete();
                            return;
                        }
                        int size = mList.size();
                        for (int i = 0; i < size; i++) {
                            items.add(mList.get(i));
                        }
                        refreshItem.onRefreshComplete();

                        if (1 == page) {
                            mAdapter = new CommonAdapter<TeamBean.MineTeamBean.TeamSubBean>(mContext, items, R.layout.myteam_item) {
                                @Override
                                public void setData(ViewHolder viewHolder, Object item) {
                                    final TeamBean.MineTeamBean.TeamSubBean mItem = (TeamBean.MineTeamBean.TeamSubBean) item;
                                    // 昵称
                                    viewHolder.setText(R.id.tv_team_nickname, mItem.nickname);
                                    // 近3个月贡献
                                    viewHolder.setText(R.id.tv_team_contribute, mItem.contribution3);
                                    // 成员数量`
                                    viewHolder.setText(R.id.tv_team_amount, "0".equals(mItem.nextAgentCount) ? "无" : mItem.nextAgentCount);
                                    // 操作  代理:有背景+白色12文字  非代理:无背景+84色值14字体
                                    TextView tvTeamSub = viewHolder.getView(R.id.tv_team_sub);
                                    if ("1".equals(mItem.isAgencyStatus)) {
                                        tvTeamSub.setText("查看成员");
                                        tvTeamSub.setTextSize(12);
                                        tvTeamSub.setTextColor(mContext.getResources().getColor(R.color.white));
                                        tvTeamSub.setBackground(mContext.getResources().getDrawable(R.drawable.ic_team_look));
                                        tvTeamSub.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
//                                                Toast.makeText(mContext, "userId = " + mItem.userId, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(mContext, MySecondTeamActivity.class);
                                                intent.putExtra(Extra.USER_ID, mItem.userIdStr);
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        tvTeamSub.setText("普通会员");
                                        tvTeamSub.setTextSize(14);
                                        tvTeamSub.setTextColor(mContext.getResources().getColor(R.color.c_848484));
                                        tvTeamSub.setBackground(null);
                                    }

                                }
                            };
                            lv_team.setAdapter(mAdapter);
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
