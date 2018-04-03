package com.lechuang.dalaba.view.activity.tipoff;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.model.bean.ResultBean;
import com.lechuang.dalaba.model.bean.TipoffShowBean;
import com.lechuang.dalaba.presenter.CommonAdapter;
import com.lechuang.dalaba.presenter.adapter.MyAdapter;
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
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/9/28 20:55
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class TipoffBaseFragmentPubu extends BaseFragment implements AdapterView.OnItemClickListener {
    @BindView(R.id.mlv_tipoff)
    MListView mlvTipoff;
    Unbinder unbinder;
    //商品
    public PullToRefreshScrollView refreshScrollView;
    //当前页数
    public int page = 1;

    //搜索商品内容
    public String content = "";
    //数据集合
    ArrayList<TipoffShowBean.ListBean> newsList = new ArrayList<>();
    @BindView(R.id.ll_noNet)
    LinearLayout llNoNet; //没有网络
    @BindView(R.id.iv_tryAgain)
    ImageView tryAgain;
    private LocalSession mSession;
    private ResultBean<TipoffShowBean> request;
    private int conditionType;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    MyAdapter myAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tipoff_comment_pubu, container, false);
        unbinder = ButterKnife.bind(this, view);
        refreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.refresh);
        isPrepared = true;
        conditionType = getArguments().getInt("conditionType");
        initView();
        //访问网络，获取数据
        initData();
        return view;
    }


    public void initView() {
        recyclerView.setHasFixedSize(true); // 设置固定大小
        recyclerView.setNestedScrollingEnabled(false);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        refreshScrollView.setOnRefreshListener(refresh);
        mlvTipoff.setOnItemClickListener(this);
        mSession = LocalSession.get(getActivity());
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
    }

    public void initData() {
        if (!isPrepared || !isVisable) {  //加载数据判断是否可见，进行懒加载
            return;
        }
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
                .getTipoff(content, conditionType, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
        // hideWaitDialog();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isPrepared = false;
        unbinder.unbind();
    }

    public PullToRefreshBase.OnRefreshListener2 refresh = new PullToRefreshBase.OnRefreshListener2() {
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
    //数据适配
    public CommonAdapter<TipoffShowBean.ListBean> commonAdapter;


    //处理信息的handler
    ResultBack handler = new ResultBack<TipoffShowBean>(getActivity()) {
        @Override
        public void successed(TipoffShowBean result) {
            TipoffShowBean request = result;
            if (request == null) {
                return;
            }
            List<TipoffShowBean.ListBean> list = request.list;
            refreshScrollView.setMode(list.size() > 0 ? PullToRefreshBase.Mode.BOTH : PullToRefreshBase.Mode.PULL_FROM_START);
            if (page != 1 && list.toString().equals("[]")) {            //数据没有了
                Utils.show(getActivity(), "亲!已经到底了");
                hideWaitDialog();
                refreshScrollView.onRefreshComplete();
                return;
            }
//            for (int i = 0; i < list.size(); i++) {     //数据加入
//                 newsList.add(list.get(i));
//            }
            if (page == 1) {                 //加载第一页
                newsList.clear();
                newsList.addAll(list);
                commonAdapter = new CommonAdapter<TipoffShowBean.ListBean>(getActivity(), newsList, R.layout.tipoff_item) {
                    @Override
                    public void setData(ViewHolder viewHolder, Object item) {
                        TipoffShowBean.ListBean bean = (TipoffShowBean.ListBean) item;
                        viewHolder.displayImage(R.id.iv_tipoff, bean.img);
                        viewHolder.setText(R.id.title_tipoff, bean.title);
                        viewHolder.displayImage(R.id.iv_tipoff_photo, bean.photo);
                        viewHolder.setText(R.id.user_name, bean.nickName);
                        viewHolder.setText(R.id.tv_comment, bean.pageViews + "");
                        viewHolder.setText(R.id.tv_dianzan, bean.praiseCount + "");
                    }
                };
                myAdapter = new MyAdapter(newsList, new MyAdapter.RecycleViewListener() {
                    @Override
                    public void itemClik(TipoffShowBean.ListBean listBean) {
                        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(LeCommon.KEY_HAS_LOGIN, false)) {
                            Intent intent = new Intent(getActivity(), StoryDetailActivity.class);
                            intent.putExtra("id", listBean.id);
                            startActivity(intent);
                        } else {
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                        }
                    }
                });
                recyclerView.setAdapter(myAdapter);
                mlvTipoff.setAdapter(commonAdapter);
            } else {
                newsList.addAll(list);
                myAdapter.addData(newsList);
            }
            myAdapter.notifyDataSetChanged();
            commonAdapter.notifyDataSetChanged();
            hideWaitDialog();
        }
    };


    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/29  15:38
     * @describe 列表条目展示
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(LeCommon.KEY_HAS_LOGIN, false)) {
            Intent intent = new Intent(getActivity(), StoryDetailActivity.class);
            intent.putExtra("id", newsList.get(position).id);
            startActivity(intent);
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    //是否可见
    protected boolean isVisable;
    // 标志位，标志Fragment已经初始化完成。
    public boolean isPrepared = false;

    /**
     * 实现Fragment数据的缓加载 * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisable = true;
            //初始化完成，切处于可见状态
            onVisible();
        } else {
            isVisable = false;
            onInVisible();
        }
    }

    protected void onInVisible() {

    }

    protected void onVisible() {
        initData();
    }

}

