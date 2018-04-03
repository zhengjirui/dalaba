package com.lechuang.dalaba.view.activity.agent;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseFragment;
import com.lechuang.dalaba.model.LeCommon;
import com.lechuang.dalaba.model.bean.OwnMyAgentBean;
import com.lechuang.dalaba.presenter.CommonAdapter;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.OwnApi;
import com.lechuang.dalaba.view.activity.own.ApplyAgentActivity;
import com.lechuang.dalaba.view.activity.own.MyTeamActivity;
import com.lechuang.dalaba.view.activity.own.ShareMoneyActivity;
import com.lechuang.dalaba.view.activity.ui.LoginActivity;
import com.lechuang.dalaba.view.defineView.MListView;
import com.lechuang.dalaba.view.defineView.ViewHolder;
import com.lechuang.dalaba.view.defineView.XCRoundImageView;

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
 * 修改备注: yrj  修改网络请求  无网络状态
 */
public class AgentFragment extends BaseFragment {
    @BindView(R.id.iv_img)
    XCRoundImageView ivImg;
    @BindView(R.id.btn_invite)
    Button btnInvite;
    @BindView(R.id.tv_all)
    TextView tvAll;
    @BindView(R.id.tv_dlsr)
    TextView tvDlsr;
    @BindView(R.id.tv_tixian)
    TextView tvTixian;
    @BindView(R.id.tv_fx)
    TextView tvFx;
    @BindView(R.id.tv_more)
    TextView tvMore;
    @BindView(R.id.mlv_team)
    MListView mlvTeam;
    @BindView(R.id.sv_agent)
    ScrollView svAgent;
    @BindView(R.id.go_agent)
    TextView goAgent;
    @BindView(R.id.ll_applyAgent)
    LinearLayout llApplyAgent;
    Unbinder unbinder;
    SharedPreferences se;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_my_agent, container, false);
        unbinder = ButterKnife.bind(this, inflate);
        mlvTeam.setFocusable(false);
        svAgent.scrollTo(0, 0);
        return inflate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(receiver, new IntentFilter(LeCommon.ACTION_APPLY_AGENT_SUCCESS));
    }

    @Override
    public void onResume() {
        super.onResume();
        intView();
    }


    //代理申请成功广播通知
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (LeCommon.ACTION_APPLY_AGENT_SUCCESS.equals(action)) {
                //申请代理成功
                svAgent.setVisibility(View.VISIBLE);
                llApplyAgent.setVisibility(View.GONE);
                getData();

            }
        }
    };


    private void intView() {
        se = PreferenceManager.getDefaultSharedPreferences(getActivity());
        svAgent.scrollTo(0, 0);

        if (se.getBoolean(LeCommon.KEY_HAS_LOGIN, false)) {
            if (se.getInt("isAgencyStatus", 0) == 0) {
                svAgent.setVisibility(View.GONE);
                llApplyAgent.setVisibility(View.VISIBLE);
            } else {
                svAgent.setVisibility(View.VISIBLE);
                llApplyAgent.setVisibility(View.GONE);
                getData();
            }
        } else {
            svAgent.setVisibility(View.GONE);
            llApplyAgent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_invite, R.id.tv_more, R.id.go_agent})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_more: //查看更多
                startActivity(new Intent(getActivity(), MyTeamActivity.class));
                break;
            case R.id.btn_invite:  //邀请好友
                startActivity(new Intent(getActivity(), ShareMoneyActivity.class));
                break;
            case R.id.go_agent:
                if (se.getBoolean(LeCommon.KEY_HAS_LOGIN, false)) {
                    startActivity(new Intent(getActivity(), ApplyAgentActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }

                break;
        }
    }

    private List<OwnMyAgentBean.RecordBean.ListBean> list;
    private CommonAdapter<OwnMyAgentBean.RecordBean.ListBean> mAdapter;

    public void getData() {
        Netword.getInstance().getApi(OwnApi.class)
                .agentInfo(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnMyAgentBean>(getActivity()) {
                    @Override
                    public void successed(OwnMyAgentBean result) {
                        if (result.record.photo != null)
                            Glide.with(getActivity()).load(result.record.photo).into(ivImg);
                        //总收入
                        if (result.record.sumIncome != null)
                            tvAll.setText(result.record.sumIncome + "积分");
                        //代理收入
                        if (result.record.agencyMoney != null)
                            tvDlsr.setText(result.record.agencyMoney + "积分");
                        //已提现收入
                        if (result.record.withdrawMoney != null)
                            tvTixian.setText(result.record.withdrawMoney + "积分");
                        //返现收入
                        if (result.record.returnMoney != null)
                            tvFx.setText(result.record.returnMoney + "积分");
                        //下级数据
                        list = result.record.list;
                        mAdapter = new CommonAdapter<OwnMyAgentBean.RecordBean.ListBean>(getActivity(), list, R.layout.myteam_item) {
                            @Override
                            public void setData(ViewHolder viewHolder, Object item) {
                                if (((OwnMyAgentBean.RecordBean.ListBean) item).photo != null)
                                    viewHolder.displayImage(R.id.iv_img, ((OwnMyAgentBean.RecordBean.ListBean) item).photo);
                                //昵称
                                viewHolder.setText(R.id.tv_name, ((OwnMyAgentBean.RecordBean.ListBean) item).nickname);
                                //个人收入
//                                viewHolder.setText(R.id.tv_nextNum, "下级人数:" + ((OwnMyAgentBean.RecordBean.ListBean) item).nextAgentCount);
                                //加入时间
                                viewHolder.setText(R.id.tv_time, "加入时间:" + ((OwnMyAgentBean.RecordBean.ListBean) item).joinTime);
                            }
                        };
                        mlvTeam.setAdapter(mAdapter);

                    }
                });
    }

}
