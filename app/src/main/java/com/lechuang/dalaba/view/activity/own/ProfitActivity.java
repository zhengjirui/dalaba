package com.lechuang.dalaba.view.activity.own;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseActivity;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.model.bean.MyIncomeBean;
import com.lechuang.dalaba.presenter.CommonAdapter;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.MListView;
import com.lechuang.dalaba.view.defineView.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * @author yrj
 * @date 2017/10/10
 * @E-mail 1422947831@qq.com
 * @desc 积分明细
 */
public class ProfitActivity extends BaseActivity {
    private Context mContext = ProfitActivity.this;
    //可刷新的listView
    private MListView lv_profit;
    //我的收益实体类
    //private MyIncome myIncome;
    //最近收益列表适配器
    private CommonAdapter<MyIncomeBean.RecordBean.ListBean> mAdapter;
    //最近收益数据集合
    private List<MyIncomeBean.RecordBean.ListBean> incomeList;
    //页数
    private int page = 1;
    //刷新
    private PullToRefreshScrollView refreshScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        incomeList = new ArrayList<>();
        getData();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_profit;
    }

    @Override
    protected void initTitle() {
        ((TextView) findViewById(R.id.tv_title)).setText("积分账单");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        lv_profit = (MListView) findViewById(R.id.lv_profit);
        refreshScrollView = (PullToRefreshScrollView) findViewById(R.id.sv);
        findViewById(R.id.iv_back).setVisibility(View.GONE);
        findViewById(R.id.iv_back2).setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        refreshScrollView.onRefreshComplete();
        refreshScrollView.setOnRefreshListener(refresh);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //网络请求获取数据
    private void getData() {
        showWaitDialog("");
        if (Utils.isNetworkAvailable(mContext)) {
            Netword.getInstance().getApi(CommenApi.class)
                    //这里不传page
                    .myIncome("" + page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<MyIncomeBean>(this) {
                        @Override
                        public void successed(MyIncomeBean result) {
                            if (result == null)
                                return;
                            //积分收益列表数据
                            List<MyIncomeBean.RecordBean.ListBean> list = result.record.list;
                            refreshScrollView.setMode(list.size() > 0 ? PullToRefreshBase.Mode.BOTH : PullToRefreshBase.Mode.PULL_FROM_START);
                            if (list.toString().equals("[]")) {
                                showShortToast("亲!已经到底了");
                                hideWaitDialog();
                                //dismissLoadingDialog();
                                refreshScrollView.onRefreshComplete();
                                return;
                            }
                            incomeList.addAll(list);
                            if (page == 1) {
                                mAdapter = new CommonAdapter<MyIncomeBean.RecordBean.ListBean>(mContext, incomeList, R.layout.item_profit) {
                                    @Override
                                    public void setData(ViewHolder viewHolder, Object item) {
                                        MyIncomeBean.RecordBean.ListBean bean = (MyIncomeBean.RecordBean.ListBean) item;
                                        //收益中文描述
                                        viewHolder.setText(R.id.tv_income, bean.typeStr);
                                        //时间
                                        viewHolder.setText(R.id.tv_time, bean.createTimeStr);
                                        //积分明细
                                        viewHolder.setText(R.id.tv_integral, bean.integralDetailsStr);
                                        //积分明细
                                        /*Double integralDetails = Double.parseDouble(bean.integralDetailsStr);
                                        if (integralDetails > 0) {  //正积分 加上正号
                                            viewHolder.setText(R.id.tv_integral, "+" + bean.integralDetailsStr);
                                        } else {       //负积分  不处理
                                            viewHolder.setText(R.id.tv_integral, bean.integralDetailsStr);
                                        }*/
                                    }
                                };
                                lv_profit.setAdapter(mAdapter);
                            } else {
                                mAdapter.notifyDataSetChanged();
                            }
                            refreshScrollView.onRefreshComplete();
                            hideWaitDialog();
                        }
                    });
        } else {
            refreshScrollView.onRefreshComplete();
            hideWaitDialog();
            showShortToast(getString(R.string.net_error));
        }
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
            if (null != incomeList) {
                incomeList.clear();
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
