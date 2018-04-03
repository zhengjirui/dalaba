package com.lechuang.dalaba.view.activity.own;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseActivity;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.model.bean.OwnNewsListBean;
import com.lechuang.dalaba.presenter.CommonAdapter;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.netApi.OwnApi;
import com.lechuang.dalaba.view.defineView.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author yrj
 * @date 2017/10/10
 * @E-mail 1422947831@qq.com
 * @desc 消息
 */
public class NewsActivity extends BaseActivity {
    private Context mContext = NewsActivity.this;
    private PullToRefreshListView lv_news;
    private int page = 1;
    //实体类
    //private News news;
    //消息数据
    private List<OwnNewsListBean.ListBean> newsList;
    private CommonAdapter<OwnNewsListBean.ListBean> mAdapter;
    private LinearLayout nothingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //消息数据集合
        newsList = new ArrayList<>();
        getData();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_news;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.tv_title)).setText("消息");
        findViewById(R.id.iv_back).setVisibility(View.GONE);
        findViewById(R.id.iv_back2).setVisibility(View.VISIBLE);
        nothingData = (LinearLayout) findViewById(R.id.common_nothing_data);
        lv_news = (PullToRefreshListView) findViewById(R.id.lv_news);
        lv_news.onRefreshComplete();
        lv_news.setOnRefreshListener(refresh);
        nothingData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                if (null != newsList)
                    newsList.clear();
                getData();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    //联网获取数据
    private void getData() {
        showWaitDialog("");
        Netword.getInstance().getApi(OwnApi.class)
                .allNws(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnNewsListBean>(mContext) {
                    @Override
                    public void successed(OwnNewsListBean result) {
                        if (result == null) {
                            nothingData.setVisibility(View.VISIBLE);
                            if (mAdapter != null)
                                mAdapter.notifyDataSetChanged();
                            lv_news.onRefreshComplete();
                            return;
                        }
                        List<OwnNewsListBean.ListBean> list = result.list;
                        lv_news.setMode(list.size() > 0 ? PullToRefreshBase.Mode.BOTH : PullToRefreshBase.Mode.PULL_FROM_START);
                        if (page == 1 && (list.toString().equals("[]") || list.size() <= 0)) {
                            nothingData.setVisibility(View.VISIBLE);
                            if (mAdapter != null)
                                mAdapter.notifyDataSetChanged();
                            lv_news.onRefreshComplete();
                            return;
                        }
                        if (list.toString().equals("[]")) {
                            showShortToast("亲!已经到底了");
                            lv_news.onRefreshComplete();
                            return;
                        }
                        nothingData.setVisibility(View.GONE);
                        newsList.addAll(list);
                        if (page == 1) {
                            mAdapter = new CommonAdapter<OwnNewsListBean.ListBean>(mContext, newsList, R.layout.item_news) {
                                @Override
                                public void setData(ViewHolder viewHolder, Object item) {
                                    OwnNewsListBean.ListBean bean = (OwnNewsListBean.ListBean) item;
                                    viewHolder.setText(R.id.tv_time, bean.createTimeStr);
                                    viewHolder.setText(R.id.tv_content, bean.content);
                                    viewHolder.setText(R.id.tv_title, "消息通知");
                                }
                            };
                            lv_news.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                        lv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                startActivity(new Intent(mContext, NewsDetailsActivity.class)
                                        .putExtra("time", newsList.get(position - 1).createTimeStr)
                                        .putExtra("content", newsList.get(position - 1).content));
                                //清空集合
                                //newsList.clear();
                            }
                        });
                        lv_news.onRefreshComplete();

                    }
                });
        hideWaitDialog();
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
            if (null != newsList) {
                newsList.clear();
                //mAdapter.notifyDataSetChanged();
            }
            // 模拟加载任务
            getData();
            hideWaitDialog();
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
            hideWaitDialog();
        }
    };
}
