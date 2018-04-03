package com.lechuang.dalaba.view.activity.tipoff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseFragment;
import com.lechuang.dalaba.model.LeCommon;
import com.lechuang.dalaba.model.bean.TipoffShowBean;
import com.lechuang.dalaba.presenter.CommonAdapter;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.TipoffShowApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.activity.ui.LoginActivity;
import com.lechuang.dalaba.view.defineView.MListView;
import com.lechuang.dalaba.view.defineView.ViewHolder;

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
public class BaoLiaoFragment extends BaseFragment {


    @BindView(R.id.iv_tryAgain)
    ImageView ivTryAgain;
    @BindView(R.id.ll_noNet)
    LinearLayout llNoNet;
    @BindView(R.id.lv_tipoff)
    MListView lvTipoff;
    Unbinder unbinder;
    private SharedPreferences sp;
    private PullToRefreshScrollView refreshScrollView;
    private View inflate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_baoliao, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        unbinder = ButterKnife.bind(this, inflate);
        tipList = new ArrayList<>();
        initView();
        initDate();
        return inflate;
    }

    private void initView() {
        refreshScrollView = (PullToRefreshScrollView) inflate.findViewById(R.id.refresh);
        refreshScrollView.setOnRefreshListener(refresh);
        refreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
    }


    private List<TipoffShowBean.ListBean> tipList;
    private CommonAdapter<TipoffShowBean.ListBean> mAdapter;

    private void initDate() {
        showWaitDialog("");
        if (!Utils.isNetworkAvailable(getActivity())) {
            hideWaitDialog();
            llNoNet.setVisibility(View.VISIBLE);
            refreshScrollView.setVisibility(View.GONE);
            Utils.show(getActivity(), getString(R.string.net_error));
            return;
        }
        llNoNet.setVisibility(View.GONE);
        refreshScrollView.setVisibility(View.VISIBLE);
        Netword.getInstance().getApi(TipoffShowApi.class)
                .getTipoffs(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<TipoffShowBean>(getActivity()) {
                    @Override
                    public void successed(TipoffShowBean result) {
                        TipoffShowBean request = result;
                        if (request == null) {
                            return;
                        }
                        List<TipoffShowBean.ListBean> list = request.list;
                        if (page != 1 && list.toString().equals("[]")) {            //数据没有了
                            Utils.show(getActivity(), "亲!已经到底了");
                            hideWaitDialog();
                            refreshScrollView.onRefreshComplete();
                            return;
                        }

                        if (page == 1) {                 //加载第一页
                            if (tipList != null)
                                tipList.clear();
                            tipList.addAll(list);
                            mAdapter = new CommonAdapter<TipoffShowBean.ListBean>(getActivity(), tipList, R.layout.item_baoliao) {
                                @Override
                                public void setData(ViewHolder viewHolder, Object item) {
                                    TipoffShowBean.ListBean bean = (TipoffShowBean.ListBean) item;
                                    viewHolder.displayRoundImage(R.id.iv_tipoff, bean.img);
                                    viewHolder.setText(R.id.title_tipoff, bean.title);
                                    viewHolder.setText(R.id.tv_comment, bean.appraiseCount + "");
                                    viewHolder.setText(R.id.tv_dianzan, bean.praiseCount + "");
                                }
                            };
                            lvTipoff.setAdapter(mAdapter);
                        } else {
                            tipList.addAll(list);

                        }
                        lvTipoff.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(LeCommon.KEY_HAS_LOGIN, false)) {
                                    Intent intent = new Intent(getActivity(), StoryDetailActivity.class);
                                    intent.putExtra("id", tipList.get(position).id);
                                    startActivity(intent);
                                } else {
                                    startActivity(new Intent(getActivity(), LoginActivity.class));
                                }
                            }
                        });
                        mAdapter.notifyDataSetChanged();
                        hideWaitDialog();
                        refreshScrollView.onRefreshComplete();
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.iv_tryAgain)
    public void onViewClicked() {
        page = 1;
        initDate();
    }


    private int page = 1;
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
            // 模拟加载任务
            initDate();
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
            initDate();
            refreshScrollView.onRefreshComplete();
        }
    };
}
