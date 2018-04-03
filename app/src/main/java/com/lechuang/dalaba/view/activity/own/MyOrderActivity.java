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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.bean.OrderBean;
import com.lechuang.dalaba.presenter.CommonAdapter;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.OwnApi;
import com.lechuang.dalaba.view.defineView.MListView;
import com.lechuang.dalaba.view.defineView.SpannelTextView;
import com.lechuang.dalaba.view.defineView.ViewHolder;
import com.lechuang.dalaba.view.dialog.FlippingLoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyOrderActivity extends AppCompatActivity {

    @BindView(R.id.line_order_all)
    View lineAll;
    @BindView(R.id.line_order_valid)
    View lineValid;
    @BindView(R.id.line_order_invalid)
    View lineInvalid;

    @BindView(R.id.show_order_valid)
    LinearLayout showValidOrder;
    @BindView(R.id.tv_order_pay)
    TextView tvPay;
    @BindView(R.id.tv_order_send)
    TextView tvSend;
    @BindView(R.id.tv_order_end)
    TextView tvEnd;
    @BindView(R.id.common_nothing_data)
    LinearLayout nothingData;

    @BindView(R.id.sv_order_refresh)
    PullToRefreshScrollView refreshItem;
    @BindView(R.id.lv_order)
    MListView mListView;

    private Context mContext = MyOrderActivity.this;
    private boolean isFinish = false;
    public int page = 1;
    public int type = 0; // (不传返回所有订单)type  1 所有有效 2 已付款 3 已收货 4 已结算 5 已失效
    private List<OrderBean.OrderDetail> items = new ArrayList<>();
    private CommonAdapter<OrderBean.OrderDetail> mAdapter;


    private boolean _isVisible;
    protected boolean fullScreen = false;
    private FlippingLoadingDialog waitDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        ButterKnife.bind(this);

        initView();
        getData();
    }

    private void initView() {
        mListView = (MListView) findViewById(R.id.lv_order);
        refreshItem.setOnRefreshListener(refresh);
        refreshItem.setMode(PullToRefreshBase.Mode.BOTH);
        refreshItem.onRefreshComplete();
        _isVisible = true;

        nothingData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != items && !items.isEmpty())
                    items.clear();
                page = 1;
                getData();
            }
        });
    }

    @OnClick({R.id.iv_order_back, R.id.tv_order_pay, R.id.tv_order_send, R.id.tv_order_end,
            R.id.tab_order_all, R.id.tab_order_valid, R.id.tab_order_invalid})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_order_back:
                isFinish = true;
                finish();
                break;
            //  所有订单
            case R.id.tab_order_all:
                type = 0;
                lineAll.setVisibility(View.VISIBLE);
                lineValid.setVisibility(View.INVISIBLE);
                lineInvalid.setVisibility(View.INVISIBLE);
                showValidOrder.setVisibility(View.GONE);
                break;
            //  有效订单
            case R.id.tab_order_valid:
                type = 1;
                lineAll.setVisibility(View.INVISIBLE);
                lineValid.setVisibility(View.VISIBLE);
                lineInvalid.setVisibility(View.INVISIBLE);
                // 显示已付款/已收货/已结算
                showValidOrder.setVisibility(View.VISIBLE);
                tvPay.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvSend.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvEnd.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvPay.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                tvSend.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                tvEnd.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                break;
            //  无效订单
            case R.id.tab_order_invalid:
                type = 5;
                lineAll.setVisibility(View.INVISIBLE);
                lineValid.setVisibility(View.INVISIBLE);
                lineInvalid.setVisibility(View.VISIBLE);
                showValidOrder.setVisibility(View.GONE);
                break;
            case R.id.tv_order_pay:
                //  已付款
                type = 2;
                tvPay.setTextColor(mContext.getResources().getColor(R.color.white));
                tvSend.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvEnd.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvPay.setBackground(mContext.getResources().getDrawable(R.drawable.btn_orderstate_pay));
                tvSend.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                tvEnd.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                break;
            case R.id.tv_order_send:
                //  已收货
                type = 3;
                tvPay.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvSend.setTextColor(mContext.getResources().getColor(R.color.white));
                tvEnd.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvPay.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                tvSend.setBackground(mContext.getResources().getDrawable(R.drawable.btn_orderstate_send));
                tvEnd.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                break;
            case R.id.tv_order_end:
                //  已结算
                type = 4;
                tvPay.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvSend.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvEnd.setTextColor(mContext.getResources().getColor(R.color.white));
                tvPay.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                tvSend.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                tvEnd.setBackground(mContext.getResources().getDrawable(R.drawable.btn_orderstate_end));
                break;
        }
        if (!isFinish) {
            page = 1;
            if (null != items && !items.isEmpty()) {
                items.clear();
            }
            getData();
        }
    }

    private void getData() {
        showWaitDialog("");
        Netword.getInstance().getApi(OwnApi.class)
                .orderDetails(type, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OrderBean>(mContext) {
                    @Override
                    public void successed(OrderBean result) {
                        Log.i("Result_successed", JSON.toJSONString(result));
                        if (result == null) {
                            nothingData.setVisibility(View.VISIBLE);
                            if (mAdapter != null)
                                mAdapter.notifyDataSetChanged();
                            refreshItem.onRefreshComplete();
                            return;
                        }
                        List<OrderBean.OrderDetail> mList = result.orderList;
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
                            mAdapter = new CommonAdapter<OrderBean.OrderDetail>(mContext, items, R.layout.item_order_details) {
                                @Override
                                public void setData(ViewHolder viewHolder, Object item) {
                                    final OrderBean.OrderDetail mItem = (OrderBean.OrderDetail) item;
                                    // 商品图片
                                    viewHolder.displayImage(R.id.iv_orderDetail_img, mItem.img);
                                    // 店铺类型(1 淘宝 2天猫)
                                    ((SpannelTextView) viewHolder.getView(R.id.stv_orderDetail_title)).setShopType(mItem.shopType == null ? 1 : Integer.parseInt(mItem.shopType));
                                    ((SpannelTextView) viewHolder.getView(R.id.stv_orderDetail_title)).setDrawText(mItem.goodsMsg);
                                    // 店铺名称：
                                    viewHolder.setText(R.id.tv_orderDetail_shop, "店铺名称：" + mItem.shopName);
                                    // 订单状态
                                    TextView tvOrderState = viewHolder.getView(R.id.tv_orderDetail_state);
                                    tvOrderState.setText(mItem.orderStatus);
                                    if ("已付款".equals(mItem.orderStatus)) {
                                        // 已付款红色背景
                                        tvOrderState.setBackground(mContext.getResources().getDrawable(R.drawable.btn_orderstate_pay));
                                    } else if ("已收货".equals(mItem.orderStatus)) {
                                        // 已付款蓝色背景
                                        tvOrderState.setBackground(mContext.getResources().getDrawable(R.drawable.btn_orderstate_send));
                                    } else if ("已结算".equals(mItem.orderStatus)) {
                                        // 已付款绿色是背景
                                        tvOrderState.setBackground(mContext.getResources().getDrawable(R.drawable.btn_orderstate_end));
                                    } else if ("已失效".equals(mItem.orderStatus)) {
                                        // 已付款灰色背景
                                        tvOrderState.setBackground(mContext.getResources().getDrawable(R.drawable.btn_orderstate_invalid));
                                    }
                                    // 付款金额
                                    viewHolder.setText(R.id.tv_orderDetail_pay, "¥" + mItem.payClearingMoney);
                                    // 预估收入
                                    viewHolder.setText(R.id.tv_orderDetail_forecast, "¥" + mItem.payClearingIncome);
                                    // 提成
                                    viewHolder.setText(R.id.tv_orderDetail_deduct, mItem.commisiom);
                                    // 来源
                                    viewHolder.setText(R.id.tv_orderDetail_source, mItem.source);
                                    // 订单创建时间
                                    viewHolder.setText(R.id.tv_create_time, mItem.createTime + "  创建");
                                    TextView tvClearTime = viewHolder.getView(R.id.tv_clear_time);
                                    if ("已结算".equals(mItem.orderStatus)) {
                                        // 订单结算时间
                                        tvClearTime.setVisibility(View.VISIBLE);
                                        tvClearTime.setText(mItem.clearingTime + "  结算");
                                    } else {
                                        tvClearTime.setVisibility(View.GONE);
                                    }

                                }
                            };
                            mListView.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        hideWaitDialog();
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
            refreshItem.onRefreshComplete();
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


    public FlippingLoadingDialog showWaitDialog(String message) {
        if (_isVisible) {
            if (waitDialog == null) {
                waitDialog = new FlippingLoadingDialog(this, message);
            }
            if (waitDialog != null) {
                waitDialog.setText(message);
                waitDialog.show();
            }
            return waitDialog;
        }
        return null;
    }

    public void hideWaitDialog() {
        if (_isVisible && waitDialog != null) {
            try {
                if (waitDialog.isShowing())
                    waitDialog.dismiss();
                waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
